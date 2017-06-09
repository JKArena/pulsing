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

import static org.jhk.pulsing.web.common.Result.CODE.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;
import javax.inject.Named;

import org.jhk.pulsing.db.cassandra.PagingResult;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.shared.util.AesCipher;
import org.jhk.pulsing.shared.util.Util;
import org.jhk.pulsing.web.common.Result;
import org.jhk.pulsing.web.dao.prod.db.cassandra.CassandraChatDao;
import org.jhk.pulsing.web.elasticsearch.ESRestClient;
import org.jhk.pulsing.web.pojo.light.Chat;
import org.jhk.pulsing.web.service.IChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Ji Kim
 */
@Service
public class ChatService implements IChatService {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(ChatService.class);
    
    private static final String SECRETS_INDEX = "secrets";
    private static final String SECRET_MESSAGE_TYPE = "message";
    
    private final AesCipher aCipher = new AesCipher();
    private final ESRestClient esRClient = new ESRestClient();
    
    @Inject
    @Named("cassandraChatDao")
    private CassandraChatDao cassandraChatDao;

    @Override
    public Result<UUID> createChatLobby(UserId userId, String lobbyName) {
        
        Result<UUID> result = new Result<>(FAILURE, null, "Unable to create chat lobby " + lobbyName);
        Optional<UUID> chatLobbyId = cassandraChatDao.createChatLobby(userId, lobbyName);
        
        if(chatLobbyId.isPresent()) {
            result = new Result<>(SUCCESS, chatLobbyId.get());
        }
        
        return result;
    }
    
    @Override
    public Result<String> chatLobbyUnSubscribe(UserId userId, UUID cLId, String lobbyName) {
        
        boolean executed = cassandraChatDao.chatLobbyUnSubscribe(userId, cLId, lobbyName);
        
        return executed ? new Result<>(SUCCESS, "Successfully sent unsubscribe chat lobby " + cLId) : 
            new Result<>(FAILURE, "UserId " + userId + " is not subscribed to " + cLId);
    }

    @Override
    public Result<Map<String, UUID>> queryChatLobbies(UserId userId) {
        
        return new Result<>(SUCCESS, cassandraChatDao.queryChatLobbies(userId));
    }

    @Override
    public Result<PagingResult<List<Chat>>> queryChatLobbyMessages(UUID cLId, UserId userId, Optional<String> pagingState) {
        
        return new Result<>(SUCCESS, cassandraChatDao.queryChatLobbyMessages(cLId, userId, pagingState));
    }

    @Override
    public void chatLobbyMessageInsert(UUID cLId, UUID msgId, long from, long timeStamp, String message) {
        
        cassandraChatDao.chatLobbyMessageInsert(cLId, msgId, from, timeStamp, message);
    }
    
    @Override
    public Result<Boolean> chatLobbySubscribe(UUID cLId, String lobbyName, UserId userId) {
        
        Optional<Boolean> chatSubscribe = cassandraChatDao.chatLobbySubscribe(cLId, lobbyName, userId);
        
        return chatSubscribe.isPresent() ? new Result<>(SUCCESS, chatSubscribe.get()) : new Result<>(FAILURE, false, "Unable to subscribe to chatLobby " + lobbyName);
    }
    
    @Override
    public void sendSecretMessage(long toUserId, Chat message) {
        _LOGGER.info("ChatService.sendSecretMessage: " + toUserId + " > " + message);
        
        try {
            Map<String, String> sMessage = new HashMap<>();
            sMessage.put("to_user_id", toUserId + "");
            sMessage.put("timestamp", message.getTimeStamp() + "");
            sMessage.put("from_user_id", message.getUserId() + "");
            sMessage.put("content", aCipher.encrypt(message.getMessage()));
            
            esRClient.putDocument(SECRETS_INDEX, SECRET_MESSAGE_TYPE, Util.uniqueId(), sMessage);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException encryptException) {
            encryptException.printStackTrace();
            _LOGGER.error("ChatService.sendSecretMessage: encryption error");
        }
        
    }

}
