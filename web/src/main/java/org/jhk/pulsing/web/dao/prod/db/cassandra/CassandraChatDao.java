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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.web.dao.prod.db.AbstractCassandraDao;
import org.jhk.pulsing.web.dao.prod.db.cassandra.table.chat.ChatLobbyTable;
import org.jhk.pulsing.web.dao.prod.db.cassandra.table.chat.ChatMessageTable;
import org.jhk.pulsing.web.pojo.light.Chat;
import org.springframework.stereotype.Repository;

/**
 * @author Ji Kim
 */
@Repository
public class CassandraChatDao extends AbstractCassandraDao {
    
    private ChatLobbyTable _chatLobbyTable;
    private ChatMessageTable _chatMessageTable;
    
    public Map<String, UUID> queryChatLobbies(UserId userId) {
        
        return _chatLobbyTable.queryChatLobbies(userId);
    }
    
    public Optional<UUID> createChatLobby(UserId userId, String lobbyName) {
        
        return _chatLobbyTable.createChatLobby(userId, lobbyName);
    }
    
    public void chatLobbyMessageInsert(UUID cLId, long from, long timeStamp, String message) {
        
        _chatMessageTable.messageInsert(cLId, from, timeStamp, message);
    }
    
    public List<Chat> queryChatLobbyMessages(UUID cLId, Long timeStamp) {
        
        return _chatMessageTable.messageQuery(cLId, timeStamp);
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
