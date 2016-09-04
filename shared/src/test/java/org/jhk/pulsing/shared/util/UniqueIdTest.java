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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Ji Kim
 */
public class UniqueIdTest {
    
    @Test
    public void testBitToString() {
        
        assertTrue("Mask for timestamp ", longToString(Util.TIMESTAMP_MASK)
                .equals("1111111111111111111111111111111111111111000000000000000000000000"));
        
        assertTrue("Counter mask ", longToString(Util.COUNTER_MASK)
                .equals("0000000000000000000000000000000000000000111111111111110000000000"));
        
        assertTrue("Host mask ", longToString(Util.HOST_MASK)
                .equals("0000000000000000000000000000000000000000000000000000001111111111"));
        
        assertTrue("Bit to String test 8", longToString(8L)
                .equals("0000000000000000000000000000000000000000000000000000000000001000"));
        
        assertTrue("Bit to String test 3", longToString(3L)
                .equals("0000000000000000000000000000000000000000000000000000000000000011"));
        
        assertTrue("Bit to String test 2043017427", longToString(2043017427L)
                .equals("0000000000000000000000000000000001111001110001011111100011010011"));
        
        assertTrue("Bit to String test MAX", longToString(Long.MAX_VALUE)
                .equals("0111111111111111111111111111111111111111111111111111111111111111"));
        
    }
    
    @Test
    public void testUniqueIdBitManipulations() {
        
        long hName = 2L & Util.HOST_MASK;
        long time = ((long) Math.pow(2, 62)) & Util.TIMESTAMP_MASK;
        long count = ((long) Math.pow(2, 11)) & Util.COUNTER_MASK;
        
        assertTrue("Unique id test 1", longToString(hName | time | count)
                .equals("0100000000000000000000000000000000000000000000000000100000000010"));
        
        //executor has 2043017427 value
        //0000000000000000000000000000000001111001110001011111100011010011
        hName = Long.parseUnsignedLong("executor".hashCode() + "") & Util.HOST_MASK;
        
        assertTrue("Unique id test 2", longToString(hName | time | count)
                .equals("0100000000000000000000000000000000000000000000000000100011010011"));
        
        count = ((long) Math.pow(2, 17)) & Util.COUNTER_MASK;
        time = ((long) Math.pow(2, 60)) & Util.TIMESTAMP_MASK;

        assertTrue("Unique id test 3", longToString(hName | time | count)
                .equals("0001000000000000000000000000000000000000000000100000000011010011"));
    }
    
    private String longToString(long val) {
        
        String cnt = "";
        
        long mask = Long.parseUnsignedLong("1");
        for(int loop=0; loop < Long.SIZE; loop++, mask <<= 1) {
            cnt = ((mask & val) == mask ? "1" : "0") + cnt;
        }
        
        return cnt;
    }
    
}
