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
import java.util.LinkedList;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;
import org.jhk.interested.serialization.avro.records.Address;
import org.jhk.interested.serialization.avro.records.Interest;
import org.jhk.interested.serialization.avro.records.InterestId;
import org.jhk.interested.serialization.avro.records.User;
import org.jhk.interested.serialization.avro.records.UserId;
import org.jhk.interested.serialization.avro.serializers.SerializationFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Ji Kim
 */
public class AvroJsonSerializationTest {
    
    private static Invocable _invoker;
    private static List<RecordInfo> _testClasses;
    
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
        
        _testClasses = new LinkedList<>();
        _testClasses.add(new RecordInfo(Address.class, Address.getClassSchema()));
        _testClasses.add(new RecordInfo(Interest.class, Interest.getClassSchema()));
        _testClasses.add(new RecordInfo(InterestId.class, InterestId.getClassSchema()));
        _testClasses.add(new RecordInfo(User.class, User.getClassSchema()));
        _testClasses.add(new RecordInfo(UserId.class, UserId.getClassSchema()));
    }
    
    @Test
    public void testSerialization() {
        
    }
    
    @Test
    public void testDeserialization() {
        
        for(RecordInfo recordInfo : _testClasses) {
            
            try {
                String serialized = _invoker.invokeFunction("getAvroClassSkeleton", recordInfo._clazz.getSimpleName()).toString();
                SerializationFactory.deserializeFromJSONStringToAvro(recordInfo._clazz, recordInfo._schema, serialized);
                assertTrue("Successfully ran " + recordInfo._clazz.getSimpleName(), true);
            }catch (Exception e) {
                assertTrue("Error while running " + recordInfo._clazz.getSimpleName(), false);
            }
            
        }
        
    }
    
    private static class RecordInfo {
        
        private Class<? extends SpecificRecord> _clazz;
        private Schema _schema;
        
        private RecordInfo(Class<? extends SpecificRecord> clazz, Schema schema) {
            super();
            
            _clazz = clazz;
            _schema = schema;
        }
        
    }
    
}
