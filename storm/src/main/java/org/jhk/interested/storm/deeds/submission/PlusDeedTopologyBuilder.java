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
package org.jhk.interested.storm.deeds.submission;

import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.trident.TransactionalTridentKafkaSpout;
import org.apache.storm.kafka.trident.TridentKafkaConfig;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentState;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.builtin.MapGet;
import org.apache.storm.trident.testing.MemoryMapState;
import org.apache.storm.tuple.Fields;
import org.jhk.interested.storm.aggregator.Counter;
import org.jhk.interested.storm.common.functions.SplitByDelimiter;
import org.jhk.interested.storm.deserializers.UserDeserializer;

/**
 * @author Ji Kim
 */
public final class PlusDeedTopologyBuilder {
    
    public StormTopology build() {
        /*
        TridentTopology topology = new TridentTopology();
        
        Stream deedsStream = topology.newStream("deed-plus-spout", buildSpout()).each(
                new Fields("deed-plus"), 
                new DeedDeserializer(),
                DeedDeserializer.FIELDS
                );
        
        TridentState plusCounts = deedsStream.
                project(new Fields("pluses")).
                groupBy(new Fields("id")).
                persistentAggregate(new MemoryMapState.Factory(), //swap to a non memory later for trending deed (should this be view?)
                        new Counter(), 
                        new Fields("pluses"));
        
        topology.newDRPCStream("plus-request-by-deed").
            name("PlusRequestByDeed").
            each(new Fields("args"), 
                    new SplitByDelimiter(","), 
                    new Fields("id")).
            groupBy(new Fields("id")).
            name("PlusRequest").
            stateQuery(plusCounts,
                    new Fields("id"),
                    new MapGet(),
                    new Fields("pluses"));
        
        return topology.build();*/
        return null;
    }
    
    private TransactionalTridentKafkaSpout buildSpout() {
        BrokerHosts host = new ZkHosts("localhost");
        TridentKafkaConfig spoutConfig = new TridentKafkaConfig(host, "deed-plus");
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        return new TransactionalTridentKafkaSpout(spoutConfig);
    }
    
}
