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
package org.jhk.pulsing.storm.trident.deserializers.avro;

import java.util.Map;
import java.util.function.BiFunction;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.ITuple;
import org.apache.storm.tuple.Values;
import org.jhk.pulsing.storm.deserializer.StringToAvroDeserializedValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class AvroDeserializerFunction extends BaseFunction {
    
    private static final long serialVersionUID = 4955780336211716483L;
    private static final Logger _LOGGER = LoggerFactory.getLogger(AvroDeserializerFunction.class);
    
    private StringToAvroDeserializedValues.STRING_TO_AVRO_VALUES _avroType;
    private boolean _includeId;
    
    private BiFunction<ITuple, Boolean, Values> _toAvroDeserializer;
    
    public AvroDeserializerFunction(StringToAvroDeserializedValues.STRING_TO_AVRO_VALUES avroType, boolean includeId) {
        super();
        
        _avroType = avroType;
        _includeId = includeId;
    }
    
    @Override
    public void prepare(Map conf, TridentOperationContext context) {
        super.prepare(conf, context);
        
        _toAvroDeserializer = StringToAvroDeserializedValues.getStringToAvroValuesBiFunction(_avroType);
    }
    
    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        _LOGGER.info("AvroDeserializerFunction.execute: " + tuple);
        
        collector.emit(_toAvroDeserializer.apply(tuple, _includeId));
        
    }

}
