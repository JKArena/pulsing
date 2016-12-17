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
import java.util.UUID;

import javax.inject.Inject;

import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.web.common.Result;
import org.jhk.pulsing.web.pojo.light.Chat;
import org.jhk.pulsing.web.service.IChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    private IChatService chatService;
    
    @RequestMapping(value="/queryChatLobbies", method=RequestMethod.GET)
    public @ResponseBody Result<Map<String, UUID>> queryChatLobby(UserId userId) {
        _LOGGER.debug("ChatController.queryChatLobbies: " + userId);
        
        return chatService.queryChatLobbies(userId);
    }
    
    @RequestMapping(value="/queryChatLobbyMessages", method=RequestMethod.GET)
    public @ResponseBody Result<List<Chat>> queryChatLobbyMessages(UUID cLId, Long timeStamp) {
        _LOGGER.debug("ChatController.queryChatLobbyMessages: " + cLId + " - " + timeStamp);
        
        return chatService.queryChatLobbyMessages(cLId, timeStamp);
    }
    
    @RequestMapping(value="/createChatLobby", method=RequestMethod.POST)
    public @ResponseBody Result<UUID> createChatLobby(UserId userId, String lobbyName) {
        _LOGGER.debug("ChatController.createChatLobby: " + userId + " - " + lobbyName);
        
        return chatService.createChatLobby(userId, lobbyName);
    }
    
}
