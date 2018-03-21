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
package org.jhk.pulsing.db.cassandra.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.cassandra.utils.UUIDGen;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.db.cassandra.ICassandraTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;

/**
 * @author Ji Kim
 */
public final class ChatLobbyTable implements ICassandraTable {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(ChatLobbyTable.class);
    
    private static final String _CHAT_LOBBY_TABLE = "CHAT_LOBBY_TABLE";
    
    private final PreparedStatement _CHAT_LOBBY_QUERY;
    private final PreparedStatement _CHAT_LOBBY_INSERT;
    
    private final Session _session;
    private final String _keySpace;
    
    public ChatLobbyTable(Session session, String keySpace) {
        super();
        
        _session = session;
        _keySpace = keySpace;
        
        _session.execute("CREATE TABLE " + _keySpace + "." + _CHAT_LOBBY_TABLE + " (" +
                "chat_lobby_id timeuuid," +
                "user_id bigint," +
                "name text," +
                "rank int," +
                "PRIMARY KEY (user_id, rank, chat_lobby_id)" + //user_id for partitioning and rank + chat_lobby_id for clustering
                " )" + 
                "WITH CLUSTERING ORDER BY (rank DESC, chat_lobby_id DESC);");
        
        _CHAT_LOBBY_QUERY = _session.prepare("SELECT name, chat_lobby_id, rank FROM " + _CHAT_LOBBY_TABLE + " WHERE user_id=? AND rank > 0 LIMIT ?");
        _CHAT_LOBBY_INSERT = _session.prepare("INSERT INTO " + _CHAT_LOBBY_TABLE + " (chat_lobby_id, user_id, name, rank) VALUES (?, ?, ?, ?)");
    }
    
    public Map<String, UUID> queryChatLobbies(UserId userId) {
        _LOGGER.info("ChatLobbyTable.queryChatLobbies : {}", userId);
        
        Map<String, UUID> chatLobbies = new HashMap<>();
        
        BoundStatement cLQuery = _CHAT_LOBBY_QUERY.bind(userId.getId());
        ResultSet cLQResult = _session.execute(cLQuery);
        
        _LOGGER.info("ChatLobbyTable.queryChatLobbies cLQResult : {}", cLQResult);
        cLQResult.forEach(chatLobby -> {
            
            String chatLobbyName = chatLobby.getString("name");
            int rank = chatLobby.getInt("rank");
            
            _LOGGER.info("ChatLobbyTable.queryChatLobbies chatLobbyName={}, rank={}", chatLobbyName, rank);
            chatLobbies.put(chatLobbyName, chatLobby.getUUID("chat_lobby_id"));
        });
        
        return chatLobbies;
    }
    
    /**
     * TODO use Spark SQL for query of whether the entry exists, since need to query all
     * 
     * @param userId
     * @param lobbyName
     * @return
     */
    public boolean chatLobbyExists(UserId userId, String lobbyName) {
        Map<String, UUID> qCLobby = queryChatLobbies(userId);
        return qCLobby.containsKey(lobbyName);
    }
    
    public Optional<UUID> createChatLobby(UserId userId, String lobbyName) {
        _LOGGER.info("ChatLobbyTable.createChatLobby : userId={}, lobbyName={}", userId, lobbyName);
        
        if(chatLobbyExists(userId, lobbyName)) return Optional.empty();
        
        UUID cLId = UUIDGen.getTimeUUID();
        
        return chatLobbySubscribe(userId, lobbyName, cLId);
    }
    
    public void chatLobbyUnSubscribe(UserId userId, UUID cLId, String lobbyName) {
        _LOGGER.info("ChatLobbyTable.chatLobbyUnSubscribe : userId={}, cLId={}", userId, cLId);
        
        _session.executeAsync(_CHAT_LOBBY_INSERT.bind(cLId, userId.getId(), lobbyName, -1));
    }
    
    public Optional<UUID> chatLobbySubscribe(UserId userId, String lobbyName, UUID cLId) {
        _LOGGER.info("ChatLobbyTable.chatLobbySubscribe : userId={}, lobbyName={}", userId, lobbyName);
        
        BoundStatement cLInsert = _CHAT_LOBBY_INSERT.bind(cLId, userId.getId(), lobbyName, 1);
        _session.executeAsync(cLInsert);
        
        return Optional.of(cLId);
    }
    
    @Override
    public void destroy() {
        
        _session.execute(SchemaBuilder.dropTable(_CHAT_LOBBY_TABLE));
    }
    
}
