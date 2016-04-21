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
package org.jhk.interested.storm.interest;

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
import org.apache.storm.trident.operation.BaseFilter;
import org.apache.storm.trident.operation.builtin.MapGet;
import org.apache.storm.trident.operation.builtin.TupleCollectionGet;
import org.apache.storm.trident.testing.MemoryMapState;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Fields;
import org.jhk.interested.storm.aggregator.Counter;
import org.jhk.interested.storm.common.functions.SplitByDelimiter;
import org.jhk.interested.storm.deserializers.InterestDeserializer;

/**
 * @author Ji Kim
 */
public final class InterestTopologyBuilder {
    
    private class InterestSubscribeFilter extends BaseFilter {

        private static final long serialVersionUID = -5563080957922894127L;

        @Override
        public boolean isKeep(TridentTuple tuple) {
            return tuple.getStringByField("action").equals("SUBSCRIBE");
        }
        
    }
    
    public StormTopology build() {
        TridentTopology topology = new TridentTopology();
        
        Stream interestStream = topology.newStream("interest-action-spout", buildSpout()).each(
                new Fields("interest-action"), 
                new InterestDeserializer(),
                InterestDeserializer.FIELDS);
        
        TridentState subscriptions = interestStream.
                filter(new InterestSubscribeFilter()).
                project(new Fields("id", "value", "timeStamp")).  //use time window for trend
                groupBy(new Fields("id")).
                persistentAggregate(new MemoryMapState.Factory(), //swap to a non memory later
                        new Counter(), 
                        new Fields("interest-count"));
        
        topology.newDRPCStream("interest-count").
                each(new Fields("args"),
                     new SplitByDelimiter(","),
                     new Fields("interestId")).
                stateQuery(subscriptions,
                        new Fields("interestId"),
                        new MapGet(),
                        new Fields("interest-count"));
        
        return topology.build();
    }
    
    private TransactionalTridentKafkaSpout buildSpout() {
        BrokerHosts host = new ZkHosts("localhost");
        TridentKafkaConfig spoutConfig = new TridentKafkaConfig(host, "interest-action");
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        return new TransactionalTridentKafkaSpout(spoutConfig);
    }

}
