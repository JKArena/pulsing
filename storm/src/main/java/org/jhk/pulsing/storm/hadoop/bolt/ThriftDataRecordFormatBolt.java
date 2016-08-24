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
package org.jhk.pulsing.storm.hadoop.bolt;

import org.apache.storm.hdfs.bolt.format.RecordFormat;
import org.apache.storm.tuple.Tuple;
import org.jhk.pulsing.serialization.thrift.data.Data;
import org.jhk.pulsing.storm.common.FieldConstants;
import org.jhk.pulsing.storm.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public class ThriftDataRecordFormatBolt implements RecordFormat {
    
    private static final long serialVersionUID = 2812772078376560440L;
    private static final Logger _LOGGER = LoggerFactory.getLogger(ThriftDataRecordFormatBolt.class);
    
    @Override
    public byte[] format(Tuple tuple) {
        _LOGGER.debug("ThriftDataRecordFormatBolt.format: " + tuple);
        
        Data tData = (Data) tuple.getValueByField(FieldConstants.THRIFT_DATA);
        byte[] bytes = Util.serializeThriftData(tData);
        
        _LOGGER.debug("ThriftDataRecordFormatBolt.format serialized to bytes: " + bytes.length);
        return bytes;
    }

}
