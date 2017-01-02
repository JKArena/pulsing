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
package org.jhk.pulsing.web.service.prod.helper;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jhk.pulsing.shared.util.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Ji Kim
 */
public class PulseServiceUtil {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(PulseServiceUtil.class);
    
    private static ObjectMapper _OBJECT_MAPPER = new ObjectMapper();
    
    private static final TypeReference<HashMap<String, Integer>> _TRENDING_PULSE_SUBSCRIPTION_TYPE_REF = 
            new TypeReference<HashMap<String, Integer>>(){};
    
    public static Map<Long, String> processTrendingPulseSubscribe(Set<String> tps) {
        
        @SuppressWarnings("unchecked")
        Map<Long, String> tpSubscriptions = Collections.EMPTY_MAP;
        final Map<String, Integer> count = new HashMap<>();
        
        tps.parallelStream().forEach(tpsIdValueCounts -> {
            
            try {
                _LOGGER.debug("PulseServiceUtil.processTrendingPulseSubscribe: trying to convert " + tpsIdValueCounts);
                
                Map<String, Integer> converted = _OBJECT_MAPPER.readValue(tpsIdValueCounts, _TRENDING_PULSE_SUBSCRIPTION_TYPE_REF);
                
                _LOGGER.debug("PulseServiceUtil.processTrendingPulseSubscribe: sucessfully converted " + converted.size());
                
                //Structure is <id>0x07<value>0x13<timestamp> -> count; i.e. {"10020x07Mocked 10020x13<timestamp>" -> 1}
                //Need to split the String content, gather the count for the searched interval
                //and return the sorted using Java8 stream
                //TODO impl better
                
                Map<String, Integer> computed = converted.entrySet().stream()
                    .reduce(
                            new HashMap<String, Integer>(),
                            (Map<String, Integer> mapped, Entry<String, Integer> entry) -> {
                                String[] split = entry.getKey().split(CommonConstants.TIME_INTERVAL_PERSIST_TIMESTAMP_DELIM);
                                Integer value = entry.getValue();

                                mapped.compute(split[0], (key, val) -> {
                                    return val == null ? value : val+value; 
                                });

                                return mapped;
                            },
                            (Map<String, Integer> result, Map<String, Integer> aggregated) -> {
                                result.putAll(aggregated);
                                return result;
                            }
                            );
                
                computed.entrySet().parallelStream()
                    .forEach(entry -> {
                        Integer value = entry.getValue();
                        
                        count.compute(entry.getKey(), (key, val) -> {
                            return val == null ? value : val+value;
                        });
                    });
                
            } catch (Exception cException) {
                cException.printStackTrace();
            }
        });
        
        if(count.size() > 0) {
            tpSubscriptions = count.entrySet().stream()
                                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                .collect(Collectors.toMap(
                                        entry -> Long.parseLong(entry.getKey().split(CommonConstants.TIME_INTERVAL_ID_VALUE_DELIM)[0]),
                                        entry -> entry.getKey().split(CommonConstants.TIME_INTERVAL_ID_VALUE_DELIM)[1],
                                        (x, y) -> {throw new AssertionError();},
                                        LinkedHashMap::new
                                        ));
        }
        
        return tpSubscriptions;
    }
    
    private PulseServiceUtil() {
        super();
    }
    
}
