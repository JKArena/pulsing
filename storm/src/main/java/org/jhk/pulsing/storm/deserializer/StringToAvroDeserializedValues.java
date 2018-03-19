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
package org.jhk.pulsing.storm.deserializer;

import java.io.IOException;
import java.util.EnumMap;
import java.util.function.BiFunction;

import org.apache.storm.tuple.ITuple;
import org.apache.storm.tuple.Values;
import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class StringToAvroDeserializedValues {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(StringToAvroDeserializedValues.class);
    
    private static final EnumMap<STRING_TO_AVRO_VALUES, BiFunction<ITuple, Boolean, Values>> _STRING_TO_AVRO_VALUES_MAPPER = new EnumMap<>(STRING_TO_AVRO_VALUES.class);
    
    public static enum STRING_TO_AVRO_VALUES {
        PULSE, USER;
    }
    
    static {
        _STRING_TO_AVRO_VALUES_MAPPER.put(STRING_TO_AVRO_VALUES.PULSE, StringToAvroDeserializedValues::deserializeStringToPulseAvroValues);
        _STRING_TO_AVRO_VALUES_MAPPER.put(STRING_TO_AVRO_VALUES.USER, StringToAvroDeserializedValues::deserializeStringToUserAvroValues);
    }
    
    public static BiFunction<ITuple, Boolean, Values> getStringToAvroValuesBiFunction(STRING_TO_AVRO_VALUES avroType) {
        return _STRING_TO_AVRO_VALUES_MAPPER.get(avroType);
    }
    
    private static Values deserializeStringToPulseAvroValues(ITuple tuple, Boolean includeId) {
        _LOGGER.info("StringToAvroDeserializedValues.deserializeStringToPulseAvroValues: tuple={}, includeId={}", tuple, includeId);
        
        String pulseString = tuple.getString(0);
        Values values = null;
        
        try {
            
            Pulse pulse = SerializationHelper.deserializeFromJSONStringToAvro(Pulse.class, Pulse.getClassSchema(), pulseString);
            values = new Values(pulse);
            
            if(includeId) {
                values.add(pulse.getId().getId());
            }
            
        } catch (IOException decodeException) {
            throw new RuntimeException(decodeException);
        }
        
        return values;
    }
    
    private static Values deserializeStringToUserAvroValues(ITuple tuple, Boolean includeId) {
        _LOGGER.info("StringToAvroDeserializedValues.deserializeStringToUserAvroValues: tuple={}, includeId={}", tuple, includeId);
        
        String userString = tuple.getString(0);
        Values values = null;
        
        try {
            
            User user = SerializationHelper.deserializeFromJSONStringToAvro(User.class, User.getClassSchema(), userString);
            values = new Values(user);
            
            if(includeId) {
                values.add(user.getId().getId());
            }
            
        } catch (IOException decodeException) {
            throw new RuntimeException(decodeException);
        }
        
        return values;
    }
    
    
    private StringToAvroDeserializedValues() {
        super();
    }

}
