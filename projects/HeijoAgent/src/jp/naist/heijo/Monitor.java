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

package jp.naist.heijo;

import java.io.IOException;

import jp.ac.osaka_u.padla.AgentMessage;
import jp.ac.osaka_u.padla.Options;
import jp.naist.heijo.debug.DebugValue;
import jp.naist.heijo.timer.Scheduler;

public class Monitor extends Thread
{



	public Config Config = new Config();
	public StructureDB StructureDB = new StructureDB();
	public Connector Connector = new Connector();
	public Scheduler Scheduler = null;
	public String[] args = null;
	private AgentMessage agentmessage = new AgentMessage();
	private static Monitor instance = null;
	public Options options = null;

	// FIXME too long
	public static Monitor getInstance()
	{
		if (instance == null) instance = new Monitor();
		return instance;
	}

	public void setArgs(String option) {
		if(option == null) {
			agentmessage.print("ERROR No option");
			System.exit(0);
		}
		this.args = option.split(",",0);
		for(int i = 0; i < this.args.length; i++) {
			String argumentTag = this.args[i].split("=",0)[0];
			String argumentValue = this.args[i].split("=",0)[1];
			switch(argumentTag) {
			case "OptionFile":
				options = new Options(argumentValue);
				break;
			default:
				agentmessage.print("ERROR Invalid argument:" + argumentTag);
			}
		}
		Scheduler = new Scheduler(options);
	}

	public void run()
	{
		try {
			Thread.sleep(options.getAgentWaitingTime());
		} catch (InterruptedException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		agentmessage.print("Setting agent...");

		boolean success = true;

		do {
			// 設定ファイル読み込み
			try {
				getInstance().Config.load();
			} catch (Exception e) {
				System.err.println("messageHead + Failed to load " + ConstValue.CONFIG_FILE_PATH);
			}

			// ビジュアライザに接続
			getInstance().Connector = new Connector();
			if (!(DebugValue.DEBUG_FLAG && DebugValue.DEBUG_NO_CONNECT)) {
				try {
					getInstance().Connector.connect(getInstance().Config.Host, getInstance().Config.Port);
				} catch (Exception e) {
					agentmessage.printerr("Failed to connect " + getInstance().Config.Host + ":" + getInstance().Config.Port);
					success = false;
					break;
				}
			}

			// クラスパス以下のクラスファイルを走査して、パッケージ・クラス・メソッド名を収集
			getInstance().StructureDB = new StructureDB();
			getInstance().StructureDB.setTarget(options.getTarget());
			getInstance().StructureDB.IgnorePackageNameSet.addAll(getInstance().Config.IgnorePackages);
			try {
				getInstance().StructureDB.collectFromClassPath();
			} catch (IOException e) {
				agentmessage.print("Failed to access to class files");
				if (!(DebugValue.DEBUG_FLAG && DebugValue.DEBUG_NO_CONNECT)) {
					getInstance().Connector.close();
				}
				success = false;
				break;
			}

		} while (false);

		if (success) {
			agentmessage.print("Succeeded to set agent");
			getInstance().Scheduler.start();
		}
	}

}
