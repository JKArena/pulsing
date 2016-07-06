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

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.shared.util.PulsingConstants;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;

/**
 * @author Ji Kim
 */
@Repository
public class RedisUserDao implements IUserOptionalDao {
    
    private Jedis _jedis;
    
    @PostConstruct
    public void init() {
        _jedis = new Jedis(PulsingConstants.REDIS_HOST, PulsingConstants.REDIS_PORT);
    }
    
    @PreDestroy
    public void destroy() {
        if(_jedis != null && _jedis.isConnected()) {
            _jedis.quit();
        }
    }

    @Override
    public Optional<User> getUser(UserId userId) {
        return Optional.empty();
    }

    @Override
    public Optional<User> createUser(User user) {
        return Optional.empty();
    }

    @Override
    public Optional<User> validateUser(String email, String password) {
        return Optional.empty();
    }
    
}
