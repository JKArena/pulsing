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
package org.jhk.interested.serialization.avro;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.avro.specific.SpecificRecord;
import org.jhk.interested.serialization.avro.serializers.SerializationHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Ji Kim
 */
public class AvroJsonSerializationTest {
    
    private static Invocable _invoker;
    
    @BeforeClass
    public static void setup() {
        
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        BufferedReader reader = new BufferedReader(new InputStreamReader(AvroJsonSerializationTest.class.getResourceAsStream("serializers.js")));
        
        try {
            engine.eval(reader);
            _invoker = (Invocable) engine;
        } catch (Exception setupException) {
            throw new RuntimeException(setupException);
        }
        
    }
    
    @Test
    public void testSerialization() {
        
    }
    
    @Test
    public void testDeserialization() {
        
        SerializationHelper.getAvroRecordStream()
            .forEach(avroRecord -> {
                
                Class<? extends SpecificRecord> clazz = avroRecord.getClazz();
                
                try {
                    String serialized = _invoker.invokeFunction("getAvroClassSkeleton", clazz.getSimpleName()).toString();
                    SerializationHelper.deserializeFromJSONStringToAvro(clazz, avroRecord.getSchema(), serialized);
                    assertTrue("Successfully ran " + clazz.getSimpleName(), true);
                }catch (Exception e) {
                    assertTrue("Error while running " + clazz.getSimpleName(), false);
                }
            });
                
    }
        
}
