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
package org.jhk.pulsing.web.dao.prod.db.cassandra;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.web.dao.prod.db.AbstractCassandraDao;
import org.jhk.pulsing.db.cassandra.chat.ChatLobbyTable;
import org.jhk.pulsing.db.cassandra.chat.ChatMessageTable;
import org.jhk.pulsing.web.pojo.light.Chat;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

/**
 * @author Ji Kim
 */
@Repository
public class CassandraChatDao extends AbstractCassandraDao {
    
    private static final int _DEFAULT_CHAT_LOBBY_LISTING = 10;
    
    private ChatLobbyTable _chatLobbyTable;
    private ChatMessageTable _chatMessageTable;
    
    public Map<String, UUID> queryChatLobbies(UserId userId) {
        
        return _chatLobbyTable.queryChatLobbies(userId, _DEFAULT_CHAT_LOBBY_LISTING);
    }
    
    public boolean userHasChatLobbyId(UserId userId, UUID cLId) {
        Map<String, UUID> cLobbies = queryChatLobbies(userId);
        
        boolean exists = cLobbies.values().stream().anyMatch(chatLobbyId -> {
            return chatLobbyId.equals(cLId);
        });
        
        return exists;
    }
    
    public Optional<UUID> createChatLobby(UserId userId, String lobbyName) {
        
        return _chatLobbyTable.createChatLobby(userId, lobbyName);
    }
    
    public boolean chatLobbyUnSubscribe(UserId userId, UUID cLId, String lobbyName) {
        
        boolean exists = userHasChatLobbyId(userId, cLId);
        
        if(!exists) {
            return false;
        }
        
        _chatLobbyTable.chatLobbyUnSubscribe(userId, cLId, lobbyName);
        
        return true;
    }
    
    public void chatLobbyMessageInsert(UUID cLId, UUID msgId, long from, long timeStamp, String message) {
        
        _chatMessageTable.messageInsert(cLId, msgId, from, timeStamp, message);
    }
    
    public List<Chat> queryChatLobbyMessages(UUID cLId, UserId userId, Long timeStamp) {
        ResultSet cLMQResult = _chatMessageTable.messageQuery(cLId, timeStamp);
        
        List<Chat> cLMessages = new LinkedList<>();
        
        for(Row message : cLMQResult) {
            Chat entry = new Chat();
            entry.setUserId(message.getLong("user_id"));
            entry.setTimeStamp(message.getLong("timestamp"));
            entry.setMessage(message.getString("message"));
            
            UUID msgId = message.getUUID("msg_id");
            _chatMessageTable.messageViewCountInsert(msgId, userId.getId(), timeStamp);
            
            ResultSet mvcResult = _chatMessageTable.messageViewCountQuery(msgId);
            
            entry.setMessageViews(mvcResult.one().getLong("user_views"));
            
            cLMessages.add(entry);
        }
        return cLMessages;
    }
    
    public Optional<Boolean> chatLobbySubscribe(UUID cLId, String lobbyName, UserId userId) {
        
        boolean exists = userHasChatLobbyId(userId, cLId);
        
        if(exists) {
            return Optional.empty();
        }
        _chatLobbyTable.chatLobbySubscribe(userId, lobbyName, cLId);
        
        return Optional.of(true);
    }
    
    @Override
    public void init() {
        super.init();
        
        _chatLobbyTable = new ChatLobbyTable(getSession(), getKeySpace());
        _chatMessageTable = new ChatMessageTable(getSession(), getKeySpace());
    }
    
    @Override
    public void destroy() {
        
        _chatLobbyTable.destroy();
        _chatMessageTable.destroy();
        
        super.destroy();
    }
    
    @Override
    protected String getKeySpace() {
        return CommonConstants.CASSANDRA_KEYSPACE.CHAT.toString();
    }

}
