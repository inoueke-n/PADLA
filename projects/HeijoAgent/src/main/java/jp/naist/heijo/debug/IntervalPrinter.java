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

import java.util.Formatter;

public class IntervalPrinter
{

  private final int time;
  private final String tag;

  private long before = 0;
  private long sum = 0;
  private int counter = 0;

  private Formatter formatter = new Formatter();

  public IntervalPrinter(int time)
  {
    this(time, null);
  }

  public IntervalPrinter(int time, String tag)
  {
    this.time = time <= 0 ? 1 : time;
    this.tag = tag == null ? "PS" : tag;
  }

  public void interval()
  {
    long now = System.nanoTime();

    if (before == 0) {
      counter++;
      before = now;
      return;
    }

    long diff = now - before;
    sum += diff;

    if (time <= counter) {
      double average = (double)sum / time / 1000000;
      if (tag == null) {
        System.out.println(formatter.format("%.2f [ms]\n", average).toString());
      }
      else {
        System.out.println(formatter.format("[" + tag + "] %.2f [ms]\n", average).toString());
      }
      sum = counter = 0;
    }
    before = now;
    counter++;
  }

}
