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
package org.jhk.pulsing.storm.elasticsearch.bolt;

import java.io.IOException;

import org.apache.avro.specific.SpecificRecord;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.elasticsearch.node.NodeValidationException;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;
import org.jhk.pulsing.storm.common.FieldConstants;
import org.jhk.pulsing.storm.elasticsearch.NativeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class ESCreateDocumentBolt extends BaseBasicBolt {

    private static final long serialVersionUID = 8440784949713207825L;
    private static final Logger _LOGGER = LoggerFactory.getLogger(ESCreateDocumentBolt.class);
    
    private String _index;
    private String _type;
    private NativeClient _nClient;
    
    public ESCreateDocumentBolt(String index, String type) throws NodeValidationException {
        super();
        
        _nClient = new NativeClient();
        _index = index;
        _type = type;
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        _LOGGER.info("ESCreateDocumentBolt.execute: " + tuple);
        
        SpecificRecord record = (SpecificRecord) tuple.getValueByField(FieldConstants.AVRO);
        String id = tuple.getValueByField(FieldConstants.ID).toString();
        
        try {
            _nClient.addDocument(_index, _type, id, SerializationHelper.serializeAvroTypeToJSONString(record));
            collector.emit(new Values(record));
        } catch (IOException sException) {
            sException.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer fieldsDeclarer) {
        fieldsDeclarer.declare(FieldConstants.AVRO_DESERIALIZE_FIELD);
    }

}
