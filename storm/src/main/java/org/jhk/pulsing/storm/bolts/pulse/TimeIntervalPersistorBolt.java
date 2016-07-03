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
package org.jhk.pulsing.storm.bolts.pulse;

import java.util.Map;
import java.util.PriorityQueue;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.codehaus.jackson.map.ObjectMapper;
import org.jhk.pulsing.shared.util.PulsingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

/**
 * Use DRPC instead w/ memory?
 * 
 * @author Ji Kim
 */
public final class TimeIntervalPersistorBolt extends BaseBasicBolt {
    
    private static final Logger _LOG = LoggerFactory.getLogger(TimeIntervalPersistorBolt.class);
    private static final long serialVersionUID = -884268616402022174L;
    private static final int HEAP_MAX_SIZE = 20;
    
    private Jedis _jedis;
    private ObjectMapper _objectMapper;
    private int _secondsInterval;
    
    public TimeIntervalPersistorBolt() {
        super();

        _secondsInterval = PulsingConstants.DEFAULT_INTERVAL_SECONDS;
    }
    
    public TimeIntervalPersistorBolt(int secondsInterval) {
        super();
        
        _secondsInterval = secondsInterval;
    }
    
    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        _jedis = new Jedis(PulsingConstants.REDIS_HOST, PulsingConstants.REDIS_PORT);
        _objectMapper = new ObjectMapper();
    }
    
    @Override
    public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
        _LOG.debug("TimeIntervalPersistorBolt.execute: " + tuple);
        
        Long timeInterval = tuple.getLongByField("timeInterval");
        Map<Long, Integer> counter = (Map<Long, Integer>) tuple.getValueByField("idCounterMap");
        
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
            _jedis.setex("trend-pulse-" + timeInterval, _secondsInterval, timeIntervalSubscription);
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
        
        public Long getId() {
            return _id;
        }
        public int getCount() {
            return _count;
        }
        
    }
    
}
