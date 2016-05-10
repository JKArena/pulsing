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
package org.jhk.interested.serialization.avro.serializers;

import java.io.IOException;
import java.util.Map;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serializer;


/**
 * @author Ji Kim
 */
public final class KafkaAvroJSONSerializer implements Serializer<SpecificRecord> {
    
    @Override
    public void close() {
        
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        
    }

    @Override
    public byte[] serialize(String topic, SpecificRecord data) {
        
        String result = "";
        
        try {
            result = SerializationHelper.serializeAvroTypeToJSONString(data);
        } catch (IOException ioExcept) {
            ioExcept.printStackTrace();
        }
        
        return result.getBytes();
    }
    
}
