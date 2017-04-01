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
package org.jhk.pulsing.storm.bolts.deserializers.avro;

import java.io.IOException;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;
import org.jhk.pulsing.storm.common.FieldConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class UserDeserializerBolt extends BaseBasicBolt {
    
    private static final long serialVersionUID = 204666646818722549L;
    private static final Logger _LOGGER = LoggerFactory.getLogger(UserDeserializerBolt.class);
    
    private boolean _emitId;
    
    public UserDeserializerBolt() {
        super();
    }
    
    public UserDeserializerBolt(boolean emitId) {
        super();
        
        _emitId = emitId;
    }
    
    @Override
    public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
        _LOGGER.info("UserDeserializerBolt.execute: " + tuple);
        
        String userString = tuple.getString(0);
        
        try {
            
            User user = SerializationHelper.deserializeFromJSONStringToAvro(User.class, User.getClassSchema(), userString);
            Values values = new Values(user);
            
            if(_emitId) {
                values.add(user.getId().getId());
            }
            
            outputCollector.emit(values);
            
        } catch (IOException decodeException) {
            outputCollector.reportError(decodeException);
        }
    }
    
    @Override
    public void declareOutputFields(OutputFieldsDeclarer fieldsDeclarer) {
        fieldsDeclarer.declare(_emitId ? FieldConstants.AVRO_DESERIALIZE_WITH_ID_FIELD : FieldConstants.AVRO_DESERIALIZE_FIELD);
    }

}
