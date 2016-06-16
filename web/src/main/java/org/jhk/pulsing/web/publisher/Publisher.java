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
package org.jhk.pulsing.web.publisher;

import java.util.Properties;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jhk.pulsing.storm.util.PulsingConstants;

/**
 * @author Ji Kim
 */
public final class Publisher {
    
    private KafkaProducer<String, SpecificRecord> _producer;
    private String _bootstrapAddress;
    
    public Publisher() {
        this(PulsingConstants.DEFAULT_BOOTSTRAP_HOST, PulsingConstants.DEFAULT_BOOTSTRAP_PORT);
    }
    
    public Publisher(String bHost, int bPort) {
        this(bHost + ":" + bPort);
    }
    
    public Publisher(String bootstrapAddress) {
        super();
        
        _bootstrapAddress = bootstrapAddress;
        _producer = createProducer();
    }
    
    public void produce(String topic, SpecificRecord message) {
        
        ProducerRecord<String, SpecificRecord> data = new ProducerRecord<>(topic, message);
        
        _producer.send(data);
    }
    
    private KafkaProducer<String, SpecificRecord> createProducer() {
        
        Properties props = new Properties();
        
        props.put("bootstrap.servers", _bootstrapAddress);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.jhk.pulsing.serialization.avro.serializers.KafkaAvroJSONSerializer");
        
        return new KafkaProducer<>(props);
    }
    
}
