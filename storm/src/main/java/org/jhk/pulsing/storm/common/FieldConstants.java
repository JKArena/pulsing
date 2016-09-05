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

/**
 * @author Ji Kim
 */
public final class FieldConstants {
    
    public static final String ID = "ID"; 
    public static final String TIMESTAMP = "TIMESTAMP";
    
    public static final String USER_ID = "USER_ID";
    public static final String COORDINATES = "COORDINATES";
    public static final String VALUE = "VALUE";
    public static final String ACTION = "ACTION";
    public static final String DESCRIPTION = "DESCRIPTION";
    
    public static final String TIME_INTERVAL = "TIME_INTERVAL";
    public static final String TIME_INTERVAL_VALUE = "TIME_INTERVAL_VALUE";
    public static final String TIME_INTERVAL_VALUE_COUNTER_MAP = "TIME_INTERVAL_VALUE_COUNTER_MAP";
    
    public static final String THRIFT_DATA = "THRIFT_DATA";
    
    public static final String AVRO_USER = "AVRO_USER";
    public static final String AVRO_PULSE = "AVRO_PULSE";
    
    public static final Fields AVRO_USER_DESERIALIZE_FIELD = new Fields(AVRO_USER);
    public static final Fields AVRO_PULSE_DESERIALIZE_FIELD = new Fields(AVRO_PULSE);
    
    public static final Fields THRIFT_DATA_FIELD = new Fields(THRIFT_DATA);
    
    private FieldConstants() {
        super();
    }
    
}
