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
package org.jhk.pulsing.web.dao.dev;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jhk.pulsing.serialization.avro.records.Address;
import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.web.common.Result;
import static org.jhk.pulsing.web.common.Result.CODE.*;
import org.jhk.pulsing.web.dao.IUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public class UserDao implements IUserDao {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(UserDao.class);
    
    private static final ConcurrentMap<UserId, User> _MOCKED_USERS = new ConcurrentHashMap<>();
    
    static {
        UserId userId = UserId.newBuilder().build();
        userId.setId(1111L);
        
        User user = User.newBuilder().build();
        user.setId(userId);
        user.setEmail("mathXphysics@truth.com");
        user.setName("Isaac Newton");
        user.setPassword("genius");
        
        Address address = Address.newBuilder().build();
        address.setAddress("Woolsthorpe-by-Colsterworth, United Kingdom");
        address.setCoordinates(Stream.of(52.809863D, -0.62877D).collect(Collectors.toList()));
        
        user.setAddress(address);
        
        _MOCKED_USERS.put(userId, user);
        
        userId = UserId.newBuilder().build();
        userId.setId(9999L);
        
        user = User.newBuilder().build();
        user.setId(userId);
        user.setEmail("philosophy@truth.com");
        user.setName("Socrates");
        user.setPassword("genius");
        
        address = Address.newBuilder().build();
        address.setAddress("Athens Greece");
        address.setCoordinates(Stream.of(37.9667D, 23.7167D).collect(Collectors.toList()));
        
        user.setAddress(address);
        
        _MOCKED_USERS.put(userId, user);
    }

    @Override
    public Result<User> getUser(UserId userId) {
        _LOGGER.info("getUser", userId);
        
        User user = _MOCKED_USERS.get(userId);
        Result<User> gResult = user == null ? new Result<User>(FAILURE, "Failed to get user " + userId) :
            new Result<User>(SUCCESS, user);
        
        return gResult;
    }

    @Override
    public Result<UserId> createUser(User user) {
        _LOGGER.info("createUser", user);
        
        _MOCKED_USERS.put(user.getId(), user);
        
        return new Result<UserId>(SUCCESS, user.getId());
    }
    
    @Override
    public Result<User> validateUser(String email, String password) {
        
        Optional<User> filteredUser = _MOCKED_USERS.values().stream()
            .filter(user -> user.getEmail().equals(email) && user.getPassword().equals(password))
            .findAny();
        
        return filteredUser.isPresent() ? new Result<User>(SUCCESS, filteredUser.get()) : 
                new Result<User>(FAILURE, "Failed to validate with " + email + ":" + password);
    }
    
}
