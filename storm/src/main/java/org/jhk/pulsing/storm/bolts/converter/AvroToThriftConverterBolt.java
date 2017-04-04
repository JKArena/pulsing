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
package org.jhk.pulsing.storm.bolts.converter;

import java.util.Map;
import java.util.function.Function;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.ITuple;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.jhk.pulsing.storm.converter.AvroToThriftConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class AvroToThriftConverterBolt extends BaseBasicBolt {
    
    private static final long serialVersionUID = -525343954401412897L;
    private static final Logger _LOGGER = LoggerFactory.getLogger(AvroToThriftConverterBolt.class);
    
    private AvroToThriftConverter.AVRO_TO_THRIFT _avroType;
    private Fields _fields;
    
    private Function<ITuple, Object> _toThriftConverter;
    
    public AvroToThriftConverterBolt(AvroToThriftConverter.AVRO_TO_THRIFT avroType, Fields fields) {
        super();
        
        _avroType = avroType;
        _fields = fields;
    }
    
    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        
        _toThriftConverter = AvroToThriftConverter.getAvroToThriftFunction(_avroType);
    }
    
    @Override
    public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
        _LOGGER.info("AvroToThriftConverter.execute " + tuple);
        
        Object data = _toThriftConverter.apply(tuple);
        
        _LOGGER.info("Converted to thrift " + data);
        outputCollector.emit(new Values(data));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer fieldsDeclarer) {
        fieldsDeclarer.declare(_fields);
    }

}
