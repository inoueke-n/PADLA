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

package jp.naist.heijo.timer;

import java.io.IOException;
import java.util.Map;

import jp.ac.osaka_u.padla.Options;
import jp.naist.heijo.Monitor;
import jp.naist.heijo.debug.DebugValue;
import jp.naist.heijo.debug.IntervalPrinter;
import jp.naist.heijo.message.ExeTimeInfo;
import jp.naist.heijo.message.Message;
import jp.naist.heijo.util.Pair;

public class UpdateThread extends Thread
{

  private boolean isFirstSend = true;

  private long before = -1;

  private IntervalPrinter debugIntervalPrinter = null;

  public Options options = null;

  private String messageHead = "[AGENT]:";

  public UpdateThread(Options options)
  {
    if (DebugValue.DEBUG_FLAG && DebugValue.DEBUG_PRINT_UPDATE_INTERVAL_FLAG) {
      debugIntervalPrinter = new IntervalPrinter(DebugValue.DEBUG_PRINT_UPDATE_INTERVAL_TIME, "UPDATE");
    }
    this.options = options;
  }

  @Override
  public void run()
  {
    try {
      if (isFirstSend) {
        firstSend();
        isFirstSend = false;
      }
    } catch (Exception e) {
      System.err.println(e);
    }

    try {
      update();
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  private void update()
  {
    if (DebugValue.DEBUG_FLAG && DebugValue.DEBUG_PRINT_UPDATE_INTERVAL_FLAG) debugIntervalPrinter.interval();

    Message message = new Message();
    message.setLEARNINGDATA(options.getLearningData());
    message.setBUFFEROUTPUT(options.getBufferoutput());
    message.setPHASEOUTPUT(options.getPhaseoutput());
    message.setBUFFER(options.getBuffer());
    message.setINTERVAL(options.getInterval());
    message.setEP(options.getEp());
    message.setIsDebug(options.isDebug());
    message.setDEBUGLOGOUTPUT(options.getDebugLogOutput());

    synchronized (Monitor.getInstance().Scheduler.Lock) {
      message.CurrentTime = System.currentTimeMillis();

      // 前回のupdateとのIntervalを計算。初回時は固定時間を信じる
      if (before < 0) {
        message.TimeLength = Monitor.getInstance().Config.UpdateInterval;
      }
      else {
        long diff = message.CurrentTime - before;
        message.TimeLength = diff;
      }
      before = message.CurrentTime;

      for (Map.Entry<Pair<Integer, Long>, Integer> entry : Monitor.getInstance().Scheduler.SampleNumMap.entrySet()) {
        int methodID = entry.getKey().first();
        long threadID = entry.getKey().second();
        double exeRate = (double) entry.getValue() / Monitor.getInstance().Scheduler.Counter;
        double exeTime = exeRate * message.TimeLength;
        ExeTimeInfo info = new ExeTimeInfo(methodID, threadID, exeTime);
        message.ExeTimes.add(info);
      }

      Monitor.getInstance().Scheduler.Counter = 0;
      Monitor.getInstance().Scheduler.SampleNumMap.clear();
    }

    if (!(DebugValue.DEBUG_FLAG && DebugValue.DEBUG_NO_CONNECT)) {
      try {
        Monitor.getInstance().Connector.write(message);
      } catch (IOException e) {
        System.err.println(messageHead + "Connection is closed");
        Monitor.getInstance().Scheduler.Executor.shutdownNow();
      }
    }
  }

  private void firstSend()
  {
    if (DebugValue.DEBUG_FLAG && DebugValue.DEBUG_NO_CONNECT) return;

    Message message = new Message();
    message.setLEARNINGDATA(options.getLearningData());
    message.setBUFFEROUTPUT(options.getBufferoutput());
    message.setPHASEOUTPUT(options.getPhaseoutput());
    message.setBUFFER(options.getBuffer());
    message.setINTERVAL(options.getInterval());
    message.setEP(options.getEp());
    message.setIsDebug(options.isDebug());
    message.setDEBUGLOGOUTPUT(options.getDebugLogOutput());
    message.CurrentTime = 0;
    message.TimeLength = 0;
    message.Methods.addAll(Monitor.getInstance().StructureDB.IdDataMap.values());
    try {
      Monitor.getInstance().Connector.write(message);
    } catch (IOException e) {
      System.err.println(messageHead + "Connection is closed");
      Monitor.getInstance().Scheduler.Executor.shutdownNow();
    }
  }

}
