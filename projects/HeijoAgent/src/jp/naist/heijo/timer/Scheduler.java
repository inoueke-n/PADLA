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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jp.naist.heijo.Monitor;
import jp.naist.heijo.util.Pair;

public class Scheduler
{

	public ScheduledExecutorService Executor = Executors.newScheduledThreadPool(2);

	Object Lock = new Object();

	public SampleThread Sampler;
	public UpdateThread Updater;

	// sampleした回数。updateでクリアされる
	public int Counter = 0;

	// <<メソッドID, スレッドID>, サンプル数>
	// samplerで書き込まれ、updaterで送信後クリアされる。同期用のlockerも兼ねる
	public Map<Pair<Integer, Long>, Integer> SampleNumMap = new HashMap<>();

	private static final int MILISEC_TO_NANOSEC = 1000000;

	public Scheduler(String learningData, String bufferoutput, String phaseoutput,int buffer, int interval, double ep)
	{
		Sampler = new SampleThread();
		Updater = new UpdateThread(learningData,bufferoutput,phaseoutput, buffer,interval, ep);
	}

	public void start()
	{
		long sInterval = (long)(Monitor.getInstance().Config.SampleInterval * MILISEC_TO_NANOSEC);
		long uInterval = (long)(Monitor.getInstance().Config.UpdateInterval * MILISEC_TO_NANOSEC);
		Executor.scheduleAtFixedRate(Sampler, sInterval, sInterval, TimeUnit.NANOSECONDS);
		Executor.scheduleAtFixedRate(Updater, uInterval, uInterval, TimeUnit.NANOSECONDS);
	}

}
