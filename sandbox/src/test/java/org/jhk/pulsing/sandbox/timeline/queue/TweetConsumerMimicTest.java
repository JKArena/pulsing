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

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jhk.pulsing.sandbox.timeline.TimeLine;
import org.jhk.pulsing.sandbox.timeline.cache.UserCache;
import org.jhk.pulsing.sandbox.timeline.cache.UserCacheTest;
import org.jhk.pulsing.sandbox.timeline.pojo.Message;

/**
 * @author Ji Kim
 */
public class TweetConsumerMimicTest {
    
    private static UserCache userCache;
    
    private TimeLine timeLine;
    
    private TweetConsumerMimic consumerMimic;

    @BeforeAll
    public static void setupAll() {
        userCache = new UserCache();
        UserCacheTest.addUser(100L, "Minion", "Real Minion", userCache);
        UserCacheTest.addUser(200L, "Thor", "Friend of Hulk...or...Bruce", userCache);
        UserCacheTest.addUser(300L, "Doctor Strange", "Why strange...", userCache);
        UserCacheTest.addUser(400L, "Loki", "Troublesome child...", userCache);
        UserCacheTest.addUser(500L, "Valkyrie", "Lone warrior", userCache);
        
        userCache.follow(200L, 300L);
        userCache.follow(400L, 200L);
        userCache.follow(400L, 500L);
    }
    
    @BeforeEach
    public void setup() {
        timeLine = new TimeLine(userCache);
        consumerMimic = new TweetConsumerMimic(timeLine);
    }
    
    @Test
    public void testTweetConsumption() {
        consumerMimic.processMessage(new Message("0,200,0,Thinking which is greater loss of my girlfriend or loss of my beloved hammer"));
        assertTrue(timeLine.getTweets(300L).isPresent(), "Thor really...");
        
        consumerMimic.processMessage(new Message("1,200"));
        
        assertTrue(timeLine.getTweets(300L).get().size() == 1, "Malformed message not delivered");
        
        consumerMimic.processMessage(new Message("1,200,1,I look to you Hulk for support..."));
        
        assertTrue(timeLine.getTweets(300L).get().size() == 2, "Good message delivered");
    }
    
}
