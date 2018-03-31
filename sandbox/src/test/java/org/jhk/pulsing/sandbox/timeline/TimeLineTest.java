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
package org.jhk.pulsing.sandbox.timeline;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.LongStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jhk.pulsing.sandbox.timeline.cache.UserCache;
import org.jhk.pulsing.sandbox.timeline.cache.UserCacheTest;
import org.jhk.pulsing.sandbox.timeline.pojo.Tweet;

/**
 * @author Ji Kim
 */
public class TimeLineTest {

    private static UserCache userCache;
    
    private TimeLine timeLine;
    
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
    }
    
    @Test
    public void testSetup() {
        assertTrue(userCache.getFollowers(200L).size() == 1, "Strange following Thor");
        assertTrue(userCache.getFollowers(400L).size() == 2, "Loki would be so popular in bad way...");
    }
    
    @Test
    public void testTweet() {
        assertFalse(timeLine.getTweets(300L).isPresent(), "No tweets from Thor");
        timeLine.fanOutTweet(new Tweet(1000L, 200L, 1L, "I broke up with Jane..."));
        
        assertTrue(timeLine.getTweets(300L).isPresent(), "Got notification of Thor's relationship");
        
        timeLine.fanOutTweet(new Tweet(1001L, 200L, 2L, "I didn't get dumped!!!"));
        
        assertTrue(timeLine.getTweets(300L).get().size() == 2, "If you say so...");
        
        assertTrue(timeLine.getTweets(300L).get().get(0).contains("dumped"), "Latest tweet is up first");
        
        assertFalse(timeLine.getTweets(200L).isPresent(), "No tweets for Loki");
        
        timeLine.fanOutTweet(new Tweet(1002L, 400L, 3L, "My brother is in denial"));
        
        assertTrue(timeLine.getTweets(200L).isPresent(), "Thor got Loki's tweet");
        assertTrue(timeLine.getTweets(500L).isPresent(), "Valkyrie too got Loki's tweet");
    }
    
    @Test
    public void testTweetLimit() {
        assertFalse(timeLine.getTweets(200L).isPresent(), "Nothing for Thor");
        
        LongStream.range(0L, 15L).forEach(value -> {
            timeLine.fanOutTweet(new Tweet(value, 400L, value+100L, "Loki spamming " + value));
        });
        
        assertTrue(timeLine.getTweets(200L).get().size() == 10, "Thor has 10 of Loki's tweet");
        assertTrue(timeLine.getTweets(500L).get().size() == 10, "Valkyrie has 10 of Loki's tweet");
    }
    
}
