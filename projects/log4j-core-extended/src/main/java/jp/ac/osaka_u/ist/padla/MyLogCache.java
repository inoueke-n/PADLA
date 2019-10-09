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
	private int numOfPartitions = 0;
	private int CACHESIZE = 0;
	private int bufferedInterval = 2;
	public void setBufferedInterval(int bufferedInterval) {
		this.bufferedInterval = bufferedInterval;
	}

	private boolean isLocked = false;
	private String BUFFEROUTPUT = null;
	private String PARTITIONSTRING = "[PADLA:PARTITION]\n";
	public String getPARTITIONSTRING() {
		return PARTITIONSTRING;
	}
	public String getOUTPUT() {
		return BUFFEROUTPUT;
	}
	public void setOUTPUT(String oUTPUT) {
		BUFFEROUTPUT = oUTPUT;
		File byteFile= new File(BUFFEROUTPUT);
		try {
			byteBw = new BufferedWriter(new FileWriter(byteFile));
			//Write partition string first
			byteBw.write(PARTITIONSTRING);
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
		isLocked = true;
		int copyIndex = oldestIndex;
		int partitionCount = 0;
		int i = 0;
		//When the num of partitions > 3, there are more than 2 intervals
		while(partitionCount < (bufferedInterval + 1)) {
			if(numOfPartitions == 1 && partitionCount == bufferedInterval) break;
			if(cachedLogs[(copyIndex + i) % CACHESIZE].equals(PARTITIONSTRING)) {
				partitionCount++;
			}
			try {
				byteBw.write("[OUTPUT]\n");
				byteBw.write(cachedLogs[(copyIndex + i) % CACHESIZE]);
				byteBw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		isLocked = false;
	}


	/**
	 * It add a log message to the cachedLogs
	 * @param str
	 */
	public synchronized void appendMessageToCache(String str, boolean isPartition) {
		if(!isLocked && CACHESIZE > 0) {
			if(cachedLogs != null) {
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
		if(isPartition) {
			numOfPartitions++;
		}
	}
}
