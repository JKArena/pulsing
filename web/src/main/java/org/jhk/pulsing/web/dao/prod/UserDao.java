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
package org.jhk.pulsing.web.dao.prod;

import java.util.Optional;

import javax.inject.Inject;

import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.web.common.Result;
import static org.jhk.pulsing.web.common.Result.CODE.*;
import org.jhk.pulsing.web.dao.IUserDao;
import org.jhk.pulsing.web.dao.prod.db.MySqlUserDao;
import org.jhk.pulsing.web.dao.prod.db.redis.RedisUserDao;

/**
 * @author Ji Kim
 */
public class UserDao implements IUserDao {
    
    @Inject
    private MySqlUserDao mySqlUserDao;
    
    @Inject
    private RedisUserDao redisUserDao;
    
    @Override
    public Result<User> getUser(UserId userId) {
        Result<User> result = new Result<>(FAILURE, "Unable to find " + userId);
        //TODO: remove later as holding this data in redis useless, but for docing 
        //for other data
        Optional<User> user = redisUserDao.getUser(userId);
        
        if(!user.isPresent()) {
            user = mySqlUserDao.getUser(userId);
        }
        
        if(user.isPresent()) {
            result = new Result<>(SUCCESS, user.get());
        }
        
        return result;
    }

    @Override
    public Result<User> createUser(User user) {
        Result<User> result = new Result<User>(FAILURE, "Failed in creating " + user); 
        Optional<User> createdUser = mySqlUserDao.createUser(user);
        
        if(createdUser.isPresent()) {
            result = new Result<>(SUCCESS, createdUser.get());
            redisUserDao.createUser(createdUser.get());
        }
        
        return result;
    }

    @Override
    public Result<User> validateUser(String email, String password) {
        Optional<User> user = mySqlUserDao.validateUser(email, password);
        
        return user.isPresent() ? new Result<User>(SUCCESS, user.get()) : new Result<User>(FAILURE, "Failed in validating " + email + " : " + password);
    }
    
}
