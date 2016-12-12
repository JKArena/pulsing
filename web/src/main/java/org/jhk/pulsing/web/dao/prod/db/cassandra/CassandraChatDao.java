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
import java.util.Optional;
import java.util.UUID;

import org.apache.cassandra.utils.UUIDGen;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.web.dao.prod.db.AbstractCassandraDao;
import org.jhk.pulsing.web.pojo.light.Chat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.datastax.driver.core.schemabuilder.SchemaStatement;

/**
 * @author Ji Kim
 */
@Repository
public class CassandraChatDao extends AbstractCassandraDao {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(CassandraChatDao.class);
    private static final String _CHAT_LOBBY_TABLE = "CHAT_LOBBY_TABLE";
    private static final String _CHAT_MESSAGE_TABLE = "CHAT_MESSAGE_TABLE";
    
    private static PreparedStatement _CHAT_LOBBY_QUERY;
    private static PreparedStatement _CHAT_LOBBY_INSERT;
    
    private static PreparedStatement _CHAT_MESSAGE_QUERY;
    private static PreparedStatement _CHAT_MESSAGE_INSERT;
    
    public Optional<UUID> createChatLobby(UserId userId, String lobbyName) {
        
        BoundStatement cLQuery = _CHAT_LOBBY_QUERY.bind(userId.getId());
        ResultSet cLQResult = getSession().execute(cLQuery);
        
        _LOGGER.info("CassandraChatDao.createChatLobby cLQResult : " + cLQResult);
        for(Row chatLobby : cLQResult) {
            if(chatLobby.getString("name").equals(lobbyName)) {
                return Optional.empty();
            }
        }
        UUID cLId = UUIDGen.getTimeUUID();
        
        BoundStatement cLInsert = _CHAT_LOBBY_INSERT.bind(cLId, userId.getId(), lobbyName);
        getSession().executeAsync(cLInsert);
        
        return Optional.of(cLId);
    }
    
    public void chatLobbyMessageInsert(UUID cLId, UserId from, UserId to, long timeStamp, String message) {
        
        BoundStatement cLMInsert = _CHAT_MESSAGE_INSERT.bind(cLId, from.getId(), to.getId(), timeStamp, message);
        getSession().executeAsync(cLMInsert);
    }
    
    public List<Chat> chatLobbyMessageQuery(UUID cLId, Long timeStamp) {
        
        BoundStatement cLMQuery = _CHAT_MESSAGE_QUERY.bind(cLId, timeStamp);
        ResultSet cLMQResult = getSession().execute(cLMQuery);
        
        List<Chat> cLMessages = new LinkedList<>();
        
        for(Row message : cLMQResult) {
            Chat entry = new Chat();
            entry.setUserId(message.getLong("user_id"));
            entry.setTimeStamp(message.getLong("timestamp"));
            entry.setMessage(message.getString("message"));
            
            cLMessages.add(entry);
        }
        
        return cLMessages;
    }
    
    @Override
    public void init() {
        super.init();
        
        setUpChatLobbyTable();
        setUpChatMessageTable();
    }
    
    private void setUpChatLobbyTable() {
        
        getSession().execute("CREATE TABLE " + getKeySpace() + "." + _CHAT_LOBBY_TABLE + " (" +
                "user_id bigint PRIMARY KEY," +
                "name text," +
                "chat_lobby_id timeuuid )");
        
        _CHAT_LOBBY_QUERY = getSession().prepare("SELECT name FROM " + _CHAT_LOBBY_TABLE + " WHERE user_id=?");
        _CHAT_LOBBY_INSERT = getSession().prepare("INSERT INTO " + _CHAT_LOBBY_TABLE + " (chat_lobby_id, user_id, name) VALUES (?, ?, ?)");
    }
    
    private void setUpChatMessageTable() {
        SchemaStatement cMSchemaStatement = SchemaBuilder.createTable(_CHAT_MESSAGE_TABLE)
                .ifNotExists()
                .addPartitionKey("chat_lobby_id", DataType.timeuuid())
                .addClusteringColumn("timestamp", DataType.timestamp())
                .addColumn("user_id", DataType.bigint())
                .addColumn("message", DataType.text())
                .withOptions().clusteringOrder("timestamp", SchemaBuilder.Direction.DESC);
        
        getSession().execute(cMSchemaStatement);
        
        _CHAT_MESSAGE_QUERY = getSession().prepare("SELECT * FROM " + _CHAT_MESSAGE_TABLE + 
                " WHERE chat_lobby_id=? AND timestamp < ? LIMIT 40");
        _CHAT_MESSAGE_INSERT = getSession().prepare("INSERT INTO " + _CHAT_MESSAGE_TABLE + 
                " (chat_lobby_id, user_id, timestamp, message) VALUES (?, ?, ?, ?)");
    }
    
    @Override
    public void destroy() {
        
        getSession().execute(SchemaBuilder.dropTable(_CHAT_LOBBY_TABLE));
        getSession().execute(SchemaBuilder.dropTable(_CHAT_MESSAGE_TABLE));
        
        super.destroy();
    }
    
    @Override
    protected String getKeySpace() {
        return CommonConstants.CASSANDRA_KEYSPACE.CHAT.toString();
    }

}
