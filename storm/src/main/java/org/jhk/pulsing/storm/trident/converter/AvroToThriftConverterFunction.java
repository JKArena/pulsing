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
package org.jhk.pulsing.storm.trident.converter;

import java.util.Map;
import java.util.function.Function;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.ITuple;
import org.apache.storm.tuple.Values;
import org.jhk.pulsing.storm.converter.AvroToThriftConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class AvroToThriftConverterFunction extends BaseFunction {
    
    private static final long serialVersionUID = -6929267349873708590L;
    private static final Logger _LOGGER = LoggerFactory.getLogger(AvroToThriftConverterFunction.class);
    
    private AvroToThriftConverter.AVRO_TO_THRIFT _avroType;
    
    private Function<ITuple, Object> _toThriftConverter;
    
    public AvroToThriftConverterFunction(AvroToThriftConverter.AVRO_TO_THRIFT avroType) {
        super();
        
        _avroType = avroType;
    }
    
    @Override
    public void prepare(Map conf, TridentOperationContext context) {
        super.prepare(conf, context);
        
        _toThriftConverter = AvroToThriftConverter.getAvroToThriftFunction(_avroType);
    }
    
    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        _LOGGER.info("AvroToThriftConverterFunction.execute " + tuple);
        
        Object data = _toThriftConverter.apply(tuple);
        
        _LOGGER.info("Converted to thrift " + data);
        
        collector.emit(new Values(data));
    }

}
