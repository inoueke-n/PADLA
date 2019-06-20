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

public class ConstValue
{

  // CONFIG.propertiesの読み込みエラー時に適用される値
  public static final String DEFAULT_HOST = "localhost";
  public static final int DEFAULT_PORT = 8000;
  public static final int DEFAULT_SAMPLE_INTERVAL = 2;
  public static final int DEFAULT_UPDATE_INTERVAL = 100;

  public static final String THIS_PACKAGE_NAME = "jp.naist.heijo.*";

  public static final String CONFIG_FILE_PATH = "/CONFIG.properties";

  public static final String DEFAULT_PACKAGE_NAME = "<default-package>";

  public static final int HEADER_SIZE = 4;
}
