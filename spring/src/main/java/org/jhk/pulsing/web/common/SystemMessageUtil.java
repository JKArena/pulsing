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
package org.jhk.pulsing.web.common;

import java.time.Instant;
import java.util.UUID;

import org.jhk.pulsing.web.pojo.light.Alert;
import org.jhk.pulsing.chat.response.Chat;
import org.jhk.pulsing.client.user.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Ji Kim
 */
public final class SystemMessageUtil {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(SystemMessageUtil.class);
    
    private static ObjectMapper _objectMapper = new ObjectMapper();
    
    private SystemMessageUtil() {
        super();
    }
    
    public static void sendSystemAlertMessage(SimpMessagingTemplate template, long userId, String message) {
        try {
            template.convertAndSend("/topics/alert/" + userId, _objectMapper.writeValueAsString(createSystemAlertMessage(message)));
        } catch (Exception except) {
            _LOGGER.error("Error while converting for system alert message ", except);
            except.printStackTrace();
        }
    }
    
    public static void sendSystemChatMessage(SimpMessagingTemplate template, UUID cLId, String message) {
        try {
            template.convertAndSend("/topics/chat/" + cLId, _objectMapper.writeValueAsString(createSystemChatMessage(message)));
        } catch (Exception except) {
            _LOGGER.error("Error while converting for system chat message ", except);
            except.printStackTrace();
        }
    }
    
    private static Alert createSystemAlertMessage(String message) {
        
        Alert msg = new Alert();
        msg.setMessage(message);
        msg.setTimeStamp(Instant.now().toEpochMilli());
        
        return msg;
    }
    
    private static Chat createSystemChatMessage(String message) {
        
        Chat msg = new Chat();
        msg.setMessage(message);
        msg.setName("System Notification");
        msg.setType(Chat.TYPE.SYSTEM_MESSAGE);
        msg.setUserId(IUserService.SYSTEM_USER_ID);
        
        return msg;
    }
    
}
