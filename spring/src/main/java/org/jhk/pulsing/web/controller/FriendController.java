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
package org.jhk.pulsing.web.controller;

import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.shared.util.RedisConstants;
import org.jhk.pulsing.web.common.Result;
import org.jhk.pulsing.web.common.SystemMessageUtil;
import org.jhk.pulsing.web.pojo.light.UserLight;

import static org.jhk.pulsing.web.common.Result.CODE.*;
import org.jhk.pulsing.web.service.IFriendService;
import org.jhk.pulsing.web.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Ji Kim
 */
@CrossOrigin(origins="*")
@Controller
@RequestMapping("/friend")
public class FriendController {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(FriendController.class);
    
    private static final int FRIEND_REQUEST_INVITE_EXPIRATION = 300; // 5 minutes
    
    private ObjectMapper _objectMapper = new ObjectMapper();
    
    @Inject
    private IFriendService friendService;
    
    @Inject
    private IUserService userService;
    
    @Inject
    private SimpMessagingTemplate template;
    
    /**
     * 1) Check whether userId and friendId are friends, if not
     * 2) Create a temp UUID which will be placed in Redis as a timeout token for friendId to confirm that he/she wants to be friend
     * 3) If friendId confirms he/she wants to be friend then a follow up REST call will be made
     * 
     * @param userId
     * @param friendId
     * @return
     */
    @RequestMapping(value="/friendRequest", method=RequestMethod.POST)
    public @ResponseBody Result<String> friendRequest(UserId userId, UserId friendId) {
        _LOGGER.debug("FriendController.friendRequest: " + userId + " - " + friendId);
        
        boolean areFriends = friendService.areFriends(userId, friendId);
        Optional<UserLight> uLight = userService.getUserLight(userId.getId());
        
        if(areFriends) {
            return new Result<>(FAILURE, null, "User " + userId + " is already friends with " + friendId);
        }
        
        String invitationId = userService.createInvitationId(userId.getId(), RedisConstants.INVITATION_ID.FRIEND_REQUEST_INVITE_, FRIEND_REQUEST_INVITE_EXPIRATION);
        SystemMessageUtil.sendSystemAlertMessage(template, userId,
                "Friend request from " + uLight.get().getName());
        
        return new Result<>(SUCCESS, invitationId, "Sent out friend request to friend");
    }
    
}
