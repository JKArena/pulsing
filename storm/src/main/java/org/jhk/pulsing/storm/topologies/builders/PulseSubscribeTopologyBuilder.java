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

import java.util.EnumSet;

import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.jhk.pulsing.storm.bolts.PulseAvroFieldExtractorBolt;
import org.jhk.pulsing.storm.bolts.PulseAvroFieldExtractorBolt.EXTRACT_FIELD;
import org.jhk.pulsing.storm.bolts.deserializers.avro.PulseDeserializerBolt;
import org.jhk.pulsing.storm.bolts.persistor.TimeIntervalPersistorBolt;
import org.jhk.pulsing.storm.bolts.time.TimeIntervalBolt;
import org.jhk.pulsing.storm.bolts.time.TimeIntervalBuilderBolt;
import org.jhk.pulsing.storm.common.FieldConstants;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.shared.util.RedisConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class PulseSubscribeTopologyBuilder {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(PulseSubscribeTopologyBuilder.class);
    
    public static StormTopology build() {
        _LOGGER.debug("PulseSubscribeTopologyBuilder.build");
        
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("pulse-subscribe-spout", buildSpout());
        
        builder.setBolt("pulse-avro-deserialize", new PulseDeserializerBolt(), 4) //sets executors, namely threads
                .setNumTasks(4) //num tasks is number of instances of this bolt
                .shuffleGrouping("pulse-subscribe-spout");
        
        builder.setBolt("pulse-avro-id-timestamp-extractor", 
                new PulseAvroFieldExtractorBolt(EnumSet.of(EXTRACT_FIELD.TIMESTAMP, EXTRACT_FIELD.ID, EXTRACT_FIELD.VALUE)), 4)
                .setNumTasks(2)
                .shuffleGrouping("pulse-avro-deserialize");
        
        builder.setBolt("pulse-subscribe-interval-extractor", new TimeIntervalBolt(), 2)
                .setNumTasks(2)
                .shuffleGrouping("pulse-avro-id-timestamp-extractor");
        
        builder.setBolt("pulse-subscribe-interval-builder", new TimeIntervalBuilderBolt(), 4)
                .setNumTasks(4)
                .fieldsGrouping("pulse-subscribe-interval-extractor", 
                                new Fields(FieldConstants.TIME_INTERVAL));
        
        builder.setBolt("pulse-subscribe-interval-persistor", 
                new TimeIntervalPersistorBolt(RedisConstants.REDIS_KEY.PULSE_TRENDING_SUBSCRIBE_.toString()))
                .shuffleGrouping("pulse-subscribe-interval-builder");
        
        return builder.createTopology();
    }
    
    private static KafkaSpout buildSpout() {
        BrokerHosts host = new ZkHosts(CommonConstants.DEFAULT_BOOTSTRAP_HOST);
        
        SpoutConfig spoutConfig = new SpoutConfig(host, CommonConstants.TOPICS.PULSE_SUBSCRIBE.toString(), 
                                                    "/kafkastorm", "trending-pulse-subscribe");
        
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        return new KafkaSpout(spoutConfig);
    }

}
