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

import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.web.common.Result;
import org.jhk.pulsing.web.dao.prod.db.cassandra.CassandraChatDao;
import org.jhk.pulsing.web.service.IChatService;
import org.springframework.stereotype.Service;

/**
 * @author Ji Kim
 */
@Service
public class ChatService implements IChatService {
    
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

}
