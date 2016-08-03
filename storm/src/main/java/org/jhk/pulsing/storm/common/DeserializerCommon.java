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
package org.jhk.pulsing.storm.common;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.User;

import static org.jhk.pulsing.storm.common.FieldConstants.*;

/**
 * @author Ji Kim
 */
public final class DeserializerCommon {
    
    public static final Fields PULSE_DESERIALIZE_FIELDS = new Fields(ACTION, ID, USER_ID, TIMESTAMP, VALUE, COORDINATES);
    public static final Fields USER_DESERIALIZE_FIELDS = new Fields(PICTURE, ID, COORDINATES, EMAIL, NAME, PASSWORD);
    
    public static Values getPulseValues(Pulse pulse) {
        return new Values(pulse.getAction().toString(), pulse.getId().getId(), pulse.getUserId().getId(), 
                pulse.getTimeStamp(), pulse.getValue(), pulse.getCoordinates());
    }
    
    public static Values getUserValues(User user) {
        return new Values(user.getPicture(), user.getId(), user.getCoordinates(), 
                user.getEmail(), user.getName(), user.getPassword());
    }
    
    private DeserializerCommon() {
        super();
    }
    
}
