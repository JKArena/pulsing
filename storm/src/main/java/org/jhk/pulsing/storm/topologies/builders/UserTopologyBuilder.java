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
package org.jhk.pulsing.storm.topologies.builders;

import java.time.LocalDate;

import org.apache.storm.generated.StormTopology;
import org.apache.storm.hdfs.bolt.HdfsBolt;
import org.apache.storm.hdfs.bolt.format.DefaultFileNameFormat;
import org.apache.storm.hdfs.bolt.format.FileNameFormat;
import org.apache.storm.hdfs.bolt.format.RecordFormat;
import org.apache.storm.hdfs.bolt.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.FileSizeRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.FileSizeRotationPolicy.Units;
import org.apache.storm.hdfs.bolt.sync.CountSyncPolicy;
import org.apache.storm.hdfs.bolt.sync.SyncPolicy;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.shared.util.HadoopConstants;
import org.jhk.pulsing.storm.bolts.converter.AvroToThriftConverterBolt;
import org.jhk.pulsing.storm.bolts.deserializers.AvroDeserializerBolt;
import org.jhk.pulsing.storm.bolts.elasticsearch.ESCreateDocumentBolt;
import org.jhk.pulsing.storm.bolts.persistor.PailDataPersistorBolt;
import org.jhk.pulsing.storm.common.FieldConstants;
import org.jhk.pulsing.storm.converter.AvroToElasticDocumentConverter;
import org.jhk.pulsing.storm.converter.AvroToThriftConverter;
import org.jhk.pulsing.storm.deserializer.StringToAvroDeserializedValues;
import org.jhk.pulsing.storm.hadoop.bolt.AvroRecordFormatBolt;
import org.jhk.pulsing.storm.hadoop.bolt.ThriftDataRecordFormatBolt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class UserTopologyBuilder {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(UserTopologyBuilder.class);
    
    private static final String ELASTIC_SEARCH_USER_INDEX = "user";
    private static final String ELASTIC_SEARCH_USER_DOC_TYPE = "user_tags";
    
    public static StormTopology build(boolean isPailBuild) {
        _LOGGER.info("UserTopologyBuilder.build");
        
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("user-create-spout", buildSpout());
        
        builder.setBolt("user-avro-deserialize", new AvroDeserializerBolt(StringToAvroDeserializedValues.STRING_TO_AVRO_VALUES.USER, false), 1) //sets executors, namely threads
            .setNumTasks(1) //num tasks is number of instances of this bolt
            .shuffleGrouping("user-create-spout");
        
        if(isPailBuild) {
            builder.setBolt("user-avro-thrift-converter", new AvroToThriftConverterBolt(AvroToThriftConverter.AVRO_TO_THRIFT.USER, FieldConstants.THRIFT_DATA_FIELD), 1)
                .setNumTasks(1)
                .shuffleGrouping("user-avro-deserialize");
            
            builder.setBolt("user-pail-data-persistor", new PailDataPersistorBolt(HadoopConstants.PAIL_NEW_DATA_PATH.USER), 1)
                .setNumTasks(1)
                .shuffleGrouping("user-avro-thrift-converter");
            
            builder.setBolt("user-hdfs-pail", hdfsPailBolt(), 1)
                .setNumTasks(1)
                .shuffleGrouping("user-avro-thrift-converter");
        }else {
            builder.setBolt("user-elasticsearch-create-doc", new ESCreateDocumentBolt(AvroToElasticDocumentConverter.AVRO_TO_ELASTIC_DOCUMENT.USER, 
                    ELASTIC_SEARCH_USER_INDEX, ELASTIC_SEARCH_USER_DOC_TYPE), 1)
                .setNumTasks(1)
                .shuffleGrouping("user-avro-deserialize");
            
            builder.setBolt("user-hdfs", avroHdfsBolt(), 1)
                .setNumTasks(1)
                .shuffleGrouping("user-elasticsearch-create-doc");
        }
        
        return builder.createTopology();
    }
    
    private static HdfsBolt hdfsPailBolt() {
        _LOGGER.info("UserTopologyBuilder.hdfsPailBolt");
        
        FileNameFormat fnFormat = new DefaultFileNameFormat()
                .withPath(HadoopConstants.PAIL_NEW_DATA_WORKSPACE)
                .withPrefix("UserCreate");
        
        RecordFormat rFormat = new ThriftDataRecordFormatBolt();
        
        // sync the filesystem after every 1k tuples (setting to 1 for testing)
        SyncPolicy sPolicy = new CountSyncPolicy(1);
        
        FileRotationPolicy rPolicy = new FileSizeRotationPolicy(5.0f, Units.MB);
        
        HdfsBolt hdfsBolt = new HdfsBolt()
            .withFileNameFormat(fnFormat)
            .withRecordFormat(rFormat)
            .withSyncPolicy(sPolicy)
            .withRotationPolicy(rPolicy)
            .withFsUrl(HadoopConstants.HDFS_URL_PORT);

        return hdfsBolt;
    }
    
    private static HdfsBolt avroHdfsBolt() {
        _LOGGER.info("UserTopologyBuilder.avroHdfsBolt");
        
        FileNameFormat fnFormat = new DefaultFileNameFormat()
                .withPath(HadoopConstants.SPARK_NEW_DATA_WORKSPACE + "user/" + LocalDate.now().getYear())
                .withPrefix("UserCreate");
        
        RecordFormat rFormat = new AvroRecordFormatBolt();
        
        // sync the filesystem after every 1k tuples (setting to 1 for testing)
        SyncPolicy sPolicy = new CountSyncPolicy(1);
        
        FileRotationPolicy rPolicy = new FileSizeRotationPolicy(5.0f, Units.MB);
        
        HdfsBolt hdfsBolt = new HdfsBolt()
            .withFileNameFormat(fnFormat)
            .withRecordFormat(rFormat)
            .withSyncPolicy(sPolicy)
            .withRotationPolicy(rPolicy)
            .withFsUrl(HadoopConstants.HDFS_URL_PORT);

        return hdfsBolt;
    }
    
    private static KafkaSpout buildSpout() {
        BrokerHosts host = new ZkHosts(CommonConstants.DEFAULT_BOOTSTRAP_HOST);
        
        SpoutConfig spoutConfig = new SpoutConfig(host, CommonConstants.TOPICS.USER_CREATE.toString(), 
                                                    "/kafkastorm", "user-create");
        
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        return new KafkaSpout(spoutConfig);
    }

}
