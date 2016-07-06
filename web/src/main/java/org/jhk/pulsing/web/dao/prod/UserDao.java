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

import javax.inject.Inject;

import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.web.common.Result;
import org.jhk.pulsing.web.dao.IUserDao;
import org.jhk.pulsing.web.dao.prod.db.MySqlUserDao;
import org.jhk.pulsing.web.dao.prod.db.RedisUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public class UserDao implements IUserDao {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(UserDao.class);
    
    private MySqlUserDao mySqlUserDao;
    
    private RedisUserDao redisUserDao;
    
    @Override
    public Result<User> getUser(UserId userId) {
        _LOGGER.debug("UserDao.getUser " + userId);
        
        mySqlUserDao.getUser(userId);
        
        return null;
    }

    @Override
    public Result<User> createUser(User user) {
        _LOGGER.debug("UserDao.createUser " + user);
        
        mySqlUserDao.createUser(user);
        
        return null;
    }

    @Override
    public Result<User> validateUser(String email, String password) {
        _LOGGER.debug("UserDao.validateUser " + email + " : " + password);
        
        mySqlUserDao.validateUser(email, password);
        
        return null;
    }
    
    @Inject
    public void setMySqlUserDao(MySqlUserDao mySqlUserDao) {
        this.mySqlUserDao = mySqlUserDao;
    }
    
    @Inject
    public void setRedisUserDao(RedisUserDao redisUserDao) {
        this.redisUserDao = redisUserDao;
    }

}
