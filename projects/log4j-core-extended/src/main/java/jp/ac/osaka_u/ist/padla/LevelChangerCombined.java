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

package jp.ac.osaka_u.ist.padla;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import jp.naist.ogami.Connector;
import jp.naist.ogami.json.ExeTimeJson;
import jp.naist.ogami.message.Message;

public class LevelChangerCombined extends Thread{
	static double EP = 0; //Threshold used to phase detection
	static int INTERVAL = 5; // Length of one intervel. INTERVEL=1 -> 0.1s
	static String FILENAME = null;
	static MyLogCache mylogcache = null;
	static boolean isFirstLevel = true;
	static String MODE = null;
	static String OUTPUTFILENAME = null;
	static BufferedWriter bwVector = null;
	static boolean ISDEBUG = false;
	static String DEBUGLOGOUTPUT = null;
	List<double[]> samplingData = new ArrayList<double[]>();
	
	static DebugMessage debugmessage = null;
	static CalcVectors calc = new CalcVectors();
	static VectorOfAnInterval vec = new VectorOfAnInterval();

	public LevelChangerCombined(MyLogCache logCache, String mode) {
		mylogcache = logCache;
		MODE = mode;
		debugmessage = new DebugMessage();
	}

	public void run() {
		LearningData learningdata = null;

		Socket socket = connect2Agent();

		Connector connector = new Connector(socket);
		List<ExeTimeJson> exeTimeJsons = new LinkedList<>();


		boolean isFirstData = true;
		PrevState ps = null;

		closeOnExit(exeTimeJsons);

		// Data receive roop
		while (socket.isConnected()) {
			Message message = null;

			try {
				message = connector.read(Message.class);
			} catch (Exception e) {
				System.err.println("Cannot recieve");
				break;
			}

			// Data receive (first)
			if (message.Methods != null && 0 < message.Methods.size()) {
				try {
					firstReceive(message);
					learningdata = new LearningData(FILENAME,EP,vec.getNumOfMethods(),ISDEBUG, MODE, DEBUGLOGOUTPUT);
					if(learningdata.isInvalidLearningData()) {
							debugmessage.print("Exit PADLA...");
						break;
					}

				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}
				ps = new PrevState();
			}

			// Data receive (after the second time)
			if (message.ExeTimes != null && 0 < message.ExeTimes.size()) {
				vec.setSumOfVectors(addSamplingDataToSumOfVectors(message, vec.getSumOfVectors()));
				vec.incCountSamaple();
				if(vec.getCountSample() == INTERVAL) {
					vec.setNormalizedVector(calc.normalizeVector(vec.getSumOfVectors(), vec.getNumOfMethods()));
					if (isFirstData || calc.isUnknownPhase(vec.getNormalizedVector(),samplingData,vec.getNumOfMethods(),EP)) {
						try {
							addSamplingData(vec.getNormalizedVector());
							if(MODE.equals("Learning")) {
								bwVector.write(Arrays.toString(vec.getNormalizedVector()) + "\n");
								bwVector.flush();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						isFirstData = false;
					}
					vec.resetCountSample();
					setLogLevel(learningdata, vec.getNormalizedVector(), ps);
					calc.initArray(vec.getSumOfVectors());
					calc.initArray(vec.getNormalizedVector());
				}
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
	 * It check whether sumOfVectors is known or unknown and change "isFirstLevel" flag
	 * @param learningdata
	 * @param vectors
	 * @param ps
	 */
	private void setLogLevel(LearningData learningdata, double[] vectors, PrevState ps) {
		//Compare to learningData
		if(learningdata.isUnknownPhase(vectors, vec.getNumOfMethods())) {
			if(this.isFirstLevel()) {
				isFirstLevel = false;
					debugmessage.printOnDebug("Unknown Phase Detected!\n");
					debugmessage.printOnDebug("Logging Level Down\n↓↓↓↓↓↓↓↓");
				if(MODE .equals("Adapter")) {
					mylogcache.outputLogs();
				}
			}
			if(continuesIn2Intervals(ps,vectors)) {
				addLearningData(learningdata,ps,vectors);
			}
		}else {
			if(!this.isFirstLevel()) {
				isFirstLevel = true;
					debugmessage.printOnDebug("Returned to Normal Phase\n");
					debugmessage.printOnDebug("Logging Level Up\n↑↑↑↑↑↑↑↑");
			}
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
		learningdata.add(cloneCurrent);
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
		double innerproduct = calc.calcInnerProduct(ps.get(), current, vec.getNumOfMethods());
		if(innerproduct > EP) {
			ps.incCount();
			ps.update(current);
			if(ps.getCount() >= 2) {
				return true;
			}
			return false;
		}else {
			ps.stayCount();
			ps.update(current);
			return false;
		}
	}

	/**
	 * It extracts options from message
	 * @param message
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void firstReceive(Message message)
			throws UnsupportedEncodingException, FileNotFoundException, IOException, InterruptedException {
		FILENAME = message.LEARNINGDATA;
		mylogcache.setOUTPUT(message.BUFFEROUTPUT);
		mylogcache.setCACHESIZE(message.BUFFER);
		INTERVAL = message.INTERVAL;
		EP = message.EP;
		ISDEBUG = message.ISDEBUG;
		OUTPUTFILENAME = message.PHASEOUTPUT;
		DEBUGLOGOUTPUT = message.DEBUGLOGOUTPUT;
		debugmessage.setISDEBUG(ISDEBUG);
		

		//Thread.sleep(5000);

		vec.setNumOfMethods(message.Methods.size());
			debugmessage.printOnDebug("\n"+ "---optionsForLevelChanger---");
			debugmessage.printOnDebug("learningData = " + FILENAME);
			debugmessage.printOnDebug("output = " + mylogcache.getOUTPUT());
			debugmessage.printOnDebug("buffer = " + mylogcache.getCACHESIZE());
			debugmessage.printOnDebug("interval = " + INTERVAL);
			debugmessage.printOnDebug("threshold = " + EP);
			debugmessage.printOnDebug("debugLogOutput = " + DEBUGLOGOUTPUT);
			if(ISDEBUG) {
				debugmessage.printOnDebug("isDebug = true");
			}else {
				debugmessage.printOnDebug("isDebug = false");
			}
			debugmessage.printOnDebug("---optionsForLevelChanger---\n");
			debugmessage.printOnDebug("Number of methods:" + vec.getNumOfMethods());
		if(OUTPUTFILENAME != null) {
			if(MODE.equals("Learning")) {
				try {
					bwVector = new BufferedWriter(new FileWriter(new File(OUTPUTFILENAME)));
				} catch (IOException e5) {
					e5.printStackTrace();
				}

			}
		}
	}


	/**
	 * It prints a message when a target process ends
	 * @param exeTimeJsons
	 */
	private static void closeOnExit(List<ExeTimeJson> exeTimeJsons) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
					debugmessage.printOnDebug("Target process finished");
			}
		});
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
