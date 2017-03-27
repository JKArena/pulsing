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

import org.jhk.pulsing.shared.util.CommonConstants;

/**
 * @author Ji Kim
 */
public class Alert {
    
    public enum TYPE {
        FRIEND_INVITE;
    }
    
    private String _message;
    private long _userId;
    private long _timeStamp;
    private TYPE _type;
    
    public String getMessage() {
        return _message;
    }
    public void setMessage(String message) {
        _message = message;
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
    
    public long getTimeStamp() {
        return _timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        _timeStamp = timeStamp;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("userId: " + _userId + ", ");
        builder.append("type: " + _type + ", ");
        builder.append("message: " + _message);
        builder.append("}");
        return builder.toString();
    }
    
    @Override
    public int hashCode() {
        int hashCodeVal = CommonConstants.HASH_CODE_INIT_VALUE;
        hashCodeVal = CommonConstants.HASH_CODE_MULTIPLY_VALUE * hashCodeVal + _message.hashCode();
        hashCodeVal = CommonConstants.HASH_CODE_MULTIPLY_VALUE * hashCodeVal + (int) _userId;
        return hashCodeVal;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Alert)) {
            return false;
        }
        
        Alert casted = (Alert) obj;
        return casted._message.equals(_message) && casted._userId == _userId;
    }
    
}
