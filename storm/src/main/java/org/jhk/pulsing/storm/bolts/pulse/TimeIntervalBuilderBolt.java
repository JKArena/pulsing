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

import java.util.HashMap;
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
import org.jhk.pulsing.storm.util.CommonBoltStreamUtil;
import org.jhk.pulsing.storm.util.PulsingConstants;

/**
 * @author Ji Kim
 */
public final class TimeIntervalBuilderBolt extends BaseBasicBolt {

    private static final long serialVersionUID = -94783556828622026L;
    
    private static final int DEFAULT_TICK_TUPLE_FREQ_SECONDS = 60;
    
    private Map<Long, Map<Long, Integer>> _timeInterval;
    private int _secondsInterval;
    
    public TimeIntervalBuilderBolt() {
        this(PulsingConstants.DEFAULT_INTERVAL_SECONDS);
    }
    
    public TimeIntervalBuilderBolt(int secondsInterval) {
        super();
        
        _secondsInterval = secondsInterval;
    }
    
    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config config = new Config();
        config.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, DEFAULT_TICK_TUPLE_FREQ_SECONDS);
        return config;
    }
    
    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        _timeInterval = new HashMap<>();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
        
        if(isTickTuple(tuple)) {
            processTickTuple(outputCollector);
        }else {
            processTimeIntervalValue(tuple, tuple.getLongByField("timeInterval"));
        }
        
    }
    
    private boolean isTickTuple(Tuple tuple) {
        String sourceComponent = tuple.getSourceComponent();
        String sourceStreamId = tuple.getSourceStreamId();
        return sourceComponent.equals(Constants.SYSTEM_COMPONENT_ID) && sourceStreamId.equals(Constants.SYSTEM_TICK_STREAM_ID);
    }
    
    private void processTickTuple(BasicOutputCollector outputCollector) {
        
        Long currTimeInterval = CommonBoltStreamUtil.getTimeInterval(System.nanoTime(), _secondsInterval);
        
        for(Long entryTI : _timeInterval.keySet()) {
            if(entryTI <= currTimeInterval) {
                Map<Long, Integer> idValueCounter = _timeInterval.remove(entryTI);
                outputCollector.emit(new Values(entryTI, idValueCounter));
            }
        }
    }
    
    private void processTimeIntervalValue(Tuple tuple, Long timeInterval) {
        Long id = tuple.getLongByField("id");
        
        //convert below to java 8 also
        Map<Long, Integer> count = _timeInterval.get(timeInterval);
        if(count == null) {
            count = new HashMap<>();
            _timeInterval.put(timeInterval, count);
        }
        
        count.compute(id, (key, oldValue) -> oldValue == null ? 1 : oldValue + 1);
    }
    
    @Override
    public void declareOutputFields(OutputFieldsDeclarer fieldsDeclarer) {
        fieldsDeclarer.declare(new Fields("timeInterval", "idCounterMap"));
    }
    
}
