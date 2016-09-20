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
package org.jhk.pulsing.storm.bolts.persistor;

import java.io.IOException;
import java.util.List;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.jhk.pulsing.pail.common.PailUtil;
import org.jhk.pulsing.pail.thrift.structures.DataPailStructure;
import org.jhk.pulsing.serialization.thrift.data.Data;
import org.jhk.pulsing.shared.util.HadoopConstants.PAIL_NEW_DATA_PATH;
import org.jhk.pulsing.storm.common.FieldConstants;
import org.jhk.pulsing.storm.common.StormUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class PailDataListPersistorBolt extends BaseBasicBolt {
    
    private static final long serialVersionUID = 8345020306001023815L;
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(PailDataListPersistorBolt.class);
    
    private PAIL_NEW_DATA_PATH _newDataPath;
    
    @SuppressWarnings("unused")
    private PailDataListPersistorBolt() {
        super();
    }
    
    public PailDataListPersistorBolt(PAIL_NEW_DATA_PATH newDataPath) {
        super();
        
        _newDataPath = newDataPath;
    }
    
    @Override
    public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
        
        List<Data> datas = (List<Data>) tuple.getValueByField(FieldConstants.THRIFT_DATA_LIST);
        
        String path = StormUtil.generateNewPailPath(_newDataPath);
        _LOGGER.info("PailDataListPersistorBolt.execute: writing to " + path + ", " + datas.size() + " - " + datas);
        
        try {
            PailUtil.writePailStructures(path, new DataPailStructure(), datas);
        } catch (IOException pioException) {
            _LOGGER.error("RIP me ToT!!!!!!!!!!!!!!!!!!!", pioException);
            pioException.printStackTrace();
        } 
    }
    
    @Override
    public void declareOutputFields(OutputFieldsDeclarer fieldsDeclarer) {
    }

}
