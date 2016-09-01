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
package org.jhk.pulsing.pulse;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.web.service.prod.helper.PulseServiceUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Ji Kim
 */
public class TrendingPulseSubTest {
    
    private static Map<Long, String> _validResult;
    private static ObjectMapper _objectMapper;
    
    @BeforeClass
    public static void setup() {
        
        _objectMapper = new ObjectMapper();
        
        _validResult = new LinkedHashMap<>();
        _validResult.put(500L, "Mocked 500"); //5 count
        _validResult.put(200L, "Mocked 200"); //2 count
        _validResult.put(100L, "Mocked 100"); //1 count
        
    }
    
    @Test
    public void testReadConversion() {
        
        Set<String> testData = new LinkedHashSet<>();
        
        try {
            
            testData.add(_objectMapper.writeValueAsString(createTempMap()));
            
            Map<Long, String> tpSubscriptions = PulseServiceUtil.processTrendingPulseSubscribe(testData, _objectMapper);
            
            assertTrue("Size equal?", tpSubscriptions.size() == _validResult.size());
            
            Iterator<Entry<Long, String>> validIterator = _validResult.entrySet().iterator();
            Iterator<Entry<Long, String>> tpProcessedIterator = tpSubscriptions.entrySet().iterator();
            
            int loop = 0;
            while(validIterator.hasNext() && tpProcessedIterator.hasNext()) {
                Entry<Long, String> vEntry = validIterator.next();
                Entry<Long, String> tpEntry = tpProcessedIterator.next();
                
                assertTrue("For loop " + loop + " check " + vEntry + "-" + tpEntry, vEntry.equals(tpEntry));
                loop++;
            }
            
        } catch (JsonProcessingException jpException) {
            assertTrue("Error while writing to string " + jpException.getMessage(), false);
        }
        
    }
    
    private static Map<String, Integer> createTempMap() {
        Map<String, Integer> temp = new HashMap<>();
        
        //Structure is <id>0x07<value>0x13<timestamp> -> count; i.e. {"10020x07Mocked 10020x13<timestamp>" -> 1}
        //technically multiple Map -> String exists in the DB, but for now use single one for test
        String joinedKey = String.join("", "500", CommonConstants.TIME_INTERVAL_ID_VALUE_DELIM, "Mocked 500",
                CommonConstants.TIME_INTERVAL_PERSIST_TIMESTAMP_DELIM, System.nanoTime() + "");
        
        temp.put(joinedKey, 3);
        
        joinedKey = String.join("", "500", CommonConstants.TIME_INTERVAL_ID_VALUE_DELIM, "Mocked 500",
                CommonConstants.TIME_INTERVAL_PERSIST_TIMESTAMP_DELIM, System.nanoTime() + "");
        
        temp.put(joinedKey, 1);
        
        joinedKey = String.join("", "500", CommonConstants.TIME_INTERVAL_ID_VALUE_DELIM, "Mocked 500",
                CommonConstants.TIME_INTERVAL_PERSIST_TIMESTAMP_DELIM, System.nanoTime() + "");
        
        temp.put(joinedKey, 1);
        
        joinedKey = String.join("", "200", CommonConstants.TIME_INTERVAL_ID_VALUE_DELIM, "Mocked 200",
                CommonConstants.TIME_INTERVAL_PERSIST_TIMESTAMP_DELIM, System.nanoTime() + "");
        
        temp.put(joinedKey, 1);
        
        joinedKey = String.join("", "200", CommonConstants.TIME_INTERVAL_ID_VALUE_DELIM, "Mocked 200",
                CommonConstants.TIME_INTERVAL_PERSIST_TIMESTAMP_DELIM, System.nanoTime() + "");
        
        temp.put(joinedKey, 1);
        
        joinedKey = String.join("", "100", CommonConstants.TIME_INTERVAL_ID_VALUE_DELIM, "Mocked 100",
                CommonConstants.TIME_INTERVAL_PERSIST_TIMESTAMP_DELIM, System.nanoTime() + "");
        
        temp.put(joinedKey, 1);
        return temp;
    }

}
