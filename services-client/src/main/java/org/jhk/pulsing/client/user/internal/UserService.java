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
package org.jhk.pulsing.client.user.internal;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.shared.util.RedisConstants.INVITATION_ID;
import org.jhk.pulsing.client.payload.Result;
import static org.jhk.pulsing.client.payload.Result.CODE.*;

import org.jhk.pulsing.db.mysql.user.MySqlUserDao;
import org.jhk.pulsing.client.payload.light.UserLight;
import org.jhk.pulsing.client.user.IUserService;
import org.springframework.stereotype.Service;

/**
 * Note that if transactional bean is implementing an interface, by default the proxy will be a Java Dynamic Proxy. 
 * This means that only external method calls that come in through the proxy will be intercepted any self-invocation calls 
 * will not start any transaction even if the method is annotated with @Transactional.
 * 
 * Also rollback only occur during RuntimeException so paradigm is to throw a RuntimeException when you wish for a 
 * rollback
 * 
 * @author Ji Kim
 */
@Service
public class UserService implements IUserService {
    
    @Inject
    @Named("mySqlUserDao")
    private MySqlUserDao mySqlUserDao;
    
    @Inject
    @Named("redisUserDao")
    private RedisUserDao redisUserDao;
    
    @Inject
    @Named("redisPulseDao")
    private RedisPulseDao redisPulseDao;
    
    @Override
    public Result<User> getUser(UserId userId) {
        Result<User> result = new Result<>(FAILURE, null, "Unable to find " + userId);
        
        Optional<User> user = mySqlUserDao.getUser(userId);
        if(user.isPresent()) {
            result = new Result<>(SUCCESS, user.get());
        }
        
        return result;
    }

    @Override
    public Result<User> createUser(User user) {
        
        if(mySqlUserDao.isEmailTaken(user.getEmail().toString())) {
            return new Result<User>(FAILURE, null, "Email is already taken " + user.getEmail());
        }
        
        Result<User> cUser = new Result<User>(FAILURE, null, "Failed in creating " + user);
        
        try {
            cUser = mySqlUserDao.createUser(user);
        } catch(RuntimeException eException) {
        }
        
        return cUser;
    }

    @Override
    public Result<User> validateUser(String email, String password) {
        Optional<User> user = mySqlUserDao.validateUser(email, password);
        
        return user.isPresent() ? new Result<>(SUCCESS, user.get()) : new Result<>(FAILURE, null, "Failed in validating " + email + " : " + password);
    }
    
    @Override
    public Result<String> logout(UserId userId) {
        
        Optional<UserLight> oUserLight = redisUserDao.getUserLight(userId.getId());
        
        if(oUserLight.isPresent()) {
            
            UserLight uLight = oUserLight.get();
            
            redisUserDao.removeUserLight(userId.getId());
            
            if(uLight.getSubscribedPulseId() != 0L) {
                redisPulseDao.unSubscribePulse(uLight);
            }
        }
        
        return new Result<>(SUCCESS, "loggedOut");
    }
    
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
    
    @Override
    public void storeUserLight(UserLight user) {
        redisUserDao.storeUserLight(user);
    }
    
    @Override
    public Optional<UserLight> getUserLight(long userId) {
        return redisUserDao.getUserLight(userId);
    }
    
}
