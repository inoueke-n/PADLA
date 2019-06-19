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

import jp.naist.heijo.debug.DebugValue;
import jp.naist.heijo.timer.Scheduler;

public class Monitor extends Thread
{



	public Config Config = new Config();
	public StructureDB StructureDB = new StructureDB();
	public Connector Connector = new Connector();
	public Scheduler Scheduler = null;

	public String[] args = null;
	public String target = null;
	public String learningData = null;
	public String bufferoutput = null;
	public String phaseoutput = null;
	public int buffer = 0;
	public int interval = 0;

	private String messageHead = "[AGENT]:";

	private static Monitor instance = null;

	// FIXME too long
	public static Monitor getInstance()
	{
		if (instance == null) instance = new Monitor();
		return instance;
	}

	public void setArgs(String option) {
		if(option == null) {
			System.out.println(messageHead + "ERROR No option");
			System.exit(0);
		}
		this.args = option.split(",",0);
		for(int i = 0; i < this.args.length; i++) {
			String argumentTag = this.args[i].split("=",0)[0];
			String argumentValue = this.args[i].split("=",0)[1];
			switch(argumentTag) {
			case "target":
				this.target = argumentValue;
				break;
			case "learningData":
				this.learningData = argumentValue;
				break;
			case "bufferOutput":
				this.bufferoutput = argumentValue;
				break;
			case "phaseOutput":
				this.phaseoutput = argumentValue;
				break;
			case "buffer":
				this.buffer = Integer.valueOf(argumentValue);
				break;
			case "interval":
				this.interval = Integer.valueOf(argumentValue);
				break;
			default:
				System.out.println(messageHead + "ERROR Invalid argument:" + argumentTag);
			}
		}
		System.out.println("\n" + messageHead + "---options---");
		System.out.println(messageHead + "target = " + this.target);
		System.out.println(messageHead + "learningData = " + this.learningData);
		System.out.println(messageHead + "bufferoutput = " + this.bufferoutput);
		System.out.println(messageHead + "phaseoutput = " + this.phaseoutput);
		System.out.println(messageHead + "buffer = " + this.buffer);
		System.out.println(messageHead + "interval = " + this.interval);
		System.out.println(messageHead + "---options---\n");
		Scheduler = new Scheduler(learningData, bufferoutput, phaseoutput, buffer, interval);
	}

	public void run()
	{
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		System.out.println(messageHead + "Setting agent...");

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
					System.err.println(messageHead + "Failed to connect " + getInstance().Config.Host + ":" + getInstance().Config.Port);
					success = false;
					break;
				}
			}

			// クラスパス以下のクラスファイルを走査して、パッケージ・クラス・メソッド名を収集
			getInstance().StructureDB = new StructureDB();
			getInstance().StructureDB.setTarget(this.target);
			getInstance().StructureDB.IgnorePackageNameSet.addAll(getInstance().Config.IgnorePackages);
			try {
				getInstance().StructureDB.collectFromClassPath();
			} catch (IOException e) {
				System.out.println(messageHead + "Failed to access to class files");
				if (!(DebugValue.DEBUG_FLAG && DebugValue.DEBUG_NO_CONNECT)) {
					getInstance().Connector.close();
				}
				success = false;
				break;
			}

		} while (false);

		if (success) {
			System.out.println(messageHead + "Succeeded to set agent");
			getInstance().Scheduler.start();
		}
	}

}
