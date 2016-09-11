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

import java.util.List;

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
public class ThriftDataListRecordFormatBolt implements RecordFormat {
    
    private static final long serialVersionUID = 1289711103039255339L;
    private static final Logger _LOGGER = LoggerFactory.getLogger(ThriftDataListRecordFormatBolt.class);
    
    @Override
    public byte[] format(Tuple tuple) {
        _LOGGER.info("ThriftDataListRecordFormatBolt.format: " + tuple);
        
        List<Data> tDatas = (List<Data>) tuple.getValueByField(FieldConstants.THRIFT_DATA_LIST);
        byte[] bytes = Util.serializeThriftDataList(tDatas);
        
        _LOGGER.info("ThriftDataListRecordFormatBolt.format serialized to bytes: " + bytes.length);
        return bytes;
    }

}
