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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.naist.heijo.message.Message;

public class LevelChangerCombined extends Thread {
	static boolean isFirstLevel = true;
	static String MODE = null;
	static BufferedWriter bwVector = null;
	static FileWriter file = null;
	List<double[]> samplingData = new ArrayList<double[]>();

	static DebugMessage debugmessage = null;
	static CalcVectors calc = new CalcVectors();
	static VectorOfAnInterval vec = new VectorOfAnInterval();
	Options options = null;
	PrevState ps;
	LearningData learningdata;
	private boolean isFirstData = true;

	public LevelChangerCombined(Options options, LearningData learningdata) {
		this.options = options;
		debugmessage = new DebugMessage();
		ps = new PrevState();
		this.learningdata = learningdata;
	}

	public void run(Message info) {
		Message message = info;

		// Data receive (after the second time)
		if (message.ExeTimes != null && 0 < message.ExeTimes.size()) {
			vec.setSumOfVectors(addSamplingDataToSumOfVectors(message, vec.getSumOfVectors()));
			vec.incCountSamaple();
			if (vec.getCountSample() == options.getInterval()) {
				vec.setNormalizedVector(calc.normalizeVector(vec.getSumOfVectors(), vec.getNumOfMethods()));
				if (isFirstData || calc
						.isUnknownPhase(vec.getNormalizedVector(), samplingData, vec.getNumOfMethods(), options.getEp())
						.isUnknownPhase()) {
					try {
						addSamplingData(vec.getNormalizedVector());
						if (MODE.equals("Learning")) {
							bwVector.write(Arrays.toString(vec.getNormalizedVector()) + "\n");
							bwVector.flush();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					isFirstData = false;
				}
				vec.resetCountSample();
				adaptLogLevel(learningdata, vec.getNormalizedVector(), ps);
				calc.initArray(vec.getSumOfVectors());
				calc.initArray(vec.getNormalizedVector());
			}
		}

	}

	private void addSamplingData(double[] vector) {
		double[] cloneVector = new double[vec.getNumOfMethods()];
		cloneVector = vector.clone();
		samplingData.add(cloneVector);
	}

	private Socket connect2Agent() {
		debugmessage.print("Waiting for Connection,,,");
		ServerSocket server = null;
		Socket socket = null;
		try {
			server = new ServerSocket(8000);
			socket = server.accept();
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		debugmessage.print("Connection Complete");
		return socket;
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
	private void adaptLogLevel(LearningData learningdata, double[] vectors, PrevState ps) {
		ResultOfPhaseDetection result = new ResultOfPhaseDetection();
		result = calc.isUnknownPhase(vectors, learningdata.getLearningData(), vec.getNumOfMethods(), options.getEp());
		//Compare to learningData
		if (result.isUnknownPhase()) {
			outputDebugLog(result, " <Unknown phase>\n");
			if (this.isFirstLevel()) {
				isFirstLevel = false;
				debugmessage.printOnDebug("Unknown Phase Detected!\n");
				debugmessage.printOnDebug("Logging Level Down\n↓↓↓↓↓↓↓↓");
				if (MODE.equals("Adapter")) {
				}
			}
			if (continuesIn2Intervals(ps, vectors)) {
				addLearningData(learningdata, ps, vectors);
			}
		} else {
			outputDebugLog(result, " <Known phase>\n");
			if (!this.isFirstLevel()) {
				isFirstLevel = true;
				debugmessage.printOnDebug("Returned to Normal Phase\n");
				debugmessage.printOnDebug("Logging Level Up\n↑↑↑↑↑↑↑↑");
			}
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
	private static void addLearningData(LearningData learningdata, PrevState ps, double[] current) {
		double[] cloneCurrent = new double[vec.getNumOfMethods()];
		cloneCurrent = current.clone();
		learningdata.getLearningData().add(cloneCurrent);
		ps.refresh();
		debugmessage.printOnDebug("Learned\n");
	}

	/**
	 * Judges if the current phase continues in 2 intervals
	 * @param ps
	 * @param current
	 * @return
	 */
	private static boolean continuesIn2Intervals(PrevState ps, double[] current) {
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

	/**
	 * Extract options from message
	 * @param message
	 */
	private static void firstReceive(Message message) {
		debugmessage.setISDEBUG(options.isISDEBUG());
		vec.setNumOfMethods(message.Methods.size());
		printDebugMessages();
		openOutputFiles();
	}

	private static void openOutputFiles() {
		if (options.getPHASEOUTPUT() != null) {
			if (MODE.equals("Learning")) {
				try {
					bwVector = new BufferedWriter(new FileWriter(new File(options.getPHASEOUTPUT())));
				} catch (IOException e5) {
					e5.printStackTrace();
				}

			}
		}
		if (options.getDEBUGLOGOUTPUT() != null) {
			try {
				file = new FileWriter(options.getDEBUGLOGOUTPUT());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * It extracts method execution times from message and add them to sumOfVectors
	 * @param message
	 * @param sumOfVectors
	 * @return
	 */
	static double[] addSamplingDataToSumOfVectors(Message message, double[] sumOfVectors) {
		double[] tmpArray = new double[vec.getNumOfMethods()];

		calc.initArray(tmpArray);

		for (int index = 0; index < message.ExeTimes.size(); index++) {
			if (tmpArray[message.ExeTimes.get(index).MethodID] < message.ExeTimes.get(index).ExeTime) { //If the method ID is the same but the thread ID is different, use the longer execution time
				tmpArray[message.ExeTimes.get(index).MethodID] = message.ExeTimes.get(index).ExeTime;
			}
		}

		for (int i = 0; i < vec.getNumOfMethods(); i++) {
			sumOfVectors[i] += tmpArray[i];
		}

		return sumOfVectors;
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
