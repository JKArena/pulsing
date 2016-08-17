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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.jhk.pulsing.pail.thrift.structures.SplitDataPailstructure;
import org.jhk.pulsing.serialization.thrift.data.Data;
import org.jhk.pulsing.shared.util.HadoopConstants;
import org.jhk.pulsing.storm.common.FieldConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backtype.pail.common.PailUtil;

/**
 * @author Ji Kim
 */
public final class PailDataPersistorBolt extends BaseBasicBolt {
    
    private static final long serialVersionUID = -6734265634986696958L;
    private static final Logger _LOG = LoggerFactory.getLogger(PailDataPersistorBolt.class);
    
    private static final String _DELIM = "-";
    private static final String HADOOP_PAIL_NEW_DATA_PATH = HadoopConstants.HDFS_URL_PORT + HadoopConstants.PAIL_NEW_DATA_WORKSPACE;
    
    private static int _COUNTER = 0;
    
    private Optional<String> _prefix;
    private Optional<String> _lHost;
    
    public PailDataPersistorBolt() {
        super();
        
        _prefix = Optional.empty();
        _lHost = Optional.empty();
    }
    
    public PailDataPersistorBolt(String prefix) {
        super();
        
        _prefix = Optional.of(prefix + _DELIM);
        _lHost = Optional.empty();
    }
    
    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        try {
            InetAddress lHost = InetAddress.getLocalHost();
            _lHost = Optional.of(lHost.getHostName() + _DELIM);
        } catch (UnknownHostException uhException) {
            
        }
    }
    
    @Override
    public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
        
        Data data = (Data) tuple.getValueByField(FieldConstants.THRIFT_DATA);
        
        List<Data> datas = new LinkedList<>();
        datas.add(data);
        
        String path = HADOOP_PAIL_NEW_DATA_PATH + generateFileName();
        _LOG.info("PailDataPersistorBolt.execute: writing to " + path + ", " + datas.size());
        
        try {
            PailUtil.writePailStructures(path, new SplitDataPailstructure(), datas);
        } catch (IOException pioException) {
            _LOG.error("RIP me ToT!!!!!!!!!!!!!!!!!!!", pioException);
            pioException.printStackTrace();
        } 
    }
    
    private String generateFileName() {
        StringBuilder builder = new StringBuilder(_prefix.orElse(""));
        builder.append(_lHost.orElse(""));
        builder.append(System.nanoTime());
        builder.append(_DELIM);
        builder.append(_COUNTER++);
        return builder.toString();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer fieldsDeclarer) {
    }

}
