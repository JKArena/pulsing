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
package org.jhk.pulsing.web.consumer;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import org.jhk.pulsing.shared.util.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class Consumer {
    private static final Logger _LOGGER = LoggerFactory.getLogger(Consumer.class);
    private static final Properties _DEFAULT_PROPERTIES = new Properties();
    
    static {
        _DEFAULT_PROPERTIES.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, CommonConstants.DEFAULT_BOOTSTRAP_HOST + ":" + CommonConstants.DEFAULT_BOOTSTRAP_PORT);
        _DEFAULT_PROPERTIES.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        _DEFAULT_PROPERTIES.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        _DEFAULT_PROPERTIES.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.jhk.pulsing.serialization.avro.serializers.KafkaAvroJSONSerializer");
    }
    
    private final KafkaConsumer<String, String> consumer;
    private final CircularFifoBuffer buffer;
    private final String topic;
    
    public Consumer(String groupId, int window, String topic) {
        Properties properties = new Properties(_DEFAULT_PROPERTIES);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        
        consumer = new KafkaConsumer<>(properties);
        buffer = new CircularFifoBuffer(window);
        this.topic = topic;
    }
    
    public void consume() {
        final Thread mainThread = Thread.currentThread();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                _LOGGER.info("Starting exit...");
                // Note that shutdownhook runs in a separate thread, so the only thing we can safely due to a consumer is wake it up
                consumer.wakeup();
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        consumer.subscribe(Collections.singleton(topic));
        
        try {
            
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(1000);
                _LOGGER.info("Waiting for data {}", System.currentTimeMillis());
                for (ConsumerRecord<String, String> record : records) {
                    _LOGGER.info("offset={}, key={}, value={}", record.offset(), record.key(), record.value());
    
                    int sum = 0;
                    try {
                        int num = Integer.parseInt(record.value());
                        buffer.add(num);
                    } catch (NumberFormatException e) {
                        // just ignore strings
                    }
    
                    for (Object entry : buffer) {
                        sum += (Integer) entry;
                    }
    
                    if (buffer.size() > 0) {
                        _LOGGER.info("Moving avg is {}" + (sum / buffer.size()));
                    }
                }
                for (TopicPartition tp: consumer.assignment()) {
                    _LOGGER.info("Committing offset at position: {}", consumer.position(tp));
                }
                consumer.commitSync();
            }
        } catch (WakeupException e) {
            // ignore for shutdown
        } finally {
            consumer.close();
            _LOGGER.info("Closed consumer and we are done");
        }
        
    }
    
}
