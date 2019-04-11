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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import jp.naist.heijo.Connector;
import jp.naist.heijo.json.ExeTimeJson;
import jp.naist.heijo.message.Message;

public class LevelChanger extends Thread{
	/**added by mizouchi**/
	static int numOfMethods = 0; //ベクトルの要素数
	static final double ep = 0.95; //フェイズの一致の判定に用いる閾値
	static int INTERVAL = 5; //区間の数1つ0.1s
	static String FILENAME = null;
	static MyLogCache mylogcache = null;
	static int LLLevel = 0;

	public LevelChanger(MyLogCache logCache) {
		mylogcache = logCache;
	}

	public void run() {
		LearningData learningdata = null;

		Socket socket = connect2Agent();

		Connector connector = new Connector(socket);
		List<ExeTimeJson> exeTimeJsons = new LinkedList<>();

		int countOfSample = 0;
		double[] sumOfVt = null; //Vtの和．5回(0.5秒)分のデータを取ったらWiを作成して初期化
		PrevState ps = null;

		closeOnExit(exeTimeJsons);

		// データ受信ループ
		while (socket.isConnected()) {
			// データを受信
			Message message = null;
			try {
				message = connector.read(Message.class);
			} catch (Exception e) {
				System.err.println("[PADLA]:Cannot recieve");
				break;
			}

			if (message.Methods != null && 0 < message.Methods.size()) {
				try {
					firstReceive(message);
					try {
						learningdata = new LearningData(FILENAME,ep,numOfMethods);
					} catch (FileNotFoundException e4) {
						// TODO 自動生成された catch ブロック
						e4.printStackTrace();
					}
				} catch (UnsupportedEncodingException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				//sumOF5Vtを初期化
				sumOfVt = new double[numOfMethods];
				ps = new PrevState();
			}

			if (message.ExeTimes != null && 0 < message.ExeTimes.size()) {
				sumOfVt =  addVtToV(message, sumOfVt);
				countOfSample++;
				if(countOfSample == INTERVAL) {
					countOfSample = 0;
					setLogLevel(learningdata, sumOfVt, ps);
				}
			}
		}
	}

	private Socket connect2Agent() {
		System.out.println("[PADLA]:Waiting for Connection,,,");

		// 接続待ち
		ServerSocket server = null;
		try {
			server = new ServerSocket(8000);
		} catch (IOException e3) {
			// TODO 自動生成された catch ブロック
			e3.printStackTrace();
		}
		Socket socket = null;
		try {
			socket = server.accept();
		} catch (IOException e2) {
			// TODO 自動生成された catch ブロック
			e2.printStackTrace();
		}
		try {
			server.close();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}

		System.out.println("[PADLA]:Connection Complete");
		return socket;
	}

	public boolean isFirstLevel() {
		if(LLLevel == 0) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 未知フェイズが来るとログの出力レベルを変更
	 * @param learningData
	 * @param jmxterm
	 * @param isLowerLevel
	 * @param sumOfVt
	 * @return
	 */
	private void setLogLevel(LearningData learningdata, double[] sumOfVt, PrevState ps) {
		double[] normalizedVector = new double[numOfMethods];
		normalizedVector = normalizeVector(sumOfVt);
		initArray(sumOfVt);
		//learningDataと比較
		if(learningdata.isUnknownPhase(normalizedVector, numOfMethods)) {
			if(this.isFirstLevel()) {
				mylogcache.outputLogs();
				LLLevel = 1;
				System.out.println("[PADLA]:Unknown Phase Detected!\n[PADLA]Logging Level Down\n↓↓↓↓↓↓↓↓");
			}
			addLearningData(learningdata,ps,normalizedVector);
		}else {
			if(!this.isFirstLevel()) {
				LLLevel = 0;
				System.out.println("[PADLA]:Returned to Normal Phase\n[PADLA]Logging Level Up\n↑↑↑↑↑↑↑↑");
			}
		}
	}

	/**
	 * 未知のフェイズが2区間分続けば既知のフェイズとして学習データに登録
	 * 登録するベクトルは最後の区間のものだけ
	 * @param learningdata
	 * @param ps
	 * @param current
	 */
	private static void addLearningData(LearningData learningdata, PrevState ps, double[] current) {
		double innerproduct = calcInnerProduct(ps.get(), current);
		double[] cloneCurrent = new double[numOfMethods];
		cloneCurrent = current.clone();
		if(innerproduct > ep) {
			ps.incCount();
			ps.update(current);
			if(ps.getCount() >= 2) {
				learningdata.add(cloneCurrent);
				ps.refresh();
				LLLevel = 0;
				System.out.println("[PADLA]:Learned\n[PADLA]Logging Level Up\n↑↑↑↑↑↑↑↑");
			}
		}else {
			ps.stayCount();
			ps.update(current);
		}
	}

	/**
	 * メソッド情報を受信したとき（初回）
	 * targetプロセスのPIDを取得しjmxtermで接続
	 * targetプロセスの総メソッド数をnumOfMethodsに格納する
	 * @param args
	 * @param message
	 * @param sumOfVt
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
		System.out.println("\n[PADLA]:---optionsForLevelChanger---");
		System.out.println("[PADLA]:learningData = " + FILENAME);
		System.out.println("[PADLA]:output = " + mylogcache.getOUTPUT());
		System.out.println("[PADLA]:buffer = " + mylogcache.getCACHESIZE());
		System.out.println("[PADLA]:nterval = " + INTERVAL);
		System.out.println("[PADLA]:---optionsForLevelChanger---\n");
		//jmxtermを起動してアプリと接続
		Thread.sleep(5000);

		numOfMethods = message.Methods.size();
		System.out.println("[PADLA]:Number of methods:" + numOfMethods);
	}

	/**
	 * ターゲットプロセスの終了時にjmxtermを終了
	 * @param exeTimeJsons
	 */
	private static void closeOnExit(List<ExeTimeJson> exeTimeJsons) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("[PADLA]:Target process finished");
			}
		});
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
