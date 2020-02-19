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

package padla;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Config
{

  public String Host;
  public int Port;
  public int SampleInterval;
  public int UpdateInterval;
  public Set<String> IgnorePackages = new HashSet<String>() { { add(ConstValue.THIS_PACKAGE_NAME); } };

//  public void load(AgentOptions options) throws Exception
//  {
//    Properties properties = new Properties();
//    properties.load(this.getClass().getResourceAsStream(ConstValue.CONFIG_FILE_PATH));
//    Host = getStringProperty(properties, "HOST", true, ConstValue.DEFAULT_HOST);
//    Port = getIntegerProperty(properties, "PORT", true, ConstValue.DEFAULT_PORT);
////    SampleInterval = getIntegerProperty(properties, "SAMPLE_INTERVAL", true, ConstValue.DEFAULT_SAMPLE_INTERVAL);
////    UpdateInterval = getIntegerProperty(properties, "UPDATE_INTERVAL", true, ConstValue.DEFAULT_UPDATE_INTERVAL);
//    SampleInterval = options.getSampleInterval();
//    UpdateInterval = options.getUpdateInterval();
//    String[] ignore = getStringArrayProperty(properties, "IGNORE_PACKAGE", true, new String[] {});
//    IgnorePackages.addAll(Arrays.asList(ignore));
//  }
  public void load() {
	  Host = ConstValue.DEFAULT_HOST;
	  Port = ConstValue.DEFAULT_PORT;
  }

  private int getIntegerProperty(Properties properties, String key, boolean isOptional, int defaultValue) throws Exception
  {
    try {
      return Integer.valueOf(properties.getProperty(key));
    } catch (Exception e) {
      if (isOptional) {
        return defaultValue;
      } else {
        throw e;
      }
    }
  }

  private String getStringProperty(Properties properties, String key, boolean isOptional, String defaultValue) throws Exception
  {
    String value = properties.getProperty(key);
    if (value == null) {
      if (isOptional) {
        return defaultValue;
      }
      else {
        throw new Exception();
      }
    }
    else {
      return value;
    }
  }

  private String[] getStringArrayProperty(Properties properties, String key, boolean isOptional, String[] defaultValue) throws Exception
  {
    String value = properties.getProperty(key);
    if (value == null) {
      if (isOptional) {
        return defaultValue;
      }
      else {
        throw new Exception();
      }
    }
    else {
      return value.replace(" ", "").replace("\t", "").split(",");
    }
  }

}
