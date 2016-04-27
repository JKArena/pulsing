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

import org.apache.avro.Schema;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonDecoder;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;

/**
 * @author Ji Kim
 */
public final class SerializationFactory {
    
    private SerializationFactory() {
        super();
    }
    
    public static <T extends SpecificRecord> T decodeFromJSONStringToAvro(Class<T> clazz, Schema schema, String jsonString) throws IOException {
        
        JsonDecoder decoder = DecoderFactory.get().jsonDecoder(schema, jsonString);

        SpecificDatumReader<T> reader = new SpecificDatumReader<T>(schema);
        
        return reader.read(null, decoder);
    }
    
    public static <T> String encodeAvroTypeToJSONString(T obj) throws IOException {
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
