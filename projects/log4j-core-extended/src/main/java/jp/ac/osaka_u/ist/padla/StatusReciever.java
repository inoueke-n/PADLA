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
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import jp.naist.ogami.Connector;
import jp.naist.ogami.Message;

public class StatusReciever extends Thread{
	static MyLogCache mylogcache = null;
	static boolean isFirstLevel = true;
	static String MODE = null;
	static BufferedWriter bwVector = null;
	static FileWriter file = null;
	List<double[]> samplingData = new ArrayList<double[]>();

	static DebugMessage debugmessage = null;

	public StatusReciever(MyLogCache logCache, String mode) {
		mylogcache = logCache;
		MODE = mode;
		debugmessage = new DebugMessage();
	}

	public void run() {
		Socket socket = connect2Agent();
		Connector connector = new Connector(socket);

		closeOnExit();
		// Data receive roop
		while (socket.isConnected()) {
			Message message = null;
			try {
				message = connector.read(Message.class);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			// Data receive (first)
			if (message.getNUMOFMETHODS() > 0) {
				firstReceive(message);
			}

			// Data receive (after the second time)
			if (message.getNUMOFMETHODS() == -1) {
				mylogcache.appendMessageToCache(mylogcache.getPARTITIONSTRING(), true);
				adaptLogLevel(message);
			}
		}
	}

	/**
	 * Extract options from message
	 * @param message
	 */
	private static void firstReceive(Message message) {
		mylogcache.setOUTPUT(message.getBUFFEROUTPUT());
		mylogcache.setCACHESIZE(message.getCACHESIZE());
		mylogcache.setBufferedInterval(message.getBUFFEREDINTERVAL());
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
	private void adaptLogLevel(Message message) {
		//Compare to learningData
		if(message.isISFIRSTLEVEL()) {
			if(this.isFirstLevel()) {
				isFirstLevel = false;
				if(MODE .equals("Adapter")) {
					mylogcache.outputLogs();
				}
			}
		}else {
			if(!this.isFirstLevel()) {
				isFirstLevel = true;
			}
		}
	}

	/**
	 * It prints a message when a target process ends
	 * @param exeTimeJsons
	 */
	private static void closeOnExit() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
			}
		});
	}

}
