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

package jp.naist.ogami;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.msgpack.MessagePack;

public class Connector
{

	public static final int HEADER_SIZE = 4;

	public Socket Socket = null;

	private MessagePack msgpack = new MessagePack();

	public Connector(Socket socket)
	{
		this.Socket = socket;
	}

	public <T> T read(Class<T> type) throws IOException
	{
		return msgpack.read(readRaw(), type);
	}

	public byte[] readRaw() throws IOException
	{
		// ヘッダ受信
		int h_count = HEADER_SIZE;
		byte[] header = new byte[HEADER_SIZE];
		while (h_count != 0) {
			Thread.yield();
			int read = Socket.getInputStream().read(header, HEADER_SIZE - h_count, h_count);
			h_count -= read;
		}
		int payload_size = ByteBuffer.wrap(header).getInt();
		// ペイロード受信
		int p_count = payload_size;
		byte[] payload = new byte[payload_size];
		while (p_count != 0) {
			Thread.yield();
			int read = Socket.getInputStream().read(payload, payload_size - p_count, p_count);
			p_count -= read;
		}
		return payload;
	}

}