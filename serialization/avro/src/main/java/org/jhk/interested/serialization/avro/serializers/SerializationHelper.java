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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.avro.Schema;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonDecoder;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.jhk.interested.serialization.avro.records.Address;
import org.jhk.interested.serialization.avro.records.Interest;
import org.jhk.interested.serialization.avro.records.InterestId;
import org.jhk.interested.serialization.avro.records.User;
import org.jhk.interested.serialization.avro.records.UserId;

/**
 * @author Ji Kim
 */
public final class SerializationHelper {
    
    private static final List<AvroRecords<? extends SpecificRecord>> _AVRO_RECORDS = new LinkedList<>();
    
    private SerializationHelper() {
        super();
    }
    
    public static class AvroRecords<T extends SpecificRecord> {
        private Class<T> _clazz;
        private Schema _schema;
        
        private AvroRecords(Class<T> clazz, Schema schema) {
            super();
            
            _clazz = clazz;
            _schema = schema;
        }
        
        public Class<T> getClazz() {
            return _clazz;
        }
        public Schema getSchema() {
            return _schema;
        }
    }
    
    static {
        _AVRO_RECORDS.add(new AvroRecords<Address>(Address.class, Address.getClassSchema()));
        _AVRO_RECORDS.add(new AvroRecords<Interest>(Interest.class, Interest.getClassSchema()));
        _AVRO_RECORDS.add(new AvroRecords<InterestId>(InterestId.class, InterestId.getClassSchema()));
        _AVRO_RECORDS.add(new AvroRecords<User>(User.class, User.getClassSchema()));
        _AVRO_RECORDS.add(new AvroRecords<UserId>(UserId.class, UserId.getClassSchema()));
    }
    
    public static Stream<AvroRecords<? extends SpecificRecord>> getAvroRecordStream() {
        return _AVRO_RECORDS.stream();
    }
    
    public static <T extends SpecificRecord> T deserializeFromJSONStringToAvro(Class<T> clazz, Schema schema, String jsonString) throws IOException {
        
        JsonDecoder decoder = DecoderFactory.get().jsonDecoder(schema, jsonString);
        
        SpecificDatumReader<T> reader = new SpecificDatumReader<T>(schema);
        
        return reader.read(null, decoder);
    }
    
    public static <T extends SpecificRecord> T deserializeFromJSONStringToAvro(Class<T> clazz, Schema wSchema, Schema rSchema, String jsonString) throws IOException {
        
        JsonDecoder decoder = DecoderFactory.get().jsonDecoder(rSchema, jsonString);
        
        SpecificDatumReader<T> reader = new SpecificDatumReader<T>(wSchema, rSchema);
        
        return reader.read(null, decoder);
    }
    
    public static <T extends SpecificRecord> String serializeAvroTypeToJSONString(T obj) throws IOException {
        if(obj == null || !(obj instanceof SpecificRecord)) {
            return null;
        }
        
        Schema schema = ((SpecificRecord) obj).getSchema();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().jsonEncoder(schema, out);
        
        @SuppressWarnings("unchecked")
        SpecificDatumWriter<T> writer = new SpecificDatumWriter<T>((Class<T>) obj.getClass());
        writer.write(obj, encoder);
        encoder.flush();
        
        return new String(out.toByteArray());
    }
    
}
