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
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Ji Kim
 */
public final class Util {
    
    public static final String DELIM = "-";
    
    private static int _COUNTER;
    private static Optional<String> _LOCAL_HOST;
    
    static {
        try {
            InetAddress lHost = InetAddress.getLocalHost();
            _LOCAL_HOST = Optional.of(lHost.getHostName() + DELIM);
        } catch (UnknownHostException uhException) {
        }
    }
    
    public static String generateUniqueId(String prefix) {
        StringBuilder builder = new StringBuilder(prefix);
        builder.append(DELIM);
        builder.append(_LOCAL_HOST.orElse(""));
        builder.append(System.nanoTime());
        builder.append(DELIM);
        builder.append(_COUNTER++);
        return builder.toString();
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
