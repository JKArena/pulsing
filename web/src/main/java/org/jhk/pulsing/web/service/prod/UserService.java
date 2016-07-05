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
package org.jhk.pulsing.web.service.prod;

import javax.inject.Inject;

import org.jhk.pulsing.db.mysql.model.MUser;
import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.web.common.Result;
import org.jhk.pulsing.web.dao.IUserDao;
import org.jhk.pulsing.web.dao.prod.MySqlUserDao;
import org.jhk.pulsing.web.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public class UserService implements IUserService {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(UserService.class);
    
    @Inject
    private MySqlUserDao mySqlUserDao;
    
    @Inject
    private IUserDao redisUserDao;
    
    @Override
    public Result<User> getUser(UserId userId) {
        _LOGGER.debug("UserService.getUser" + userId);
        
        mySqlUserDao.getUser(userId);
        
        return new Result<User>(Result.CODE.FAILURE, "to avoid null for now");
    }

    @Override
    public Result<User> createUser(User user) {
        
        return new Result<User>(Result.CODE.FAILURE, "to avoid null for now");
    }

    @Override
    public Result<User> validateUser(String email, String password) {
        _LOGGER.debug("UserService.validateUser" + email + " - " + password);
        UserId sample = UserId.newBuilder().build();
        sample.setId(1234L);
        mySqlUserDao.getUser(sample);
        
        return new Result<User>(Result.CODE.FAILURE, "to avoid null for now");
    }
    
}
