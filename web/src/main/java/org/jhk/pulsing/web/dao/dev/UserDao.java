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

import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.web.common.Result;
import static org.jhk.pulsing.web.common.Result.CODE.*;
import org.jhk.pulsing.web.dao.IUserDao;

/**
 * @author Ji Kim
 */
public class UserDao implements IUserDao {
    
    private static final ConcurrentMap<UserId, User> _MOCKED_USERS = new ConcurrentHashMap<>();
    
    //simply use counter since in real db will use AUTO_INCREMENT with different range for partitions
    private static long USERID = 1000L;
    
    static {
        UserId userId = UserId.newBuilder().build();
        userId.setId(USERID++);
        
        User user = User.newBuilder().build();
        user.setId(userId);
        user.setEmail("mathXphysics@truth.com");
        user.setName("Isaac Newton");
        user.setPassword("genius");
        user.setCoordinates(Stream.of(52.809863D, -0.62877D).collect(Collectors.toList()));
        
        _MOCKED_USERS.put(userId, user);
        
        userId = UserId.newBuilder().build();
        userId.setId(USERID++);
        
        user = User.newBuilder().build();
        user.setId(userId);
        user.setEmail("philosophy@truth.com");
        user.setName("Socrates");
        user.setPassword("genius");
        user.setCoordinates(Stream.of(37.9667D, 23.7167D).collect(Collectors.toList()));
        
        _MOCKED_USERS.put(userId, user);
    }

    @Override
    public Optional<User> getUser(UserId userId) {
        User user = _MOCKED_USERS.get(userId);
        
        return Optional.ofNullable(user);
    }

    @Override
    public Result<User> createUser(User userSubmitted) {
        Optional<User> findUser = _MOCKED_USERS.values().stream()
                .filter(user -> user.getEmail().equals(userSubmitted.getEmail()))
                .findAny();
        
        if(findUser.isPresent()) {
            return new Result<>(FAILURE, "User with the email already exists " + userSubmitted.getName());
        }
        
        UserId userId = UserId.newBuilder().build();
        userId.setId(USERID++);
        
        userSubmitted.setId(userId);
        _MOCKED_USERS.put(userId, userSubmitted);
        
        return new Result<>(SUCCESS, userSubmitted);
    }
    
    public static Result<User> validateUser(String email, String password) {
        Optional<User> filteredUser = _MOCKED_USERS.values().stream()
            .filter(user -> { return user.getEmail().toString().equals(email) && user.getPassword().toString().equals(password);})
            .findAny();
        
        return filteredUser.isPresent() ? new Result<>(SUCCESS, filteredUser.get()) : 
                new Result<>(FAILURE, "Failed to validate with " + email + ":" + password);
    }
    
}
