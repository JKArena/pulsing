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

import org.apache.storm.generated.StormTopology;
import org.apache.storm.hdfs.trident.HdfsState;
import org.apache.storm.hdfs.trident.HdfsStateFactory;
import org.apache.storm.hdfs.trident.HdfsUpdater;
import org.apache.storm.hdfs.trident.format.DefaultFileNameFormat;
import org.apache.storm.hdfs.trident.format.FileNameFormat;
import org.apache.storm.hdfs.trident.format.RecordFormat;
import org.apache.storm.hdfs.trident.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.trident.rotation.FileSizeRotationPolicy;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.trident.TransactionalTridentKafkaSpout;
import org.apache.storm.kafka.trident.TridentKafkaConfig;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentState;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.state.StateFactory;
import org.apache.storm.tuple.Fields;
import org.elasticsearch.node.NodeValidationException;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.shared.util.HadoopConstants;
import org.jhk.pulsing.storm.bolts.converter.AvroToThriftConverterBolt;
import org.jhk.pulsing.storm.bolts.deserializers.AvroDeserializerBolt;
import org.jhk.pulsing.storm.bolts.elasticsearch.ESCreateDocumentBolt;
import org.jhk.pulsing.storm.bolts.persistor.PailDataListPersistorBolt;
import org.jhk.pulsing.storm.common.FieldConstants;
import org.jhk.pulsing.storm.converter.AvroToElasticDocumentConverter;
import org.jhk.pulsing.storm.converter.AvroToThriftConverter;
import org.jhk.pulsing.storm.deserializer.StringToAvroDeserializedValues;
import org.jhk.pulsing.storm.hadoop.trident.AvroRecordFormatFunction;
import org.jhk.pulsing.storm.trident.deserializers.AvroDeserializerFunction;
import org.jhk.pulsing.storm.trident.elasticsearch.ESCreateDocumentFunction;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class PulseTopologyBuilder {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(PulseTopologyBuilder.class);
    
    private static final String ELASTIC_SEARCH_PULSE_INDEX = "pulse";
    private static final String ELASTIC_SEARCH_PULSE_DOC_TYPE = "pulse_tags";
    
    public static StormTopology build(boolean isPailBuild) throws NodeValidationException {
        _LOGGER.debug("PulseTopologyBuilder.build");
        
        if(isPailBuild) {
            return pailBuild();
        }else {
            return tridentBuild();
        }

    }
    
    /**
     * Need to create PulseEdge for the tags 
     * 
     * @return
     * @throws NodeValidationException 
     * @throws IllegalArgumentException 
     */
    private static StormTopology pailBuild() throws IllegalArgumentException, NodeValidationException {
        
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("pulse-create-spout", buildSpout());
        
        builder.setBolt("pulse-avro-deserialize", new AvroDeserializerBolt(StringToAvroDeserializedValues.STRING_TO_AVRO_VALUES.PULSE, true), 1) //sets executors, namely threads
            .setNumTasks(1) //num tasks is number of instances of this bolt
            .shuffleGrouping("pulse-create-spout");
        
        builder.setBolt("pulse-elasticsearch-create-doc", new ESCreateDocumentBolt(AvroToElasticDocumentConverter.AVRO_TO_ELASTIC_DOCUMENT.PULSE, 
                ELASTIC_SEARCH_PULSE_INDEX, ELASTIC_SEARCH_PULSE_DOC_TYPE), 1)
            .setNumTasks(1)
            .shuffleGrouping("pulse-avro-deserialize");
        
        builder.setBolt("pulse-avro-thrift-converter", new AvroToThriftConverterBolt(AvroToThriftConverter.AVRO_TO_THRIFT.PULSE, FieldConstants.THRIFT_DATA_LIST_FIELD), 1)
            .setNumTasks(1)
            .shuffleGrouping("pulse-elasticsearch-create-doc");
        
        builder.setBolt("pulse-pail-data-persistor", new PailDataListPersistorBolt(HadoopConstants.PAIL_NEW_DATA_PATH.TAG_GROUP), 1)
            .setNumTasks(1)
            .shuffleGrouping("pulse-avro-thrift-converter");
        
        return builder.createTopology();
    }
    
    private static KafkaSpout buildSpout() {
        BrokerHosts host = new ZkHosts("localhost");
        
        SpoutConfig spoutConfig = new SpoutConfig(host, CommonConstants.TOPICS.PULSE_CREATE.toString(), 
                                                    "/kafkastorm", "pulse-create");
        
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        return new KafkaSpout(spoutConfig);
    }
    
    private static StormTopology tridentBuild() throws NodeValidationException {
        
        TridentTopology topology = new TridentTopology();
        Stream stream = topology.newStream("pulse-create-spout", buildTridentSpout())
                .each(new Fields("str"), new AvroDeserializerFunction(StringToAvroDeserializedValues.STRING_TO_AVRO_VALUES.PULSE, true), FieldConstants.AVRO_DESERIALIZE_WITH_ID_FIELD)
                .shuffle()
                .each(FieldConstants.AVRO_DESERIALIZE_WITH_ID_FIELD, 
                        new ESCreateDocumentFunction(AvroToElasticDocumentConverter.AVRO_TO_ELASTIC_DOCUMENT.PULSE, ELASTIC_SEARCH_PULSE_INDEX, ELASTIC_SEARCH_PULSE_DOC_TYPE), new Fields());
        
        avroHdfsStatePersist(stream);
        
        return topology.build();
    }
    
    private static void avroHdfsStatePersist(Stream stream) {
        
        FileNameFormat fnFormat = new DefaultFileNameFormat()
                .withPath(HadoopConstants.SPARK_NEW_DATA_WORKSPACE + "pulse/" + LocalDate.now().getYear())
                .withPrefix("PulseCreate");
        
        RecordFormat rFormat = new AvroRecordFormatFunction();
        
        FileRotationPolicy rPolicy = new FileSizeRotationPolicy(10.0f, FileSizeRotationPolicy.Units.MB);
        
        HdfsState.Options opts = new HdfsState.HdfsFileOptions()
                .withFileNameFormat(fnFormat)
                .withRecordFormat(rFormat)
                .withRotationPolicy(rPolicy)
                .withFsUrl(HadoopConstants.HDFS_URL_PORT);
        
        StateFactory sFactory = new HdfsStateFactory().withOptions(opts);
        
        TridentState tState = stream.partitionPersist(sFactory, FieldConstants.AVRO_DESERIALIZE_FIELD, new HdfsUpdater(), new Fields());
    }
    
    private static TransactionalTridentKafkaSpout buildTridentSpout() {
        BrokerHosts host = new ZkHosts(CommonConstants.DEFAULT_BOOTSTRAP_HOST);
        TridentKafkaConfig spoutConfig = new TridentKafkaConfig(host, CommonConstants.TOPICS.PULSE_CREATE.toString());
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        return new TransactionalTridentKafkaSpout(spoutConfig);
    }
    
}
