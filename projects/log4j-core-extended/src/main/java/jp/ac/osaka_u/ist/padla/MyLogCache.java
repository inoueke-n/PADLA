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
import java.io.FileWriter;
import java.io.IOException;

public class MyLogCache {
	private int count = 0;
	private int CACHESIZE = 0;
	public String BUFFEROUTPUT = null;
	public String getOUTPUT() {
		return BUFFEROUTPUT;
	}
	public void setOUTPUT(String oUTPUT) {
		BUFFEROUTPUT = oUTPUT;
        File byteFile= new File(BUFFEROUTPUT);
        try {
			byteBw = new BufferedWriter(new FileWriter(byteFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public int getCACHESIZE() {
		return CACHESIZE;
	}
	public void setCACHESIZE(int cACHESIZE) {
		CACHESIZE = cACHESIZE;
		cachedLogs = new String[CACHESIZE];
	}

	private int nextIndex = 0;
	private int currentIndex = 0;
	private int oldestIndex = 0;
	String[] cachedLogs = null;
	BufferedWriter byteBw = null;




	/**
	 * It output log messages that are kept in the cachedLogs
	 */
	public void outputLogs() {
		int numOfOutputLogs = 0;
		if(count < CACHESIZE) {
			numOfOutputLogs = count;
		}else {
			numOfOutputLogs = CACHESIZE;
		}
		try {
			byteBw.write("[OUTPUT]\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(int i = 0; i < numOfOutputLogs; i++) {
			try {
				byteBw.write(cachedLogs[(oldestIndex + i) % CACHESIZE]);
				byteBw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * It add a log message to the cachedLogs
	 * @param str
	 */
	public void appendLogToCache(String str) {
    	cachedLogs[nextIndex] = str;
    	currentIndex = nextIndex;
    	if(count != 0) {
    		if(oldestIndex == currentIndex) {
    			oldestIndex = (oldestIndex + 1) % CACHESIZE;
    		}
    	}
    	nextIndex = (nextIndex + 1) % CACHESIZE;
    	count++;
	}
}
