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
package org.jhk.interested.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonDecoder;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.jhk.interested.serialization.pojo.IAddress;
import org.jhk.interested.serialization.pojo.IUser;

/**
 * @author Ji Kim
 */
public final class SerializationFactory {
    
    private static final Map<Class<?>, Schema> AVRO_MAPPER = new HashMap<>();
    private static final Map<Class<?>, Class<?>> THRIFT_MAPPER = new HashMap<>();
    
    static {
        AVRO_MAPPER.put(IAddress.class, org.jhk.interested.serialization.avro.Address.getClassSchema());
        AVRO_MAPPER.put(IUser.class, org.jhk.interested.serialization.avro.User.getClassSchema());
        
        THRIFT_MAPPER.put(IAddress.class, org.jhk.interested.serialization.thrift.Address.class);
        THRIFT_MAPPER.put(IUser.class, org.jhk.interested.serialization.thrift.User.class);
    }
    
    private SerializationFactory() {
        super();
    }
    
    public static <T> T decodeFromJSONStringToType(Class<T> clazz, String jsonString) throws IOException {
        return decodeFromJSONStringToAvro(clazz, jsonString);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T> T decodeFromJSONStringToAvro(Class<T> clazz, String jsonString) throws IOException {
        
        Schema schema = AVRO_MAPPER.get(clazz);
        JsonDecoder decoder = DecoderFactory.get().jsonDecoder(schema, jsonString);

        SpecificDatumReader reader = new SpecificDatumReader(schema);
        Object decoded = reader.read(null, decoder);
        
        return PojoProxy.getInstance(clazz, decoded);
    }
    
    public static String encodeTypeToJSONString(Object obj) throws IOException {
        return encodeAvroTypeToJSONString(obj);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String encodeAvroTypeToJSONString(Object obj) throws IOException {
        if(obj == null || !(obj instanceof SpecificRecord)) {
            return null;
        }
        
        Schema schema = ((SpecificRecord) obj).getSchema();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().jsonEncoder(schema, out);
        
        SpecificDatumWriter writer = new SpecificDatumWriter(obj.getClass());
        writer.write(obj, encoder);
        encoder.flush();
        
        return new String(out.toByteArray());
    }
    
}
