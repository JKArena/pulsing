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

import static org.jhk.pulsing.shared.util.RedisConstants.REDIS_KEY.*;

import java.util.Optional;

import org.jhk.pulsing.shared.util.RedisConstants;
import org.jhk.pulsing.web.dao.prod.db.AbstractRedisDao;
import org.jhk.pulsing.web.pojo.light.UserLight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Ji Kim
 */
@Repository
public class RedisUserDao extends AbstractRedisDao {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(RedisUserDao.class);
    
    public void removeUserLight(long userId) {
        _LOGGER.debug("RedisUserDao.removeUserLight: " + userId);

        getJedis().del(USER_LIGHT_.toString() + userId);
    }
    
    public void storeUserLight(UserLight userLight) {
        _LOGGER.debug("RedisUserDao.storeUserLight: " + userLight);
        
        try {
            getJedis().setex(USER_LIGHT_.toString() + userLight.getId(), RedisConstants.CACHE_EXPIRE_DAY, 
                                getObjectMapper().writeValueAsString(userLight));
        } catch (JsonProcessingException jpException) {
            _LOGGER.error("Error writing userLight", jpException);
            jpException.printStackTrace();
        }
    }
    
    public Optional<UserLight> getUserLight(long userId) {
        _LOGGER.debug("RedisUserDao.getUserLight: " + userId);
        
        Optional<UserLight> uLight = Optional.empty();
        String uString = getJedis().get(USER_LIGHT_.toString() + userId);
        
        if(uString != null) {
            _LOGGER.debug("RedisUserDao.getUserLight uString " + uString);
            
            try {
                UserLight rUserLight = getObjectMapper().readValue(uString, UserLight.class);
                uLight = Optional.of(rUserLight);
            } catch (Exception exception) {
                _LOGGER.error("Error reading userLight", exception);
                exception.printStackTrace();
            }
        }
        
        return uLight;
    }
    
}
