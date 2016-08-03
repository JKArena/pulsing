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

import java.util.Map;
import java.util.PriorityQueue;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.codehaus.jackson.map.ObjectMapper;
import static org.jhk.pulsing.storm.common.FieldConstants.*;
import org.jhk.pulsing.shared.util.RedisConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

/**
 * Use DRPC instead w/ memory?
 * 
 * @author Ji Kim
 */
public final class TimeIntervalPersistorBolt extends BaseBasicBolt {
    
    private static final long serialVersionUID = -884268616402022174L;
    private static final Logger _LOG = LoggerFactory.getLogger(TimeIntervalPersistorBolt.class);
    private static final int HEAP_MAX_SIZE = 20;
    
    private Jedis _jedis;
    private ObjectMapper _objectMapper;
    
    public TimeIntervalPersistorBolt() {
        super();
    }
    
    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        _jedis = new Jedis(RedisConstants.REDIS_HOST, RedisConstants.REDIS_PORT);
        _jedis.auth(RedisConstants.REDIS_PASSWORD);
        _objectMapper = new ObjectMapper();
    }
    
    @Override
    public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
        _LOG.debug("TimeIntervalPersistorBolt.execute: " + tuple);
        
        Long timeInterval = tuple.getLongByField(TIME_INTERVAL);
        Map<Long, Integer> counter = (Map<Long, Integer>) tuple.getValueByField(ID_COUNTER_MAP);
        
        //make it a min heap to constrain the HEAP_MAX_SIZE
        PriorityQueue<Counter> queue = new PriorityQueue<Counter>();
        
        for(Long id : counter.keySet()) {
            int count = counter.get(id);
            
            if(queue.size() < HEAP_MAX_SIZE) {
                queue.offer(new Counter(id, count));
            }else if(queue.size() < HEAP_MAX_SIZE) {
                queue.poll();
                queue.offer(new Counter(id, count));
            }
        }
        
        try {
            String timeIntervalSubscription = _objectMapper.writeValueAsString(queue);
            _jedis.setex(RedisConstants.REDIS_KEY.TRENDING_PULSE_.toString() + timeInterval, RedisConstants.DEFAULT_CACHE_EXPIRE_SECONDS, timeIntervalSubscription);
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
    
    private class Counter implements Comparable<Counter> {
        
        private Long _id;
        private int _count;
        
        private Counter(Long id, int count) {
            super();
            
            _id = id;
            _count = count;
        }
        
        @Override
        public int compareTo(Counter compare) {
            return _count - compare._count;
        }
        
    }
    
}
