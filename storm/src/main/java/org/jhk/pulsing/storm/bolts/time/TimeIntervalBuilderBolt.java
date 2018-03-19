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
package org.jhk.pulsing.storm.bolts.time;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.Constants;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.shared.util.Util;
import static org.jhk.pulsing.storm.common.FieldConstants.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class TimeIntervalBuilderBolt extends BaseBasicBolt {
    
    private static final long serialVersionUID = -94783556828622026L;
    private static final Logger _LOGGER = LoggerFactory.getLogger(TimeIntervalBuilderBolt.class);
    
    private Map<Long, Map<String, Integer>> _timeInterval;
    private int _secondsInterval;
    
    public TimeIntervalBuilderBolt() {
        this(CommonConstants.DEFAULT_STORM_INTERVAL_SECONDS);
    }
    
    public TimeIntervalBuilderBolt(int secondsInterval) {
        super();
        
        _secondsInterval = secondsInterval;
    }
    
    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config config = new Config();
        config.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, CommonConstants.DEFAULT_STORM_TICK_TUPLE_FREQ_SECONDS);
        return config;
    }
    
    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext context) {
        _timeInterval = new HashMap<>();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
        _LOGGER.info("TimeIntervalBuilderBolt.execute: {}", tuple);
        
        if(isTickTuple(tuple)) {
            processTickTuple(outputCollector);
        }else {
            processTimeIntervalValue(tuple, tuple.getLongByField(TIME_INTERVAL));
        }
        
    }
    
    /**
     * Whether the tuple is from system or not
     * 
     * @param tuple
     * @return
     */
    private boolean isTickTuple(Tuple tuple) {
        String sourceComponent = tuple.getSourceComponent();
        String sourceStreamId = tuple.getSourceStreamId();
        return sourceComponent.equals(Constants.SYSTEM_COMPONENT_ID) && sourceStreamId.equals(Constants.SYSTEM_TICK_STREAM_ID);
    }
    
    private void processTickTuple(BasicOutputCollector outputCollector) {
        
        long currTimeInterval = Util.getTimeInterval(Instant.now().getEpochSecond(), _secondsInterval);
        List<Long> toRemoveTI = new LinkedList<>();
        
        _LOGGER.info("TimeIntervalBuilderBolt.processTickTuple: currTimeInterval={}, timeInterval={}", currTimeInterval, _timeInterval);
        
        _timeInterval.keySet().parallelStream()
            .filter(entryTI -> (entryTI <= currTimeInterval))
            .forEach(filteredTI -> {
                //the entry is past the current interval so emit them
                toRemoveTI.add(filteredTI);
                
                Map<String, Integer> tIValueCounter = _timeInterval.get(filteredTI);
                
                _LOGGER.info("TimeIntervalBuilderBolt.processTickTuple EMIT: filteredTI={}, tIValueCounter={}", filteredTI, tIValueCounter);
                outputCollector.emit(new Values(filteredTI, tIValueCounter));
            });
        
        toRemoveTI.stream()
            .forEach(entry -> { _timeInterval.remove(entry); });
    }
    
    private void processTimeIntervalValue(Tuple tuple, Long timeInterval) {
        String tIValue = tuple.getStringByField(TIME_INTERVAL_VALUE);
        
        _LOGGER.info("TimeIntervalBuilderBolt.processTimeIntervalValue: tIValue={}, timeInterval={}, timeInterval={}", 
                tIValue, timeInterval, _timeInterval);
        
        Map<String, Integer> count = _timeInterval.get(timeInterval);
        if(count == null) {
            count = new HashMap<>();
            _timeInterval.put(timeInterval, count);
        }
        
        count.compute(tIValue, (key, oldValue) -> oldValue == null ? 1 : oldValue + 1);
        _LOGGER.info("TimeIntervalBuilderBolt.processTimeIntervalValue after compute: timeInterval={}", _timeInterval);
    }
    
    @Override
    public void declareOutputFields(OutputFieldsDeclarer fieldsDeclarer) {
        fieldsDeclarer.declare(new Fields(TIME_INTERVAL, TIME_INTERVAL_VALUE_COUNTER_MAP));
    }
    
}
