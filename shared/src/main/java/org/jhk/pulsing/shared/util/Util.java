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

import java.util.concurrent.TimeUnit;

/**
 * @author Ji Kim
 */
public final class Util {
    
    private Util() {
        super();
    }
    
    public static int getTimeInterval(Long timestampNano, int timeInterval) {
        int timestampSeconds = convertNanoToSeconds(timestampNano);
        return timestampSeconds / timeInterval;
    }
    
    /**
     * Just for convenience, since will be used a lot
     * 
     * @param nanoTime
     * @return
     */
    public static int convertNanoToSeconds(long nanoTime) {
        return (int) TimeUnit.SECONDS.convert(nanoTime, TimeUnit.NANOSECONDS);
    }
    
}
