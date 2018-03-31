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

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jhk.pulsing.sandbox.timeline.cache.UserCache;
import org.jhk.pulsing.sandbox.timeline.pojo.Tweet;
import org.jhk.pulsing.sandbox.timeline.pojo.User;

/**
 * @author Ji Kim
 */
public final class TimeLine {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeLine.class);
    private static final int TWEET_CACHE_SIZE = 10;
    
    // mimic of distributed memory cache userId -> cache
    private final ConcurrentMap<Long, PriorityQueue<Tweet>> tweetCache;
    private final UserCache userCache;
    
    public TimeLine(UserCache userCache) {
        this.userCache = userCache;
        tweetCache = new ConcurrentHashMap<>();
    }
    
    public void fanOutTweet(Tweet tweet) {
        // assume that number of followers is acceptable; otherwise best for followers to query the DB for any popular tweeter
        LOGGER.debug("Fanout of tweet => {}", tweet);
        userCache.getFollowers(tweet.getAuthorId()).parallelStream()
            .mapToLong(User::getUserId)
            .forEach(followerId -> {
                tweetCache.compute(followerId, (key, value) -> {
                    if (value == null) {
                        value = new PriorityQueue<>(TWEET_CACHE_SIZE, Comparator.comparing(Tweet::getTimeStamp));
                    }
                    if (value.size() >= TWEET_CACHE_SIZE) { // works with == but for sanity
                        // should be min heap, if the min value is < the current time stamp remove and offer the tweet
                        if (value.peek().getTimeStamp() < tweet.getTimeStamp()) {
                            value.poll();
                            value.offer(tweet);
                        }
                    } else {
                        value.offer(tweet);
                    }
                    return value; 
                });
            });
    }
    
    public Optional<List<String>> getTweets(long userId) {
        return tweetCache.containsKey(userId) ?
                Optional.of(tweetCache.get(userId).stream()
                        .sorted(Comparator.comparing(Tweet::getTimeStamp).reversed()) // since want the order of latest as first
                        .map(tweet -> {
                            Optional<User> optUser = userCache.getUser(tweet.getAuthorId());
                            String userName = "";
                            if (optUser.isPresent()) {
                                userName = optUser.get().getFullName();
                            }
                            
                            Instant instant = Instant.ofEpochSecond(tweet.getTimeStamp());
                            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("US/Eastern"));
                            String creationTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                            return String.format("Tweet [userName=%s, creationTime=%s, text=%s]", userName, creationTime, tweet.getText());
                        })
                        .collect(Collectors.toList()))
                : Optional.empty();
    }
    
}
