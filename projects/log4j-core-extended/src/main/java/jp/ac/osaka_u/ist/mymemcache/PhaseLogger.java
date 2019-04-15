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

package jp.ac.osaka_u.ist.mymemcache;

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

import jp.naist.heijo.Connector;
import jp.naist.heijo.message.Message;

public class PhaseLogger extends Thread{
	/**added by mizouchi**/
	static int numOfMethods = 0; //ベクトルの要素数
	static final double ep = 0.95; //フェイズの一致の判定に用いる閾値
	static int INTERVAL = 5; //区間の数1つ0.1s
	static String OUTPUTFILENAME = null;
	static int LLLevel = 0;
	static BufferedWriter bwVector = null;

	private static String messageHead = "[LOG4JCORE-EXTENDED]:";


	public void run() {

		Socket socket = connect2Agent();
		try {
			socket.setSoTimeout(2000);
		} catch (SocketException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}

		Connector connector = new Connector(socket);

		int countOfSample = 0;
		double[] sumOfVt = null; //Vtの和．5回(0.5秒)分のデータを取ったらWiを作成して初期化
		// データ受信ループ
		while (socket.isConnected()) {
			// データを受信
			Message message = null;
			try {
				message = connector.read(Message.class);
			} catch (SocketTimeoutException e) {
				System.err.println(messageHead + "Socket timeout");
				break;
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			if (message.Methods != null && 0 < message.Methods.size()) {
				try {
					firstReceive(message);
				} catch (IOException |InterruptedException e) {
					e.printStackTrace();
				}
				//sumOF5Vtを初期化
				sumOfVt = new double[numOfMethods];
			}

			if (message.ExeTimes != null && 0 < message.ExeTimes.size()) {
				sumOfVt =  addVtToV(message, sumOfVt);
				countOfSample++;
				if(countOfSample == INTERVAL) {
					try {
						bwVector.write(Arrays.toString(sumOfVt) + "\n");
					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
					countOfSample = 0;
				}
			}
		}
	}

	private Socket connect2Agent() {
		System.out.println(messageHead + "Waiting for Connection,,,");
		// 接続待ち
		ServerSocket server = null;
		Socket socket = null;
		try {
			server = new ServerSocket(8000);
			socket = server.accept();
			server.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		System.out.println(messageHead + "Connection Complete");
		return socket;
	}

	/**
	 * メソッド情報を受信したとき（初回）
	 * 一区間にどれだけサンプリングデータを入れるかmessageから取得
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
			// TODO 自動生成された catch ブロック
			e5.printStackTrace();
		}
	}

	/**added by mizouchi**/
	/**
	 * ある区間のVtの和であるVの作成
	 * @param message
	 * @param sumOf5Vt
	 */
	static double[] addVtToV(Message message, double[] sumOf5Vt) {
		double[] Vt = new double[numOfMethods];

		//Vtの初期化
		initArray(Vt);

		//messageに格納された実行時間を，メソッドIDに対応するVtの要素に格納する
		for (int index = 0; index < message.ExeTimes.size(); index++) {
			if (Vt[message.ExeTimes.get(index).MethodID] < message.ExeTimes.get(index).ExeTime) { //メソッドIDは同じでもスレッドIDが違う場合は実行時間が長いほうを採用する
				Vt[message.ExeTimes.get(index).MethodID] = message.ExeTimes.get(index).ExeTime;
			}
		}

		for (int i = 0; i < numOfMethods; i++) {
			sumOf5Vt[i] += Vt[i];
		}

		return sumOf5Vt;
	}

	/**
	 * ベクトルを正規化
	 */
	static double[] normalizeVector(double[] sumOfVt) {
		double[] Wi = new double[numOfMethods];
		double normOfV = 0;

		//Wiの初期化
		initArray(Wi);

		//Vのノルムを求める
		for (int i = 0; i < numOfMethods; i++) {
			normOfV += sumOfVt[i] * sumOfVt[i];
		}
		normOfV = Math.sqrt(normOfV);

		//Wiの各要素をWiのノルムで割って正規化完了
		for (int i = 0; i < numOfMethods; i++) {
			if (normOfV != 0) {
				Wi[i] = sumOfVt[i] / normOfV;
			}
		}

		return Wi;
	}

	/**
	 * 配列を0で初期化
	 * @param array
	 * @param numOfContents
	 */
	static void initArray(double[] array) {
		Arrays.fill(array, 0.0);
	}

	/**
	 * 二つのベクトルの内積を返す
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
