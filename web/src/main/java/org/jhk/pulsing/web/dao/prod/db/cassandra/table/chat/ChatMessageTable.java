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
package org.jhk.pulsing.web.dao.prod.db.cassandra.table.chat;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.jhk.pulsing.web.dao.prod.db.cassandra.table.ICassandraTable;
import org.jhk.pulsing.web.pojo.light.Chat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.datastax.driver.core.schemabuilder.SchemaStatement;

/**
 * @author Ji Kim
 */
public final class ChatMessageTable implements ICassandraTable {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(ChatMessageTable.class);
    
    private static final String _CHAT_MESSAGE_TABLE = "CHAT_MESSAGE_TABLE";
    
    private final PreparedStatement _CHAT_MESSAGE_QUERY;
    private final PreparedStatement _CHAT_MESSAGE_INSERT;
    
    private final Session _session;
    private final String _keySpace;
    
    public ChatMessageTable(Session session, String keySpace) {
        super();
        
        _session = session;
        _keySpace = keySpace;
        
        SchemaStatement cMSchemaStatement = SchemaBuilder.createTable(_CHAT_MESSAGE_TABLE)
                .ifNotExists()
                .addPartitionKey("chat_lobby_id", DataType.timeuuid())
                .addClusteringColumn("timestamp", DataType.bigint())
                .addColumn("user_id", DataType.bigint())
                .addColumn("message", DataType.text())
                .withOptions().clusteringOrder("timestamp", SchemaBuilder.Direction.DESC);
        
        _session.execute(cMSchemaStatement);
        
        _CHAT_MESSAGE_QUERY = _session.prepare("SELECT * FROM " + _CHAT_MESSAGE_TABLE + 
                " WHERE chat_lobby_id=? AND timestamp < ? LIMIT 40");
        _CHAT_MESSAGE_INSERT = _session.prepare("INSERT INTO " + _CHAT_MESSAGE_TABLE + 
                " (chat_lobby_id, user_id, timestamp, message) VALUES (?, ?, ?, ?)");
    }
    
    public void messageInsert(UUID cLId, long from, long timeStamp, String message) {
        _LOGGER.info("ChatMessageTable.messageInsert : " + cLId + " - " + ";" + message);
        
        BoundStatement cLMInsert = _CHAT_MESSAGE_INSERT.bind(cLId, from, timeStamp, message);
        _session.executeAsync(cLMInsert);
    }
    
    public List<Chat> messageQuery(UUID cLId, Long timeStamp) {
        _LOGGER.info("ChatMessageTable.messageQuery : " + cLId + " - " + timeStamp);
        
        BoundStatement cLMQuery = _CHAT_MESSAGE_QUERY.bind(cLId, timeStamp);
        ResultSet cLMQResult = _session.execute(cLMQuery);
        
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
    public void destroy() {
        _session.execute(SchemaBuilder.dropTable(_CHAT_MESSAGE_TABLE));
    }

}
