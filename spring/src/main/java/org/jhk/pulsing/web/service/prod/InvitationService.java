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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.shared.util.RedisConstants.INVITATION_ID;
import org.jhk.pulsing.client.payload.Result;

import static org.jhk.pulsing.client.payload.Result.CODE.*;

import org.jhk.pulsing.web.dao.prod.db.redis.RedisUserDao;
import org.jhk.pulsing.web.pojo.light.Invitation;
import org.jhk.pulsing.web.service.IInvitationService;
import org.springframework.stereotype.Service;

/**
 * Manages invitations which are held in Redis between users
 * 
 * @author Ji Kim
 */
@Service
public class InvitationService implements IInvitationService {
    
    @Inject
    @Named("redisUserDao")
    private RedisUserDao redisUserDao;
    
    @Override
    public Result<List<Invitation>> getAlertList(UserId userId) {
        
        return new Result<List<Invitation>>(SUCCESS, redisUserDao.getAlertList(userId));
    }
    
    @Override
    public String createInvitationId(long toUserId, long fromUserId, INVITATION_ID prefix, int expiration) {
        
        return redisUserDao.createInvitationId(toUserId, fromUserId, prefix, expiration);
    }
    
    @Override
    public boolean removeInvitationId(long userId, String invitationId) {
        
        return redisUserDao.removeInvitationId(userId, invitationId);
    }

}
