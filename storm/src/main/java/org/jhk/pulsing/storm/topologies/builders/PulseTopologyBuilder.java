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
import org.apache.storm.hdfs.trident.format.DelimitedRecordFormat;
import org.apache.storm.hdfs.trident.format.FileNameFormat;
import org.apache.storm.hdfs.trident.format.RecordFormat;
import org.apache.storm.hdfs.trident.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.trident.rotation.FileSizeRotationPolicy;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.trident.TransactionalTridentKafkaSpout;
import org.apache.storm.kafka.trident.TridentKafkaConfig;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentState;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.state.StateFactory;
import org.apache.storm.tuple.Fields;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.shared.util.HadoopConstants;
import org.jhk.pulsing.storm.common.FieldConstants;
import org.jhk.pulsing.storm.trident.deserializers.avro.PulseDeserializerFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class PulseTopologyBuilder {
    
    private static final Logger _LOG = LoggerFactory.getLogger(PulseTopologyBuilder.class);
    
    public static StormTopology build() {
        _LOG.info("PulseTopologyBuilder.build");
        
        TridentTopology topology = new TridentTopology();
        Stream stream = topology.newStream("pulse-create-spout", buildSpout())
            .each(
                    new Fields("str"), 
                    new PulseDeserializerFunction(), 
                    FieldConstants.AVRO_PULSE_DESERIALIZE_FIELD
                    );
        
        hdfsStatePersist(stream);
        
        return topology.build();
    }
    
    private static void hdfsStatePersist(Stream stream) {
        _LOG.info("PulseTopologyBuilder.hdfsPersistBolt");
        
        FileNameFormat fnFormat = new DefaultFileNameFormat()
                .withPath(HadoopConstants.NEW_DATA_WORKSPACE)
                .withPrefix("PulseCreate");
        
        RecordFormat rFormat = new DelimitedRecordFormat()
                .withFields(FieldConstants.THRIFT_DATA_FIELD);
        
        FileRotationPolicy rPolicy = new FileSizeRotationPolicy(10.0f, FileSizeRotationPolicy.Units.MB);
        
        HdfsState.Options opts = new HdfsState.HdfsFileOptions()
                .withFileNameFormat(fnFormat)
                .withRecordFormat(rFormat)
                .withRotationPolicy(rPolicy)
                .withFsUrl(HadoopConstants.HDFS_URL_PORT);
        
        StateFactory sFactory = new HdfsStateFactory().withOptions(opts);
        
        TridentState tState = stream.partitionPersist(sFactory, FieldConstants.THRIFT_DATA_FIELD, new HdfsUpdater(), new Fields());
    }
    
    private static TransactionalTridentKafkaSpout buildSpout() {
        BrokerHosts host = new ZkHosts("localhost");
        TridentKafkaConfig spoutConfig = new TridentKafkaConfig(host, CommonConstants.TOPICS.PULSE_CREATE.toString());
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        return new TransactionalTridentKafkaSpout(spoutConfig);
    }
    
}
