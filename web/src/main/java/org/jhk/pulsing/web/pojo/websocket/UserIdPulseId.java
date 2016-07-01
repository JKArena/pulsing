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
package org.jhk.pulsing.web.pojo.websocket;

/**
 * @author Ji Kim
 */
public final class UserIdPulseId {
    
    private long _userId;
    private long _pulseId;
    
    public long getUserId() {
        return _userId;
    }
    public void setUserId(long userId) {
        _userId = userId;
    }
    public long getPulseId() {
        return _pulseId;
    }
    public void setPulseId(long pulseId) {
        _pulseId = pulseId;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ userId => ");
        builder.append(_userId);
        builder.append(", pulseId => ");
        builder.append(_pulseId);
        builder.append(" }");
        return builder.toString();
    }
    
}
