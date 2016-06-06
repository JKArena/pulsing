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
package org.jhk.pulsing.storm.topologies;

import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.jhk.pulsing.storm.bolts.pulse.PulseDeserializerBolt;
import org.jhk.pulsing.storm.bolts.pulse.TimeIntervalBolt;
import org.jhk.pulsing.storm.bolts.pulse.TimeIntervalBuilderBolt;
import org.jhk.pulsing.storm.bolts.pulse.TimeIntervalPersistorBolt;
import org.jhk.pulsing.storm.util.PulsingConstants;

/**
 * @author Ji Kim
 */
public final class PulseSubscribeTopologyBuilder {
    
    public static StormTopology build() {
        
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("pulse-subscribe-spout", buildSpout());
        
        builder.setBolt("pulse-deserializer", new PulseDeserializerBolt(), 4) //sets executors, namely threads
                .setNumTasks(4) //num tasks is number of instances of this bolt
                .shuffleGrouping("pulse-subscribe-spout");
        
        builder.setBolt("pulse-subscribe-interval-extractor", new TimeIntervalBolt(), 2)
                .setNumTasks(2)
                .shuffleGrouping("pulse-deserialized");
        
        builder.setBolt("pulse-subscribe-interval-builder", new TimeIntervalBuilderBolt(), 4)
                .setNumTasks(4)
                .fieldsGrouping("pulse-subscribe-interval-extractor", 
                                new Fields("timeInterval"));
        
        builder.setBolt("pulse-subscribe-interval-persistor", new TimeIntervalPersistorBolt())
                .shuffleGrouping("pulse-subscribe-interval-builder");
        
        return builder.createTopology();
    }
    
    private static KafkaSpout buildSpout() {
        BrokerHosts host = new ZkHosts("localhost");
        
        SpoutConfig spoutConfig = new SpoutConfig(host, PulsingConstants.TOPICS.PULSE_SUBSCRIBE.toString(), 
                                                    "/kafkastorm", "trending-pulse-subscribe");
        
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        return new KafkaSpout(spoutConfig);
    }

}
