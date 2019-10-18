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

package jp.ac.osaka_u.padla;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.naist.heijo.Monitor;
import jp.naist.heijo.debug.DebugValue;

public class LevelChangerCombined extends Thread {
	static boolean isFirstLevel = true;
	static BufferedWriter bwVector = null;
	static FileWriter file = null;
	List<double[]> samplingData = new ArrayList<double[]>();

	static DebugMessage debugmessage = null;
	static CalcVectors calc = new CalcVectors();
	static MethodVector vec;
	Options options = null;
	PrevState ps;
	LearningData learningdata;
	private boolean isFirstData = true;
	int mode = 0;
	private boolean isNewData;
	private String messageHead = "[AGENT]:";

	public LevelChangerCombined(Options options, LearningData learningdata, int numOfMethods) {
		vec = new MethodVector(numOfMethods);
		this.options = options;
		debugmessage = new DebugMessage();
		ps = new PrevState();
		this.learningdata = learningdata;
		if (this.options.getMode().equals("Adapter")) {
			mode = 1;
		} else if (this.options.getMode().equals("Learning")) {
			mode = 2;
		}
		openOutputFiles(this.options);
		isNewData = false;
	}

	public void run() {

		while (true) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (isNewData) {
				Message message = new Message();
				message.setISFIRSTLEVEL(isUnkownPhase());
				socketWrite(message);
				isNewData = false;
			}
		}

	}

	private void socketWrite(Message message) {
		Monitor.getInstance().Scheduler.Counter = 0;
		Monitor.getInstance().Scheduler.SampleNumMap.clear();

		if (!(DebugValue.DEBUG_FLAG && DebugValue.DEBUG_NO_CONNECT)) {
			try {
				Monitor.getInstance().Connector.write(message);
			} catch (IOException e) {
				System.err.println(messageHead + "Connection is closed");
				Monitor.getInstance().Scheduler.Executor.shutdownNow();
			}
		}
	}

	public synchronized void updateSamplingData(MethodVector vector) {
		vec.setSumOfVectors(vector.getSumOfVectors());
		isNewData = true;

	}

	public synchronized boolean isUnkownPhase() {
		boolean result = false;
		vec.incCountSamaple();
		if (vec.getCountSample() == options.getInterval()) {
			vec.setNormalizedVector(calc.normalizeVector(vec.getSumOfVectors(), vec.getNumOfMethods()));
			if (isFirstData || calc
					.isUnknownPhase(vec.getNormalizedVector(), samplingData, vec.getNumOfMethods(), options.getEp())
					.isUnknownPhase()) {
				try {
					addSamplingData(vec.getNormalizedVector());
					//If mode == "Learning"
					if (mode == 2) {
						bwVector.write(Arrays.toString(vec.getNormalizedVector()) + "\n");
						bwVector.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				isFirstData = false;
			}
			vec.resetCountSample();
			result = adaptLogLevel(learningdata, vec.getNormalizedVector(), ps);
			calc.initArray(vec.getSumOfVectors());
			calc.initArray(vec.getNormalizedVector());
		}
		return result;
	}

	private void addSamplingData(double[] vector) {
		double[] cloneVector = new double[vec.getNumOfMethods()];
		cloneVector = vector.clone();
		samplingData.add(cloneVector);
	}

	public boolean isFirstLevel() {
		return isFirstLevel;
	}

	/**
	 * Check whether vectors is known or unknown and change "isFirstLevel" flag
	 * @param learningdata
	 * @param vectors
	 * @param ps
	 */
	private boolean adaptLogLevel(LearningData learningdata, double[] vectors, PrevState ps) {
		ResultOfPhaseDetection result = new ResultOfPhaseDetection();
		result = calc.isUnknownPhase(vectors, learningdata.getLearningData(), vec.getNumOfMethods(), options.getEp());
		//Compare to learningData
		if (result.isUnknownPhase()) {
			outputDebugLog(result, " <Unknown phase>\n");
			if (this.isFirstLevel()) {
				isFirstLevel = false;
				//If mode == "Adapter"
				if (mode == 1) {
				}
			}
			if (continuesIn2Intervals(ps, vectors)) {
				addLearningData(learningdata, ps, vectors);
			}
			return true;
		} else {
			outputDebugLog(result, " <Known phase>\n");
			if (!this.isFirstLevel()) {
				isFirstLevel = true;
			}
			return false;
		}
	}

	private void outputDebugLog(ResultOfPhaseDetection result, String phase) {
		try {
			if (file != null) {
				file.write(debugmessage.getMessagehead() + "Sim: " + result.getMaxSimilarity() + "  phaseNum: "
						+ result.getPhaseNum() + phase);
				file.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * It records the last vector of unknown phase that lasts two intervals as learning data.
	 * @param learningdata
	 * @param ps
	 * @param current
	 */
	private void addLearningData(LearningData learningdata, PrevState ps, double[] current) {
		double[] cloneCurrent = new double[vec.getNumOfMethods()];
		cloneCurrent = current.clone();
		learningdata.getLearningData().add(cloneCurrent);
		ps.refresh();
	}

	/**
	 * Judges if the current phase continues in 2 intervals
	 * @param ps
	 * @param current
	 * @return
	 */
	private boolean continuesIn2Intervals(PrevState ps, double[] current) {
		if (calc.calcInnerProduct(ps.get(), current, vec.getNumOfMethods()) > options.getEp()) {
			ps.incCount();
			ps.update(current);
			if (ps.getCount() >= 2) {
				return true;
			}
			return false;
		} else {
			ps.stayCount();
			ps.update(current);
			return false;
		}
	}

	private void openOutputFiles(Options options) {
		if (options.getPhaseoutput() != null) {
			if (mode == 2) {
				try {
					bwVector = new BufferedWriter(new FileWriter(new File(options.getPhaseoutput())));
				} catch (IOException e5) {
					e5.printStackTrace();
				}

			}
		}
		if (options.getDebugLogOutput() != null) {
			try {
				file = new FileWriter(options.getDebugLogOutput());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static class PrevState {
		private double[] prev;
		private int count;

		public PrevState() {
			prev = new double[vec.getNumOfMethods()];
			refresh();
		}

		/**
		 * 同じフェイズで連続する区間のカウントを1にする
		 */
		public void stayCount() {
			count = 1;
		}

		public void refresh() {
			calc.initArray(prev);
			count = 0;
		}

		public void update(double[] current) {
			prev = current.clone();
		}

		public double[] get() {
			return prev;
		}

		public void incCount() {
			count++;
		}

		public int getCount() {
			return count;
		}
	}
}
