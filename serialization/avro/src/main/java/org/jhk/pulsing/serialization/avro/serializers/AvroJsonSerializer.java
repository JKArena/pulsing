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

import org.apache.avro.specific.SpecificRecord;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * @author Ji Kim
 */
public class AvroJsonSerializer<T extends SpecificRecord> extends StdSerializer<T> {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(AvroJsonSerializer.class);

    private static final long serialVersionUID = 1754524804919512789L;

    public AvroJsonSerializer(Class<T> type) {
        super(type);
    }

    @Override
    public void serialize(T value, JsonGenerator jgen, SerializerProvider provider)throws IOException {
        
        _LOGGER.info("AvroJsonSerializer.serialize: " + value.getClass().getName() + " -> " + value);
        
        jgen.writeString(SerializationHelper.serializeAvroTypeToJSONString(value));
        
    }

}
