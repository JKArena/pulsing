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
package org.jhk.interested.web.publisher;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jhk.interested.serialization.avro.serializers.KafkaAvroJSONSerializer;
import org.jhk.interested.storm.util.InterestedConstants;
import org.jhk.interested.storm.util.InterestedConstants.TOPICS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.kafka.Kafka;
import org.springframework.integration.dsl.kafka.KafkaProducerMessageHandlerSpec;
import org.springframework.integration.kafka.support.ProducerMetadata;
import org.springframework.messaging.MessageChannel;

/**
 * @author Ji Kim
 */
@Configuration
@EnableIntegration
public class PublisherConfig {
	
	@Autowired
	@Qualifier("subscribeInterest.input")
	private MessageChannel publishToSubscribeInterestKafkaFlowInput;
	
	@Bean
    public IntegrationFlow subscribeInterest() {
		String serverAddress = InterestedConstants.DEFAULT_ZOOKEEPER_HOST + ":" + InterestedConstants.DEFAULT_ZOOKEEPER_PORT;
		
        return flow -> flow.publishSubscribeChannel(
        			consumer -> consumer.subscribe(
        					subFlow -> subFlow.handle(getKafkaProducerMessageHandler(serverAddress, InterestedConstants.TOPICS.INTEREST_SUBSCRIBE)
        					)
        			)
        		);
    }
    
    private KafkaProducerMessageHandlerSpec getKafkaProducerMessageHandler(final String serverAddress, final TOPICS topic) {
    	
    	return Kafka.outboundChannelAdapter(props -> {
			    		props.put("timeout.ms", "35000");
			    		props.put("queue.buffering.max.ms", "15000");
			    	})
			    	.messageKey(m -> m
							.getHeaders()
							.get(IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER))
			    	.partitionId(m -> 1)
			    	.addProducer(
							new ProducerMetadata<>(topic.toString(), String.class, SpecificRecord.class,
									new StringSerializer(),
									new KafkaAvroJSONSerializer()),
							serverAddress);
    }
    
}
