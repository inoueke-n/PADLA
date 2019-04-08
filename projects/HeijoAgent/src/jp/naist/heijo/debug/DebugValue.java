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

package jp.naist.heijo.debug;

public class DebugValue
{

  // falseのとき、すべてのDEBUGフラグを無視する
  public static final boolean DEBUG_FLAG = false;

  // サーバへの接続を省略する
  public static final boolean DEBUG_NO_CONNECT = false;

  // サンプリングおよび更新のレートをコンソールに表示する（FLAG：コンソール表示のフラグ，TIME：指定した回数分のレートの平均を表示する）
  public static final boolean DEBUG_PRINT_SAMPLE_INTERVAL_FLAG = false;
  public static final boolean DEBUG_PRINT_UPDATE_INTERVAL_FLAG = false;
  public static final int DEBUG_PRINT_SAMPLE_INTERVAL_TIME = 1000;
  public static final int DEBUG_PRINT_UPDATE_INTERVAL_TIME = 10;

}
