/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package jp.naist.heijo.timer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import jp.ac.osaka_u.padla.LearningData;
import jp.ac.osaka_u.padla.LevelChangerCombined;
import jp.ac.osaka_u.padla.Message;
import jp.ac.osaka_u.padla.MethodVector;
import jp.ac.osaka_u.padla.Options;
import jp.naist.heijo.Monitor;
import jp.naist.heijo.debug.DebugValue;
import jp.naist.heijo.debug.IntervalPrinter;
import jp.naist.heijo.message.SamplingResult;
import jp.naist.heijo.util.Pair;

public class UpdateThread extends Thread {

	private boolean isFirstSend = true;

	private long before = -1;

	private IntervalPrinter debugIntervalPrinter = null;

	public Options options = null;

	private String messageHead = "[AGENT]:";
	private LevelChangerCombined levelchanger;
	private int numOfMethods;

	public UpdateThread(Options options) {
		if (DebugValue.DEBUG_FLAG && DebugValue.DEBUG_PRINT_UPDATE_INTERVAL_FLAG) {
			debugIntervalPrinter = new IntervalPrinter(DebugValue.DEBUG_PRINT_UPDATE_INTERVAL_TIME, "UPDATE");
		}
		this.options = options;
	}

	@Override
	public void run() {
		try {
			if (isFirstSend) {
				firstSend();
				isFirstSend = false;
			}
		} catch (Exception e) {
			System.err.println(e);
		}

		try {
			update();
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private void update() {
		if (DebugValue.DEBUG_FLAG && DebugValue.DEBUG_PRINT_UPDATE_INTERVAL_FLAG)
			debugIntervalPrinter.interval();

		SamplingResult samplingresult = new SamplingResult();
		MethodVector vector = new MethodVector(numOfMethods);
		Arrays.fill(vector.getSumOfVectors(), 0.0);
		synchronized (Monitor.getInstance().Scheduler.Lock) {
			samplingresult.CurrentTime = System.currentTimeMillis();

			// 前回のupdateとのIntervalを計算。初回時は固定時間を信じる
			if (before < 0) {
				samplingresult.TimeLength = Monitor.getInstance().Config.UpdateInterval;
			} else {
				long diff = samplingresult.CurrentTime - before;
				samplingresult.TimeLength = diff;
			}
			before = samplingresult.CurrentTime;

			for (Map.Entry<Pair<Integer, Long>, Integer> entry : Monitor.getInstance().Scheduler.SampleNumMap
					.entrySet()) {
				int methodID = entry.getKey().first();
				double exeRate = (double) entry.getValue() / Monitor.getInstance().Scheduler.Counter;
				double exeTime = exeRate * samplingresult.TimeLength;
				vector.add(methodID, exeTime);
			}

			//for LevelChanger
			levelchanger.updateSamplingData(vector);
		}
	}

	private void firstSend() {
		if (DebugValue.DEBUG_FLAG && DebugValue.DEBUG_NO_CONNECT)
			return;
		SamplingResult samplingresult = new SamplingResult();
		Message message = new Message();
		samplingresult.CurrentTime = 0;
		samplingresult.TimeLength = 0;
		samplingresult.Methods.addAll(Monitor.getInstance().StructureDB.IdDataMap.values());
		message.setBUFFEROUTPUT(options.getBufferoutput());
		message.setCACHESIZE(options.getCacheSize());
		numOfMethods = samplingresult.Methods.size();
		message.setNUMOFMETHODS(numOfMethods);
		LearningData learningdata = new LearningData(options, message.getNUMOFMETHODS(), options.getMode());
		this.levelchanger = new LevelChangerCombined(options, learningdata, message.getNUMOFMETHODS());
		this.levelchanger.start();
		try {
			Monitor.getInstance().Connector.write(message);
		} catch (IOException e) {
			System.err.println(messageHead + "Connection is closed");
			Monitor.getInstance().Scheduler.Executor.shutdownNow();
		}
	}

}
