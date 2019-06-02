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
package org.jhk.pulsing.chat.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.jhk.pulsing.client.payload.Result;
import org.jhk.pulsing.client.payload.chat.Chat;
import org.jhk.pulsing.client.payload.chat.PagingResult;
import org.jhk.pulsing.db.cassandra.chat.CassandraChatDao;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.jhk.pulsing.client.payload.Result.CODE.*;

/**
 * Consider re-impl using grpc or thrift rpc
 * 
 * @author Ji Kim
 */
@CrossOrigin(origins="*")
@Controller
@RequestMapping("/chatService")
public class ChatServiceController {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(ChatServiceController.class);
    
    @Inject
    @Named("cassandraChatDao")
    private CassandraChatDao cassandraChatDao;
    
    @RequestMapping(value="/createChatLobby", method=RequestMethod.POST)
    public Result<UUID> createChatLobby(UserId userId, String lobbyName) {
        _LOGGER.debug("createChatLobby userId {}, lobbyName {}", userId, lobbyName);
        
        Result<UUID> result = new Result<>(FAILURE, null, "Unable to create chat lobby " + lobbyName);
        Optional<UUID> chatLobbyId = cassandraChatDao.createChatLobby(userId, lobbyName);
        
        if(chatLobbyId.isPresent()) {
            result = new Result<>(SUCCESS, chatLobbyId.get());
        }
        
        return result;
    }

    @RequestMapping(value="/chatLobbyUnSubscribe/{cLId}/{lobbyName}/{userId}", method=RequestMethod.PUT)
    public @ResponseBody Result<String> chatLobbyUnSubscribe(@PathVariable UUID cLId, @PathVariable String lobbyName, @PathVariable UserId userId) {
        _LOGGER.debug("chatLobbyUnSubscribe chatLobbyId {}, userId {}", cLId, userId);
        
        boolean executed = cassandraChatDao.chatLobbyUnSubscribe(userId, cLId, lobbyName);
        
        return executed ? new Result<>(SUCCESS, "Successfully sent unsubscribe chat lobby " + cLId) : 
            new Result<>(FAILURE, "UserId " + userId + " is not subscribed to " + cLId);
    }

    @RequestMapping(value="/queryChatLobbies/{userId}", method=RequestMethod.GET)
    public @ResponseBody Result<Map<String, UUID>> queryChatLobbies(@PathVariable UserId userId) {
        _LOGGER.debug("queryChatLobbies userId {}", userId);
        
        return new Result<>(SUCCESS, cassandraChatDao.queryChatLobbies(userId));
    }

    /**
     * @param cLId
     * @param timeStamp held as milliseconds in back end
     * @return
     */
    @RequestMapping(value="/queryChatLobbyMessages/{cLId}/{userId}", method=RequestMethod.GET)
    public @ResponseBody Result<PagingResult<List<Chat>>> queryChatLobbyMessages(@PathVariable UUID cLId, @PathVariable UserId userId, @RequestParam String paging) {
        _LOGGER.debug("queryChatLobbyMessages chatLobbyId {}, userId {}, paging {}", cLId, userId, paging);
        
        return new Result<>(SUCCESS, cassandraChatDao.queryChatLobbyMessages(cLId, userId, Optional.ofNullable(paging)));
    }

    @RequestMapping(value="/chatLobbyMessageInsert/{cLId}/{msgId}/", method=RequestMethod.PUT)
    public void chatLobbyMessageInsert(@PathVariable UUID cLId, @PathVariable UUID msgId,
            @RequestParam long from, @RequestParam long timeStamp, @RequestParam String message) {
        
        cassandraChatDao.chatLobbyMessageInsert(cLId, msgId, from, timeStamp, message);
    }

    @RequestMapping(value="/chatLobbySubscribe/{cLId}/{lobbyName}/{chatLobbyInvitationId}/{userId}", method=RequestMethod.PUT)
    public @ResponseBody Result<Boolean> chatLobbySubscribe(@PathVariable UUID cLId, @PathVariable String lobbyName, 
                                                                @PathVariable String chatLobbyInvitationId, @PathVariable UserId userId) {
        _LOGGER.debug("chatLobbySubscribe chatLobbyId {}, lobbyName {}, userId {}, chatLobbyInvitationId {}",
                cLId, lobbyName, userId, chatLobbyInvitationId);
        
        Optional<Boolean> chatSubscribe = cassandraChatDao.chatLobbySubscribe(cLId, lobbyName, userId);
        
        return chatSubscribe.isPresent() ? new Result<>(SUCCESS, chatSubscribe.get()) :
            new Result<>(FAILURE, false, "Unable to subscribe to chatLobby " + lobbyName);
    }

}
