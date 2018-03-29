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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jhk.pulsing.sandbox.timeline.pojo.User;

/**
 * @author Ji Kim
 */
public class UserCacheTest {
    
    private UserCache userCache;
    
    @BeforeEach
    public void setupAll() {
        userCache = new UserCache();
    }
    
    @Test
    public void testAdd() {
        assertFalse(userCache.getUser(100L).isPresent(), "User 100 should not be present");
        
        addUser(100L, "Minion", "Real Minion", userCache);
        assertTrue(userCache.getUser(100L).isPresent(), "User 100 should now be present");
    }
    
    @Test
    public void testFollow() {
        assertTrue(userCache.getFollowers(100L).isEmpty(), "Should have empty followers for 100");
        
        userCache.follow(100L, 200L);
        addUser(200L, "Minion", "Real Minion", userCache);
        
        assertFalse(userCache.getFollowers(100L).isEmpty(), "Should no longer be empty");
        
        userCache.follow(100L, 300L);
        addUser(300L, "Doctor Strange", "O_O", userCache);
        assertTrue(userCache.getFollowers(100L).size() == 2, "Should have 2 followers");
    }
    
    public static void addUser(long userId, String screenName, String displayName, UserCache userCache) {
        User user = new User(userId, screenName, displayName);
        userCache.addUser(user);
    }
    
}
