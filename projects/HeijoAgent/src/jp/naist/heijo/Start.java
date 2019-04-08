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

import java.lang.instrument.Instrumentation;

public class Start {
	//argsの中身 "target=[監視対象のjar],learningData=[学習データのパス],bufferOutput=[メモリバッファの出力先],phaseOutput,=[学習データの出力先],buffer=[メモリバッファサイズ],interval=[区間の長さ]"
	  public static void premain(String args, Instrumentation inst) {
		  Monitor monitor = Monitor.getInstance();
		  monitor.setArgs(args);
		  monitor.start();
	  }
}
