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
package org.jhk.pulsing.sandbox.timeline.queue;

import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jhk.pulsing.sandbox.timeline.pojo.Message;

/**
 * Note this is a bare mimic of Kafka broker (single topic, single consumer, no partition and etc xD)
 */
public final class KafkaMimic {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMimic.class);
    
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> handle;
    
    // consumers for this message broker (thought about using WeakHashMap to mimic drop of consumer when loss of heartbeat, but too much code then xD)
    private final List<IConsumerMimic> consumers;
    private final TransferQueue<Message> messages;
    private final int delay;
    
    public KafkaMimic(int delay) {
        this.delay = delay;

        consumers = new LinkedList<>();
        messages = new LinkedTransferQueue<>();
    }
    
    public void publish(String message) {
        try {
            boolean submitted = messages.tryTransfer(new Message(message), 5000, TimeUnit.SECONDS);
            LOGGER.debug("Message submission => {}", submitted);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    
    public void addConsumer(IConsumerMimic consumer) {
        consumers.add(consumer);
    }
    
    public void run() {
        
        handle = service.scheduleAtFixedRate(() -> {
            try {
                Message message = messages.take();
                consumers.parallelStream().forEach(consumer -> {
                    consumer.processMessage(message);
                });
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, delay, delay, TimeUnit.MILLISECONDS);
    }
    
    public void cancel() {
        final ScheduledFuture<?> handler = handle;
        service.schedule(new Runnable() {
            public void run() {
                handler.cancel(true);
            }
        }, 10, TimeUnit.MILLISECONDS);
    }
    
}
