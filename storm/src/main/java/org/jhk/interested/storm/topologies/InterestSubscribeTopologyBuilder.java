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
package org.jhk.interested.storm.topologies;

import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.jhk.interested.storm.bolts.interest.InterestDeserializerBolt;
import org.jhk.interested.storm.bolts.interest.TimeIntervalBolt;
import org.jhk.interested.storm.bolts.interest.TimeIntervalBuilderBolt;
import org.jhk.interested.storm.bolts.interest.TimeIntervalPersistorBolt;

/**
 * @author Ji Kim
 */
public final class InterestSubscribeTopologyBuilder {
    
    public static StormTopology build() {
        
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("interest-subscribe-spout", buildSpout());
        
        builder.setBolt("interest-deserializer", new InterestDeserializerBolt(), 8) //sets executors, namely threads
                .setNumTasks(8) //num tasks is number of instances of this bolt
                .shuffleGrouping("interest-subscribe-spout");
        
        builder.setBolt("interest-subscribe-interval-extractor", new TimeIntervalBolt())
                .shuffleGrouping("interest-deserialized");
        
        builder.setBolt("interest-subscribe-interval-builder", new TimeIntervalBuilderBolt())
                .fieldsGrouping("interest-subscribe-interval-extractor", 
                                new Fields("timeInterval"));
        
        builder.setBolt("interest-subscribe-interval-persistor", new TimeIntervalPersistorBolt())
                .shuffleGrouping("interest-subscribe-interval-builder");
        
        return builder.createTopology();
    }
    
    private static KafkaSpout buildSpout() {
        BrokerHosts host = new ZkHosts("localhost");
        
        SpoutConfig spoutConfig = new SpoutConfig(host, "interest-subscribe", 
                                                    "/kafkastorm", "trending-interest-subscribe");
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        return new KafkaSpout(spoutConfig);
    }

}
