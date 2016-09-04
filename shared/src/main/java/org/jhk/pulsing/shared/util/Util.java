/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jhk.pulsing.shared.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * @author Ji Kim
 */
public final class Util {
    
    public static long TIMESTAMP_MASK = ~0L << 24;
    public static long HOST_MASK = ~0L >>> 54;
    public static long COUNTER_MASK = ((long) Math.pow(2, 14) - 1L) << 10;
    
    private static int _COUNTER;
    private static long _LOCAL_HOST_HASHED;
    
    static {
        try {
            String lHost = InetAddress.getLocalHost().getHostName();
            _LOCAL_HOST_HASHED = (long) lHost.hashCode();
        } catch (UnknownHostException uhException) {
        }
    }
    
    /**
     * Simple uniqueId generator (i.e. for PulseId and etc)
     * First 40 is time, next 14 is count, and the next 10 is the host name 
     * 
     * @param nanoTime
     * @return
     */
    public static long uniqueId() {
        long lHost = _LOCAL_HOST_HASHED & HOST_MASK;
        long time = System.nanoTime() & TIMESTAMP_MASK;
        long count = _COUNTER++ & COUNTER_MASK; 
        return time | count | lHost;
    }
    
    /**
     * Given a timestamp in seconds and split it to the provided 
     * timeInterval 
     * 
     * @param timestampSeconds
     * @param timeInterval
     * @return
     */
    public static long getTimeInterval(Long timestampSeconds, int timeInterval) {
        return timestampSeconds / timeInterval;
    }
    
    /**
     * Just for convenience, since will be used a lot
     * 
     * @param nanoTime
     * @return
     */
    public static long convertNanoToSeconds(long nanoTime) {
        return TimeUnit.SECONDS.convert(nanoTime, TimeUnit.NANOSECONDS);
    }
    
    private Util() {
        super();
    }
}
