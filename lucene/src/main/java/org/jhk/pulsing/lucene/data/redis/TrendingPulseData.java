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
package org.jhk.pulsing.lucene.data.redis;

import static org.jhk.pulsing.shared.util.RedisConstants.REDIS_KEY.PULSE_TRENDING_SUBSCRIBE_;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import redis.clients.jedis.Jedis;

import org.jhk.pulsing.shared.processor.PulseProcessor;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.shared.util.RedisConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fetch trending pulse from Redis that is pushed from Storm for boosting the search in Lucene
 * 
 * @author Ji Kim
 */
public final class TrendingPulseData implements IData<Long>{
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(TrendingPulseData.class);
    private static final int _LIMIT = 100;
    private static final int _PAST_MINUTES = 30;
    
    private Jedis _jedis;
    
    public TrendingPulseData() {
        super();
        
        _jedis = new Jedis(RedisConstants.REDIS_HOST, RedisConstants.REDIS_PORT);
        _jedis.auth(RedisConstants.REDIS_PASSWORD);
    }

    @Override
    public Collection<Long> getData() {
        Instant current = Instant.now();
        Instant beforeRange = current.minus(_PAST_MINUTES, ChronoUnit.MINUTES);
        
        Set<String> tpSubscribe = _jedis.zrangeByScore(PULSE_TRENDING_SUBSCRIBE_.toString(), beforeRange.getEpochSecond(), current.getEpochSecond(), 0, _LIMIT);
        
        @SuppressWarnings("unchecked")
        Set<Long> data = Collections.EMPTY_SET;
        
        Map<String, Integer> count = PulseProcessor.countTrendingPulseSubscribe(tpSubscribe);
        
        if(count.size() > 0) {
            data = count.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .map(entry -> Long.parseLong(entry.getKey().split(CommonConstants.TIME_INTERVAL_ID_VALUE_DELIM)[0]))
                    .collect(Collectors.toSet());
        }
        
        _LOGGER.debug("TrendingPulseData.getData: data {}", data);
        return data;
    }

}
