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
package org.jhk.pulsing.web.dao.prod.db;

import java.io.IOException;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;
import org.jhk.pulsing.shared.util.RedisConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jhk.pulsing.shared.util.RedisConstants.REDIS_KEY.*;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;

/**
 * @author Ji Kim
 */
@Repository
public class RedisUserDao {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(RedisUserDao.class);
    
    private Jedis _jedis;
    
    public Optional<User> getUser(UserId userId) {
        _LOGGER.debug("RedisUserDao.getUser " + userId);
        
        String userJson = _jedis.get(USER_.toString() + userId.getId());
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

    public Optional<User> createUser(User user) {
        _LOGGER.debug("RedisUserDao.createUser " + user);
        
        Optional<User> oUser = Optional.empty();
        
        try {
            String userJson = SerializationHelper.serializeAvroTypeToJSONString(user);
            _jedis.setex(USER_.toString() + user.getId().getId(), RedisConstants.DEFAULT_CACHE_EXPIRE_SECONDS, userJson);            
            oUser = Optional.of(user);
        } catch (IOException sException) {
            sException.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    @PostConstruct
    public void init() {
        _jedis = new Jedis(RedisConstants.REDIS_HOST, RedisConstants.REDIS_PORT);
        _jedis.auth(RedisConstants.REDIS_PASSWORD);
    }
    
    @PreDestroy
    public void destroy() {
        if(_jedis != null && _jedis.isConnected()) {
            _jedis.flushAll();
            _jedis.quit();
        }
    }
    
}
