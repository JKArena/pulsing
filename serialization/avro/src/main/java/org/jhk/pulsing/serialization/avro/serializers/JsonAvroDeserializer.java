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
package org.jhk.pulsing.serialization.avro.serializers;

import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * @author Ji Kim
 */
public class JsonAvroDeserializer<T extends SpecificRecord> extends StdDeserializer<T> {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(JsonAvroDeserializer.class);
    
    private Class<T> _type;
    private Schema _schema;
    private ObjectMapper _mapper = new ObjectMapper();
    
    public JsonAvroDeserializer(Class<T> type, Schema schema) {
        super(type);
        
        _type = type;
        _schema = schema;
    }

    /* 
     * Strangely even if the fields are schemed to be optional, they must come with null values in json format to be deserialized properly
     * 
     * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
     */
    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        
        JsonNode node = _mapper.readTree(parser);
        
        _LOGGER.info("JsonAvroDeserializer.deserialize: " + (node != null ? node.toString() : ""));
        return SerializationHelper.deserializeFromJSONStringToAvro(_type, _schema, node.toString());
    }
    
}
