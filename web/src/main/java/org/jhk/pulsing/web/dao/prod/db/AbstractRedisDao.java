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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jhk.pulsing.shared.util.RedisConstants;

import redis.clients.jedis.Jedis;

/**
 * @author Ji Kim
 */
public abstract class AbstractRedisDao {
    
    private Jedis _jedis;
    
    protected Jedis getJedis() {
        return _jedis;
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
