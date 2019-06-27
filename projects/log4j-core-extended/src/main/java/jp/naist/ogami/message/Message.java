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

package jp.naist.ogami.message;

import java.util.LinkedList;
import java.util.List;

@org.msgpack.annotation.Message
public class Message
{

	@org.msgpack.annotation.Index(0)
	public long CurrentTime = 0;

	@org.msgpack.annotation.Index(1)
	public double TimeLength = 0;

	@org.msgpack.annotation.Index(2)
	public List<MethodInfo> Methods = new LinkedList<>();

	@org.msgpack.annotation.Index(3)
	public List<ExeTimeInfo> ExeTimes = new LinkedList<>();

	@org.msgpack.annotation.Index(4)
	public String PID = "pid";

	@org.msgpack.annotation.Index(5)
	public String LEARNINGDATA = null;


	@org.msgpack.annotation.Index(6)
	public String BUFFEROUTPUT = null;

	@org.msgpack.annotation.Index(7)
	public String PHASEOUTPUT = null;

	@org.msgpack.annotation.Index(8)
	public int BUFFER = 0;

	@org.msgpack.annotation.Index(9)
	public int INTERVAL = 0;

	@org.msgpack.annotation.Index(10)
	public double EP = 0;

	@org.msgpack.annotation.Index(11)
	public boolean ISDEBUG = false;


}
