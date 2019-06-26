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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import jp.naist.ogami.Connector;
import jp.naist.ogami.json.ExeTimeJson;
import jp.naist.ogami.message.Message;

public class LevelChanger extends Thread{
	static int numOfMethods = 0;
	static double EP = 0; //Threshold used to phase detection
	static int INTERVAL = 5; // Length of one intervel. INTERVEL=1 -> 0.1s
	static String FILENAME = null;
	static MyLogCache mylogcache = null;
	static boolean isFirstLevel = true;

	private final static String messageHead = "[LOG4JCORE-EXTENDED]:";

	public LevelChanger(MyLogCache logCache) {
		mylogcache = logCache;
	}

	public void run() {
		LearningData learningdata = null;

		Socket socket = connect2Agent();

		Connector connector = new Connector(socket);
		List<ExeTimeJson> exeTimeJsons = new LinkedList<>();

		int countOfSample = 0;
		double[] sumOfVectors = null;
		PrevState ps = null;

		closeOnExit(exeTimeJsons);

		// Data receive roop
		while (socket.isConnected()) {
			Message message = null;
			try {
				message = connector.read(Message.class);
			} catch (Exception e) {
				System.err.println(messageHead + "Cannot recieve");
				break;
			}

			// Data receive (first)
			if (message.Methods != null && 0 < message.Methods.size()) {
				try {
					firstReceive(message);
					learningdata = new LearningData(FILENAME,EP,numOfMethods);
					if(learningdata.isInvalidLearningData()) {
						//System.out.println(messageHead + "Exit PADLA...");
						break;
					}

				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}
				sumOfVectors = new double[numOfMethods];
				ps = new PrevState();
			}

			// Data receive (after the second time)
			if (message.ExeTimes != null && 0 < message.ExeTimes.size()) {
				sumOfVectors =  addVtToV(message, sumOfVectors);
				countOfSample++;
				if(countOfSample == INTERVAL) {
					countOfSample = 0;
					setLogLevel(learningdata, sumOfVectors, ps);
				}
			}
		}
	}

	private Socket connect2Agent() {
		//System.out.println(messageHead + "Waiting for Connection,,,");
		ServerSocket server = null;
		Socket socket = null;
		try {
			server = new ServerSocket(8000);
			socket = server.accept();
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//System.out.println(messageHead + "Connection Complete");
		return socket;
	}

	public boolean isFirstLevel() {
		return isFirstLevel;
	}


	/**
	 * It check whether sumOfVectors is known or unknown and change "isFirstLevel" flag
	 * @param learningdata
	 * @param sumOfVectors
	 * @param ps
	 */
	private void setLogLevel(LearningData learningdata, double[] sumOfVectors, PrevState ps) {
		double[] normalizedVector = new double[numOfMethods];
		normalizedVector = normalizeVector(sumOfVectors);
		initArray(sumOfVectors);
		//Compare to learningData
		if(learningdata.isUnknownPhase(normalizedVector, numOfMethods)) {
			if(this.isFirstLevel()) {
				isFirstLevel = false;
				//System.out.println(messageHead + "Unknown Phase Detected!\n");
				//System.out.println(messageHead + "Logging Level Down\n↓↓↓↓↓↓↓↓");
				mylogcache.outputLogs();
			}
			if(continuesIn2Intervals(ps,normalizedVector)) {
				addLearningData(learningdata,ps,normalizedVector);
			}
		}else {
			if(!this.isFirstLevel()) {
				isFirstLevel = true;
				//System.out.println(messageHead + "Returned to Normal Phase\n");
				//System.out.println(messageHead + "Logging Level Up\n↑↑↑↑↑↑↑↑");
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
		double[] cloneCurrent = new double[numOfMethods];
		cloneCurrent = current.clone();
		learningdata.add(cloneCurrent);
		ps.refresh();
		//System.out.println(messageHead + "Learned\n");
		////System.out.println(messageHead + "Logging Level Up\n↑↑↑↑↑↑↑↑");
	}

	/**
	 * Judges if the current phase continues in 2 intervals
	 * @param ps
	 * @param current
	 * @return
	 */
	private static boolean continuesIn2Intervals(PrevState ps, double[] current) {
		double innerproduct = calcInnerProduct(ps.get(), current);
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
		//System.out.println("\n"+ messageHead + "---optionsForLevelChanger---");
		//System.out.println(messageHead + "learningData = " + FILENAME);
		//System.out.println(messageHead + "output = " + mylogcache.getOUTPUT());
		//System.out.println(messageHead + "buffer = " + mylogcache.getCACHESIZE());
		//System.out.println(messageHead + "interval = " + INTERVAL);
		//System.out.println(messageHead + "---optionsForLevelChanger---\n");
		Thread.sleep(5000);

		numOfMethods = message.Methods.size();
		//System.out.println(messageHead + "Number of methods:" + numOfMethods);
	}


	/**
	 * It prints a message when a target process ends
	 * @param exeTimeJsons
	 */
	private static void closeOnExit(List<ExeTimeJson> exeTimeJsons) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				//System.out.println(messageHead + "Target process finished");
			}
		});
	}

	/**
	 * It extracts method execution times from message and add them to sumOfVectors
	 * @param message
	 * @param sumOfVectors
	 * @return
	 */
	static double[] addVtToV(Message message, double[] sumOfVectors) {
		double[] tmpArray = new double[numOfMethods];

		initArray(tmpArray);

		for (int index = 0; index < message.ExeTimes.size(); index++) {
			if (tmpArray[message.ExeTimes.get(index).MethodID] < message.ExeTimes.get(index).ExeTime) { //If the method ID is the same but the thread ID is different, use the longer execution time
				tmpArray[message.ExeTimes.get(index).MethodID] = message.ExeTimes.get(index).ExeTime;
			}
		}

		for (int i = 0; i < numOfMethods; i++) {
			sumOfVectors[i] += tmpArray[i];
		}

		return sumOfVectors;
	}


	/**
	 * It returns normalized vector
	 * @param vector
	 * @return
	 */
	static double[] normalizeVector(double[] vector) {
		double[] normalizedVector = new double[numOfMethods];
		double normOfVector = 0;

		initArray(normalizedVector);

		// calculate norm of the vector
		for (int i = 0; i < numOfMethods; i++) {
			normOfVector += vector[i] * vector[i];
		}
		normOfVector = Math.sqrt(normOfVector);

		for (int i = 0; i < numOfMethods; i++) {
			if (normOfVector != 0) {
				normalizedVector[i] = vector[i] / normOfVector;
			}
		}

		return normalizedVector;
	}

	/**
	 * It initializes array
	 * @param array
	 */
	static void initArray(double[] array) {
		Arrays.fill(array, 0.0);
	}

	/**
	 * It returns inner product of two vectors(array1 and array2)
	 * @param array1
	 * @param array2
	 * @return
	 */
	static double calcInnerProduct(double[] array1, double[] array2) {
		double innerProduct = 0;

		for (int i = 0; i < numOfMethods; i++) {
			innerProduct += array1[i] * array2[i];
		}

		return innerProduct;
	}

	public static class PrevState {
		private double[] prev;
		private int count;

		public PrevState() {
			prev = new double[numOfMethods];
			refresh();
		}

		/**
		 * 同じフェイズで連続する区間のカウントを1にする
		 */
		public void stayCount() {
			count = 1;
		}

		public void refresh() {
			initArray(prev);
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
