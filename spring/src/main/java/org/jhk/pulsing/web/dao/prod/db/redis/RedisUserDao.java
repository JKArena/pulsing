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

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.shared.util.RedisConstants;
import org.jhk.pulsing.shared.util.RedisConstants.INVITATION_ID;
import org.jhk.pulsing.shared.util.Util;
import org.jhk.pulsing.web.dao.prod.db.AbstractRedisDao;
import org.jhk.pulsing.web.pojo.light.Invitation;
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
    
    /**
     * For now just invitations, later additional alerts
     * 
     * @param userId
     * @return
     */
    public List<Invitation> getAlertList(UserId userId) {
        
        List<Invitation> invitations = new LinkedList<>();
        List<String> remove = new LinkedList<>();
        
        String setKey = INVITATIONS_.toString() + userId.getId();
        long current = Instant.now().toEpochMilli();
        
        Set<String> invitationSet = getJedis().smembers(setKey);
        _LOGGER.debug("RedisUserDao.getAlertList: invitation size " + invitationSet.size());
        
        invitationSet.stream()
            .forEach(value -> {
                try {
                    Invitation invitation = getObjectMapper().readValue(value, Invitation.class);
                    
                    if(invitation.getExpiration() <= current) {
                        //expired
                        remove.add(value);
                    }else {
                        invitations.add(invitation);
                    }
                } catch (Exception exception) {
                    _LOGGER.error("Error reading invitation", exception);
                    exception.printStackTrace();
                }
            });
        
        if(!remove.isEmpty()) {
            getJedis().srem(setKey, remove.toArray(new String[0]));
        }
        
        return invitations;
    }
    
    /**
     * Since can expire by key only (i.e. can't use sadd), generate the id of the invitation and use any value for the value
     * 
     * @param userId
     * @param prefix
     * @param expiration in seconds
     * @return
     */
    public String createInvitationId(long toUserId, long fromUserId, INVITATION_ID prefix, int expiration) {
        _LOGGER.debug("RedisUserDao.createInvitationId: " + toUserId + "/" + fromUserId + " - " + prefix + ", " + expiration);
        
        String key = new StringJoiner("_").add(prefix.toString()).add(toUserId+"").add(Util.uniqueId()+"").toString();
        getJedis().setex(key, expiration, "1");
        
        try {
            Invitation invitation = new Invitation(fromUserId, prefix, key, (expiration*1000)+Instant.now().toEpochMilli());
            getJedis().sadd(INVITATIONS_.toString() + toUserId, getObjectMapper().writeValueAsString(invitation));
        } catch (JsonProcessingException jpException) {
            _LOGGER.error("Error writing invitation", jpException);
            jpException.printStackTrace();
        }
        
        return key;
    }
    
    public boolean removeInvitationId(long userId, String invitationId) {
        _LOGGER.debug("RedisUserDao.removeInvitationId: " + invitationId);
        
        String setKey = INVITATIONS_.toString() + userId;
        
        Set<String> invites = getJedis().smembers(setKey);
        Optional<String> invite = invites.stream()
                .filter(value -> {
                    try {
                        Invitation invitation = getObjectMapper().readValue(value, Invitation.class);
                        if(invitation.getInvitationId().equals(invitationId)) {
                            return true;
                        }
                    } catch (Exception exception) {
                        _LOGGER.error("Error reading invitation", exception);
                        exception.printStackTrace();
                    }
                    return false;
                })
                .findAny();
        
        if(invite.isPresent()) {
            getJedis().srem(setKey, invite.get());
        }
        
        return getJedis().del(invitationId) == 1L;
    }
    
}
