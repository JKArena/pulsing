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
package org.jhk.pulsing.storm.bolts.deserializers;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.ITuple;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.jhk.pulsing.storm.common.FieldConstants;
import org.jhk.pulsing.storm.deserializer.StringToAvroDeserializedValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class AvroDeserializerBolt extends BaseBasicBolt {
    
    private static final long serialVersionUID = -6065528421670008189L;
    private static final Logger _LOGGER = LoggerFactory.getLogger(AvroDeserializerBolt.class);
    
    private StringToAvroDeserializedValues.STRING_TO_AVRO_VALUES _avroType;
    private boolean _includeId;
    private Fields _fields;
    
    private BiFunction<ITuple, Boolean, Values> _toAvroDeserializer;
    
    public AvroDeserializerBolt(StringToAvroDeserializedValues.STRING_TO_AVRO_VALUES avroType, boolean includeId) {
        super();
        
        _avroType = avroType;
        _includeId = includeId;
        _fields = includeId ? FieldConstants.AVRO_DESERIALIZE_WITH_ID_FIELD : FieldConstants.AVRO_DESERIALIZE_FIELD;
    }
    
    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        
        _toAvroDeserializer = StringToAvroDeserializedValues.getStringToAvroValuesBiFunction(_avroType);
    }
    
    @Override
    public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
        _LOGGER.info("AvroDeserializerBolt.execute: {}", tuple);
        
        outputCollector.emit(_toAvroDeserializer.apply(tuple, _includeId));
    }
    
    @Override
    public void declareOutputFields(OutputFieldsDeclarer fieldsDeclarer) {
        fieldsDeclarer.declare(_fields);
    }

}
