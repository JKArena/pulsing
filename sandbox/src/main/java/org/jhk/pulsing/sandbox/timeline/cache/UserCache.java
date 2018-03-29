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
package org.jhk.pulsing.sandbox.timeline.cache;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jhk.pulsing.sandbox.timeline.pojo.User;
import org.jhk.pulsing.sandbox.timeline.util.Util;

/**
 * @author Ji Kim
 */
public final class UserCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCache.class);
    
    private final ConcurrentMap<Long, User> userEntries;
    // key is the twitter and Set are the followers
    private final ConcurrentMap<Long, Set<Long>> following;
    
    public UserCache() {
        userEntries = new ConcurrentHashMap<>();
        following = new ConcurrentHashMap<>();
    }
    
    public void loadUsers(InputStream stream) {
        Util.readStream(stream, line -> {
            try {
                int firstComma = line.indexOf(',');
                int secondComma = line.indexOf(',', firstComma+1);
                if (firstComma == -1 || secondComma == -1) {
                    LOGGER.warn("Skipping loading of the user due to malform => first={}, second={}, line={}", firstComma, secondComma, line);
                    return;
                }
                User user = new User(Long.valueOf(line.substring(0, firstComma)), 
                        line.substring(firstComma+1, secondComma), line.substring(secondComma+1));
                userEntries.putIfAbsent(user.getUserId(), user);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        });
    }
    
    public void loadFollowers(InputStream stream) {
        Util.readStream(stream, line -> {
            // note that the first entry is the follower to the second entry
            try {
                int firstComma = line.indexOf(',');
                if (firstComma == -1) {
                    LOGGER.warn("Skipping loading of the follower due to malform => first={}, line={}", firstComma, line);
                    return;
                }
                Long follower = Long.valueOf(line.substring(0, firstComma));
                Long followee = Long.valueOf(line.substring(firstComma+1));
                
                follow(followee, follower);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        });
    }

    public void addUser(User user) {
        userEntries.putIfAbsent(user.getUserId(), user);
    }
    
    public Optional<User> getUser(long userId) {
        return userEntries.containsKey(userId) ? Optional.of(userEntries.get(userId)) : Optional.empty();
    }
    
    public void follow(long followee, long follower) {
        following.compute(followee, (key, value) -> {
           if (value == null) { 
               value = new HashSet<>();
           }
           
           value.add(follower);
           return value;
        });
    }
    
    public Set<User> getFollowers(long followee) {
        return following.containsKey(followee) ? 
                following.get(followee).stream()
                    .map(followerId -> userEntries.get(followerId))
                    .filter(value -> value != null)
                    .collect(Collectors.toSet())
                : Collections.emptySet();
    }
    
}
