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
package org.jhk.pulsing.storm.bolts.persistor;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.codehaus.jackson.map.ObjectMapper;
import static org.jhk.pulsing.storm.common.FieldConstants.*;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.shared.util.RedisConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

/**
 * @author Ji Kim
 */
public final class TimeIntervalPersistorBolt extends BaseBasicBolt {
    
    private static final long serialVersionUID = -884268616402022174L;
    private static final Logger _LOGGER = LoggerFactory.getLogger(TimeIntervalPersistorBolt.class);
    
    private Jedis _jedis;
    private ObjectMapper _objectMapper;
    private String _redisZKey;
    
    public TimeIntervalPersistorBolt(String redisZKey) {
        super();
        
        _redisZKey = redisZKey;
    }
    
    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext context) {
        _jedis = new Jedis(RedisConstants.REDIS_HOST, RedisConstants.REDIS_PORT);
        _jedis.auth(RedisConstants.REDIS_PASSWORD);
        _objectMapper = new ObjectMapper();
    }
    
    @Override
    public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
        _LOGGER.info("TimeIntervalPersistorBolt.execute: " + tuple);
        
        long timeStamp = Instant.now().getEpochSecond();
        @SuppressWarnings("unchecked")
        Map<String, Integer> obj = (Map<String, Integer>) tuple.getValueByField(TIME_INTERVAL_VALUE_COUNTER_MAP);
        
        obj = obj.entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> entry.getKey() + CommonConstants.TIME_INTERVAL_PERSIST_TIMESTAMP_DELIM + timeStamp,
                    entry -> entry.getValue()
                    ));
        
        //When displaying query from whenever-to-whenever time interval range, union and return
        try {
            String timeIntervalSubscription = _objectMapper.writeValueAsString(obj);
            
            _LOGGER.info("TimeIntervalPersistorBolt.execute: putting " + timeStamp + "/" + timeIntervalSubscription);
            _jedis.zadd(_redisZKey, (double) timeStamp, timeIntervalSubscription);
        } catch (Exception writeException) {
            writeException.printStackTrace();
        }
    }
    
    @Override
    public void cleanup() {
        if(_jedis.isConnected()) {
            _jedis.quit();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer fieldsDeclarer) {
    }
    
}
