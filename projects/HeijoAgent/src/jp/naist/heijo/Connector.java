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
import java.net.Socket;
import java.nio.ByteBuffer;

import org.msgpack.MessagePack;

import jp.naist.heijo.debug.DebugValue;

public class Connector
{

  private Socket socket = null;
  private MessagePack msgpack = new MessagePack();

  private class ConnectingThread extends Thread
  {

    public boolean Finish = false;
    public Exception Exception = null;

    private final String host;
    private final int port;

    public ConnectingThread(String host, int port)
    {
      this.host = host;
      this.port = port;
    }

    @Override
    public void run() {
      try {
        socket = new Socket(host, port);
      } catch (Exception e) {
        Exception = e;
      }
      Finish = true;
    };

  }

  public void connect(String host, int port) throws Exception
  {
    if (DebugValue.DEBUG_FLAG && DebugValue.DEBUG_NO_CONNECT) return;

    ConnectingThread thread = new ConnectingThread(host, port);
    thread.start();
    while (!thread.Finish) {
      Thread.yield();
    }
    if (thread.Exception != null) throw thread.Exception;
    if (socket == null) throw new Exception("socket is null");
  }

  public void close()
  {
    try {
      socket.close();
    } catch (IOException e) {
    }
  }

  public <T> void write(T obj) throws IOException
  {
    byte[] payload = null;
    payload = msgpack.write(obj);
    byte[] header = ByteBuffer.allocate(ConstValue.HEADER_SIZE).putInt(payload.length).array();
    socket.getOutputStream().write(header);
    socket.getOutputStream().write(payload);
  }

}

