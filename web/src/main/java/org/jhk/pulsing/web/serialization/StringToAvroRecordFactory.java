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
package org.jhk.pulsing.web.serialization;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.Schema;

import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.PulseId;
import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * @author Ji Kim
 */
public final class StringToAvroRecordFactory implements ConverterFactory<String, SpecificRecord> {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(StringToAvroRecordFactory.class);
    
    private static final Map<Class<? extends SpecificRecord>, Schema> _MAPPER = new HashMap<>();
    
    static {
        _MAPPER.put(Pulse.class, Pulse.getClassSchema());
        _MAPPER.put(PulseId.class, PulseId.getClassSchema());
        _MAPPER.put(User.class, User.getClassSchema());
        _MAPPER.put(UserId.class, UserId.getClassSchema());
    }
    
    @Override
    public <T extends SpecificRecord> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToAvroRecord(targetType);
    }
    
    private final class StringToAvroRecord<T extends SpecificRecord> implements Converter<String, T> {
        
        private Class<T> _avroType;
        
        public StringToAvroRecord(Class<T> avroType) {
            _avroType = avroType;
        }

        @Override
        public T convert(String source) {
            _LOGGER.info("Converting type " + _avroType.getName() + " : " + source);
            T converted = null;
            
            try {
                converted = SerializationHelper.deserializeFromJSONStringToAvro(_avroType, _MAPPER.get(_avroType), source);
            }catch(IOException deserializedException) {
                _LOGGER.error("Error in deserializing " + source, deserializedException);
            }
            
            return converted;
        }
        
    }
    
}
