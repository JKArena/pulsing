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
package org.jhk.pulsing.storm.trident.deserializers.avro;

import java.io.IOException;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Values;
import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class UserDeserializerFunction extends BaseFunction {
    
    private static final long serialVersionUID = -5222249102945206582L;
    private static final Logger _LOGGER = LoggerFactory.getLogger(UserDeserializerFunction.class);
    
    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        _LOGGER.debug("UserDeserializer.execute: " + tuple);
        
        String userString = tuple.getString(0);
        
        try {
            
            User user = SerializationHelper.deserializeFromJSONStringToAvro(User.class, User.getClassSchema(), userString);
            _LOGGER.debug("UserDeserializer.execute deserialized to " + user);
            collector.emit(new Values(user));
            
        } catch (IOException decodeException) {
            collector.reportError(decodeException);
        }
        
    }
    
}
