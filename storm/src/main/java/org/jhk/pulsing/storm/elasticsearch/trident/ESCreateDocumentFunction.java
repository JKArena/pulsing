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
package org.jhk.pulsing.storm.elasticsearch.trident;

import java.util.Map;
import java.util.function.Function;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.ITuple;
import org.apache.storm.tuple.Values;
import org.elasticsearch.node.NodeValidationException;
import org.jhk.pulsing.storm.common.ConverterCommon;
import org.jhk.pulsing.storm.common.FieldConstants;
import org.jhk.pulsing.storm.elasticsearch.NativeClient;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class ESCreateDocumentFunction extends BaseFunction {

    private static final long serialVersionUID = 8917913390559160418L;
    private static final Logger _LOGGER = LoggerFactory.getLogger(ESCreateDocumentFunction.class);
    
    private String _index;
    private String _docType;
    private NativeClient _nClient;
    private ConverterCommon.AVRO_TO_ELASTIC_JSON _avroType;
    private Function<ITuple, JSONObject> _toJsonConverter;
    
    public ESCreateDocumentFunction(ConverterCommon.AVRO_TO_ELASTIC_JSON avroType, String index, String docType) {
        super();
        
        _avroType = avroType;
        _index = index;
        _docType = docType;
    }
    
    @Override
    public void prepare(Map conf, TridentOperationContext context) {
        super.prepare(conf, context);
        
        try {
            _nClient = new NativeClient();
        } catch (NodeValidationException nvException) {
            nvException.printStackTrace();
            throw new RuntimeException(nvException);
        }
        
        _toJsonConverter = ConverterCommon.getAvroToElasticJsonFunction(_avroType);
    }

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        _LOGGER.info("ESCreateDocumentFunction.execute: " + tuple);
        
        String id = tuple.getValueByField(FieldConstants.ID).toString();
        
        _nClient.addDocument(_index, _docType, id, _toJsonConverter.apply(tuple).toString());
        collector.emit(new Values(tuple.getValueByField(FieldConstants.AVRO)));
        
        _LOGGER.info("ESCreateDocumentFunction.execute: ADDED" + tuple);
        
    }

}
