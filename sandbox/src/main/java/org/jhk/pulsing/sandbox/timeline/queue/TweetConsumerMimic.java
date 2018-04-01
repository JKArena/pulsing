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

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jhk.pulsing.sandbox.timeline.TimeLine;
import org.jhk.pulsing.sandbox.timeline.pojo.Message;
import org.jhk.pulsing.sandbox.timeline.pojo.Tweet;

/**
 * Simple consumer whose responsibility is to pull message from KafkaMimic and process them 
 */
public final class TweetConsumerMimic implements IConsumerMimic {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TweetConsumerMimic.class);
    
    private ExecutorService service = Executors.newCachedThreadPool();
    private TimeLine timeLine;
    
    public TweetConsumerMimic(TimeLine timeLine) {
        this.timeLine = timeLine;
    }
    
    @Override
    public void processMessage(Message message) {
        consumerTopology().complete(message.getMessage());
    }
    
    /**
     * Just using synchronous though ideally each step should be multi-threaded like stream processors
     */
    private CompletableFuture<String> consumerTopology() {
        CompletableFuture<String> topology = new CompletableFuture<>();
        
        topology.thenApplyAsync(((Function<? super String, Optional<Tweet>>) message -> {
            try {
                int firstComma = message.indexOf(',');
                int secondComma = message.indexOf(',', firstComma+1);
                int thirdComma = message.indexOf(',', secondComma+1);
                if (firstComma == -1 || secondComma == -1 || thirdComma == -1) {
                    LOGGER.warn("Skipping tweet since malformed => first={}, second={}, third={}, message={}", firstComma, secondComma, thirdComma, message);
                    return Optional.empty();
                }
                Tweet tweet = new Tweet(Long.valueOf(message.substring(0, firstComma)), Long.valueOf(message.substring(firstComma+1, secondComma)),
                        Long.valueOf(message.substring(secondComma+1, thirdComma)), message.substring(thirdComma+1));
                
                return Optional.of(tweet);
            } catch(Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                return Optional.empty();
            }
        }), service).thenApplyAsync(tweet -> {
            if (!tweet.isPresent()) {
                return tweet;
            }
            this.timeLine.fanOutTweet(tweet.get());
            return tweet;
        }, service);
        
        return topology;
    }
    
}
