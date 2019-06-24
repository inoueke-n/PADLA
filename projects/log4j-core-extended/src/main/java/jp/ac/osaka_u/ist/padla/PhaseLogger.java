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
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import jp.naist.ogami.Connector;
import jp.naist.ogami.message.Message;

public class PhaseLogger extends Thread{
	static int numOfMethods = 0; //
	static final double ep = 0.95; //Threshold used to phase detection
	static int INTERVAL = 5; // Length of one intervel. INTERVEL=1 -> 0.1s
	static String OUTPUTFILENAME = null;
	static BufferedWriter bwVector = null;

	private final static String messageHead = "[LOG4JCORE-EXTENDED]:";


	public void run() {

		Socket socket = connect2Agent();
		try {
			socket.setSoTimeout(2000);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}

		Connector connector = new Connector(socket);

		int countOfSample = 0;
		double[] sumOfVt = null;
		// Data receive roop
		while (socket.isConnected()) {
			// データを受信
			Message message = null;
			try {
				message = connector.read(Message.class);
			} catch (SocketTimeoutException e) {
				System.err.println(messageHead + "Socket timeout");
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Data receive (first)
			if (message.Methods != null && 0 < message.Methods.size()) {
				try {
					firstReceive(message);
				} catch (IOException |InterruptedException e) {
					e.printStackTrace();
				}
				sumOfVt = new double[numOfMethods];
			}

			// Data receive (after the second time)
			if (message.ExeTimes != null && 0 < message.ExeTimes.size()) {
				sumOfVt =  addSamplingDataToVector(message, sumOfVt);
				countOfSample++;
				if(countOfSample == INTERVAL) {
					try {
						bwVector.write(Arrays.toString(normalizeVector(sumOfVt)) + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
					countOfSample = 0;
				}
			}
		}
	}


	private Socket connect2Agent() {
		System.out.println(messageHead + "Waiting for Connection,,,");
		ServerSocket server = null;
		Socket socket = null;
		try {
			server = new ServerSocket(8000);
			socket = server.accept();
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(messageHead + "Connection Complete");
		return socket;
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
		INTERVAL = message.INTERVAL;
		OUTPUTFILENAME = message.PHASEOUTPUT;
		System.out.println("\n[PADLA]:---optionsForPhaseExporter---");
		System.out.println(messageHead + "interval = " + INTERVAL);
		System.out.println(messageHead + "output = " + OUTPUTFILENAME);
		System.out.println(messageHead + "---optionsForPhaseExporter---\n");
		numOfMethods = message.Methods.size();
		System.out.println(messageHead + "Number of Methods:" + numOfMethods);

		try {
			bwVector = new BufferedWriter(new FileWriter(new File(OUTPUTFILENAME)));
		} catch (IOException e5) {
			e5.printStackTrace();
		}
	}


	/**
	 * It extracts method execution times from message and add them to sumOfVectors
	 * @param message
	 * @param sumOfVectors
	 * @return
	 */
	static double[] addSamplingDataToVector(Message message, double[] sumOfVectors) {
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


}
