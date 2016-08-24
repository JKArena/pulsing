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
package org.jhk.pulsing.web.dao.prod.db.redis;

import java.io.IOException;
import java.util.Optional;

import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;
import org.jhk.pulsing.shared.util.RedisConstants;
import org.jhk.pulsing.web.common.Result;
import static org.jhk.pulsing.web.common.Result.CODE.*;
import org.jhk.pulsing.web.dao.IUserDao;
import org.jhk.pulsing.web.dao.prod.db.AbstractRedisDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jhk.pulsing.shared.util.RedisConstants.REDIS_KEY.*;
import org.springframework.stereotype.Repository;

/**
 * @author Ji Kim
 */
@Repository
public class RedisUserDao extends AbstractRedisDao
                            implements IUserDao {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(RedisUserDao.class);
    
    @Override
    public Optional<User> getUser(UserId userId) {
        _LOGGER.debug("RedisUserDao.getUser " + userId);
        
        String userJson = getJedis().get(USER_.toString() + userId.getId());
        Optional<User> user = Optional.empty();
        
        if(userJson != null) {
            try {
                user = Optional.of(SerializationHelper.deserializeFromJSONStringToAvro(User.class, User.getClassSchema(), userJson));
            } catch (IOException dException) {
                dException.printStackTrace();
            }
        }
        
        return user;
    }
    
    @Override
    public Result<User> createUser(User user) {
        _LOGGER.debug("RedisUserDao.createUser " + user);
        
        Result<User> result;
        
        try {
            String userJson = SerializationHelper.serializeAvroTypeToJSONString(user);
            getJedis().setex(USER_.toString() + user.getId().getId(), RedisConstants.CACHE_EXPIRE_DAY, userJson);            
            result = new Result<>(SUCCESS, user);
        } catch (IOException sException) {
            result = new Result<>(FAILURE, sException.getMessage());
            sException.printStackTrace();
        }
        
        return result;
    }
    
}
