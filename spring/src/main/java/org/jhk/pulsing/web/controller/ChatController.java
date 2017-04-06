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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import org.jhk.pulsing.db.cassandra.PagingResult;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.web.common.CommonUtil;
import org.jhk.pulsing.web.common.Result;
import org.jhk.pulsing.web.common.SystemMessageUtil;

import static org.jhk.pulsing.web.common.Result.CODE.*;
import org.jhk.pulsing.web.pojo.light.Chat;
import org.jhk.pulsing.web.pojo.light.UserLight;
import org.jhk.pulsing.web.service.IChatService;
import org.jhk.pulsing.web.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Ji Kim
 */
@CrossOrigin(origins="*")
@Controller
@RequestMapping("/chat")
public class ChatController {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(ChatController.class);
    
    @Inject
    private IUserService userService;
    
    @Inject
    private IChatService chatService;
    
    @Inject
    private SimpMessagingTemplate template;
    
    @RequestMapping(value="/queryChatLobbies/{userId}", method=RequestMethod.GET)
    public @ResponseBody Result<Map<String, UUID>> queryChatLobbies(@PathVariable UserId userId) {
        _LOGGER.debug("ChatController.queryChatLobbies: " + userId);
        
        return chatService.queryChatLobbies(userId);
    }
    
    /**
     * @param cLId
     * @param timeStamp held as milliseconds in back end
     * @return
     */
    @RequestMapping(value="/queryChatLobbyMessages/{cLId}/{userId}", method=RequestMethod.GET)
    public @ResponseBody Result<PagingResult<List<Chat>>> queryChatLobbyMessages(@PathVariable UUID cLId, @PathVariable UserId userId, @RequestParam String paging) {
        _LOGGER.debug("ChatController.queryChatLobbyMessages: " + cLId + "/" + userId + " - " + paging);
        
        return chatService.queryChatLobbyMessages(cLId, userId, CommonUtil.checkPaging(paging));
    }
    
    @RequestMapping(value="/createChatLobby", method=RequestMethod.POST)
    public @ResponseBody Result<UUID> createChatLobby(UserId userId, String lobbyName) {
        _LOGGER.debug("ChatController.createChatLobby: " + userId + " - " + lobbyName);
        
        return chatService.createChatLobby(userId, lobbyName);
    }
    
    @RequestMapping(value="/chatLobbyUnSubscribe/{cLId}/{lobbyName}/{userId}", method=RequestMethod.PUT)
    public @ResponseBody Result<String> chatLobbyUnSubscribe(@PathVariable UUID cLId, @PathVariable String lobbyName, @PathVariable UserId userId) {
        _LOGGER.debug("ChatController.chatLobbyUnSubscribe: " + cLId + " - " + userId);
        
        Result<String> result = chatService.chatLobbyUnSubscribe(userId, cLId, lobbyName);
        
        if(result.getCode() == Result.CODE.SUCCESS) {
            Optional<UserLight> uLight = userService.getUserLight(userId.getId());
            SystemMessageUtil.sendSystemChatMessage(template, cLId, 
                    "User " + (uLight.isPresent() ? uLight.get().getName() + " " : "") + " left the chat lobby ");
        }
        
        return result;
    }
    
    @RequestMapping(value="/chatLobbySubscribe/{cLId}/{lobbyName}/{chatLobbyInvitationId}/{userId}", method=RequestMethod.PUT)
    public @ResponseBody Result<Boolean> chatLobbySubscribe(@PathVariable UUID cLId, @PathVariable String lobbyName, 
                                                                @PathVariable String chatLobbyInvitationId, @PathVariable UserId userId) {
        _LOGGER.debug("ChatController.chatLobbySubscribe: " + cLId + " - " + lobbyName + " : " + userId + ";" + chatLobbyInvitationId);
        
        if(!userService.removeInvitationId(userId.getId(), chatLobbyInvitationId)) {
            return new Result<>(FAILURE, null, "Failed to subscribe to chatLobby " + lobbyName + " - the invitationId has expired.");
        }
        
        Result<Boolean> chatSubscribe = chatService.chatLobbySubscribe(cLId, lobbyName, userId);
        
        if(chatSubscribe.getData()) {
            Optional<UserLight> uLight = userService.getUserLight(userId.getId());
            SystemMessageUtil.sendSystemChatMessage(template, cLId, 
                    "User " + (uLight.isPresent() ? uLight.get().getName() + " " : "") + " joined the chat lobby " + lobbyName + ", welcome him/her!!!");
        }
        
        return chatSubscribe;
    }
    
}
