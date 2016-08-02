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
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class Publisher {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(Publisher.class);
    private static final Properties _DEFAULT_PROPERTIES = new Properties();
    
    static {
        _DEFAULT_PROPERTIES.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, CommonConstants.DEFAULT_BOOTSTRAP_HOST + ":" + CommonConstants.DEFAULT_BOOTSTRAP_PORT);
        _DEFAULT_PROPERTIES.put(ProducerConfig.ACKS_CONFIG, "all");
        _DEFAULT_PROPERTIES.put(ProducerConfig.RETRIES_CONFIG, 3);
        _DEFAULT_PROPERTIES.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        _DEFAULT_PROPERTIES.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.jhk.pulsing.serialization.avro.serializers.KafkaAvroJSONSerializer");
    }
    
    private KafkaProducer<String, SpecificRecord> _producer;
    private Properties _properties;
    
    public Publisher() {
        super();
        
        _properties = _DEFAULT_PROPERTIES;
    }
    
    public Publisher(Properties properties) {
        super();
        
        _properties = properties;
    }
    
    public void produce(String topic, SpecificRecord message, Callback cb) {
        _LOGGER.debug("Publisher.produce " + topic + " : " + message);
        
        if(_producer == null) {
            _producer = createProducer();
        }
        
        ProducerRecord<String, SpecificRecord> data = new ProducerRecord<>(topic, message);
        _producer.send(data, cb);
    }
    
    public void produce(String topic, SpecificRecord message) {
        produce(topic, message , new Callback() {
            
            @Override
            public void onCompletion(RecordMetadata rMetadata, Exception exception) {
                
                _LOGGER.debug("metadata: {topic: " + rMetadata.topic() + ", partition: " + rMetadata.partition() + ", offset: " + rMetadata.offset() + "}");
                if(exception != null) {
                    exception.printStackTrace();
                }
            }
            
        });
    }
    
    public void close() {
        if(_producer != null) {
            _producer.close();
        }
    }
    
    private KafkaProducer<String, SpecificRecord> createProducer() {
        return new KafkaProducer<>(_properties);
    }
    
}
