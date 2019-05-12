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
package org.jhk.pulsing.client.chat.internal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.jhk.pulsing.client.chat.IChatService;
import org.jhk.pulsing.client.payload.chat.Chat;
import org.jhk.pulsing.client.payload.chat.PagingResult;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.client.payload.Result;
import org.springframework.stereotype.Service;

/**
 * @author Ji Kim
 */
@Service
public class ChatService implements IChatService {

    @Override
    public Result<UUID> createChatLobby(UserId userId, String lobbyName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Result<String> chatLobbyUnSubscribe(UserId userId, UUID cLId, String lobbyName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Result<Map<String, UUID>> queryChatLobbies(UserId userId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Result<PagingResult<List<Chat>>> queryChatLobbyMessages(UUID cLId, UserId userId,
            Optional<String> pagingState) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void chatLobbyMessageInsert(UUID cLId, UUID msgId, long from, long timeStamp, String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Result<Boolean> chatLobbySubscribe(UUID cLId, String lobbyName, UserId userId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sendSecretMessage(long to, Chat message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Optional<String> checkPaging(String paging) {
        // TODO Auto-generated method stub
     // Paging.checkPaging
        return null;
    }

}
