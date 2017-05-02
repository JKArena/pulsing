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
package org.jhk.pulsing.web.pojo.light;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jhk.pulsing.shared.util.CommonConstants;

/**
 * @author Ji Kim
 */
public class Chat {
    
    public enum TYPE {
        PULSE, CHAT_LOBBY, CHAT_LOBBY_INVITE, CHAT_LOBBY_JOIN, FRIEND_REQUEST, SYSTEM_MESSAGE;
    }
    
    private String _message;
    private long _userId;
    private String _name;
    private String _picturePath;
    private Map<String, Object> _data;
    private long _timeStamp;
    private TYPE _type;
    private UUID _msgId;
    private long _messageViews;
    
    public Chat() {
        super();
        
        _type = TYPE.PULSE;
        _data = new HashMap<>();
    }
    
    public String getMessage() {
        return _message;
    }
    public void setMessage(String message) {
        _message = message;
    }
    
    public Map<String, Object> getData() {
        return _data;
    }
    public void setData(Map<String, Object> data) {
        _data = data;
    }
    public void addData(String key, Object value) {
        _data.put(key, value);
    }
    
    public TYPE getType() {
        return _type;
    }
    public void setType(TYPE type) {
        _type = type;
    }
    
    public long getUserId() {
        return _userId;
    }
    public void setUserId(long userId) {
        _userId = userId;
    }
    
    public String getName() {
        return _name;
    }
    public void setName(String name) {
        _name = name;
    }
    
    public String getPicturePath() {
        return _picturePath;
    }
    public void setPicturePath(String picturePath) {
        _picturePath = picturePath;
    }
    
    public long getTimeStamp() {
        return _timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        _timeStamp = timeStamp;
    }
    
    public long getMessageViews() {
        return _messageViews;
    }
    public void setMessageViews(long messageViews) {
        _messageViews = messageViews;
    }
    
    public UUID getMsgId() {
        return _msgId;
    }
    public void setMsgId(UUID msgId) {
        _msgId = msgId;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("userId: " + _userId + ", ");
        builder.append("name: " + _name + ", ");
        builder.append("type: " + _type + ", ");
        builder.append("message: " + _message + ", ");
        builder.append("data: " + _data + ", ");
        builder.append("msgId: " + _msgId + ", ");
        builder.append("messageViews: " + _messageViews);
        builder.append("}");
        return builder.toString();
    }
    
    @Override
    public int hashCode() {
        int hashCodeVal = CommonConstants.HASH_CODE_INIT_VALUE;
        hashCodeVal = CommonConstants.HASH_CODE_MULTIPLY_VALUE * hashCodeVal + _message.hashCode();
        hashCodeVal = CommonConstants.HASH_CODE_MULTIPLY_VALUE * hashCodeVal + (int) _userId;
        hashCodeVal = CommonConstants.HASH_CODE_MULTIPLY_VALUE * hashCodeVal + _name.hashCode();
        return hashCodeVal;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Chat)) {
            return false;
        }
        
        Chat casted = (Chat) obj;
        return casted._message.equals(_message) && casted._userId == _userId;
    }
    
}
