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
package org.jhk.pulsing.storm.converter;

import static org.jhk.pulsing.storm.common.FieldConstants.AVRO;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.storm.tuple.ITuple;
import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.User;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class AvroToElasticDocumentConverter {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(AvroToElasticDocumentConverter.class);
    
    private static final EnumMap<AVRO_TO_ELASTIC_DOCUMENT, Function<ITuple, JSONObject>> _AVRO_TO_ELASTIC_DOC_MAPPER = new EnumMap<>(AVRO_TO_ELASTIC_DOCUMENT.class);
    
    public static enum AVRO_TO_ELASTIC_DOCUMENT {
        PULSE, USER;
    }
    
    static {
        _AVRO_TO_ELASTIC_DOC_MAPPER.put(AVRO_TO_ELASTIC_DOCUMENT.PULSE, AvroToElasticDocumentConverter::convertPulseAvroToElasticDoc);
        _AVRO_TO_ELASTIC_DOC_MAPPER.put(AVRO_TO_ELASTIC_DOCUMENT.USER, AvroToElasticDocumentConverter::convertUserAvroToElasticDoc);
    }
    
    public static Function<ITuple, JSONObject> getAvroToElasticDocFunction(AVRO_TO_ELASTIC_DOCUMENT avroType) {
        return _AVRO_TO_ELASTIC_DOC_MAPPER.get(avroType);
    }
    
    /**
     * Simple converter from Avro Pulse to JSONObject for ElasticSearch as Avro's json string is 
     * encryptic and causes parse errors for regular json builders. Just using jackson now but may 
     * be swap out with gson later.
     * 
     * @param tuple
     * @return
     */
    private static JSONObject convertPulseAvroToElasticDoc(ITuple tuple) {
        _LOGGER.info("AvroToElasticDocumentConverter.convertPulseAvroToElasticDoc " + tuple);
        
        Pulse pulse = (Pulse) tuple.getValueByField(AVRO);
        
        List<String> tags = pulse.getTags().stream()
                .map(value -> value.toString())
                .collect(Collectors.<String> toList());
        
        JSONObject obj = new JSONObject();
        
        obj.put("description", pulse.getDescription() != null ? pulse.getDescription().toString() : "");
        obj.put("name", pulse.getValue() != null ? pulse.getValue().toString() : "");
        obj.put("user_id", pulse.getUserId().getId());
        obj.put("timestamp", pulse.getTimeStamp());
        obj.put("tags", tags);
        
        return obj;
    }
    
    private static JSONObject convertUserAvroToElasticDoc(ITuple tuple) {
        _LOGGER.info("AvroToElasticDocumentConverter.convertUserAvroToElasticDoc " + tuple);
        
        User user = (User) tuple.getValueByField(AVRO);
        
        JSONObject obj = new JSONObject();
        
        obj.put("name", user.getName().toString());
        obj.put("email", user.getEmail().toString());
        
        return obj;
    }
    
    private AvroToElasticDocumentConverter() {
        super();
    }
    
}
