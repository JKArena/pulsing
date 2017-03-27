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

import org.jhk.pulsing.shared.util.RedisConstants.INVITATION_ID;

/**
 * @author Ji Kim
 */
public class Invitation {
    
    private long _fromUserId;
    private INVITATION_ID _invitationType;
    private String _invitationId;
    private long _expiration;
    
    public Invitation() {
        super();
    }
    
    public Invitation(long fromUserId, INVITATION_ID invitationType, String invitationId, long expiration) {
        super();
        
        _fromUserId = fromUserId;
        _invitationType = invitationType;
        _invitationId = invitationId;
        _expiration = expiration;
    }
    
    public long getFromUserId() {
        return _fromUserId;
    }
    public void setFromUserId(long fromUserId) {
        _fromUserId = fromUserId;
    }
    
    public INVITATION_ID getInvitationType() {
        return _invitationType;
    }
    public void setInvitationType(INVITATION_ID invitationType) {
        _invitationType = invitationType;
    }
    
    public long getExpiration() {
        return _expiration;
    }
    public void setExpiration(long expiration) {
        _expiration = expiration;
    }
    
    public String getInvitationId() {
        return _invitationId;
    }
    public void setInvitationId(String invitationId) {
        _invitationId = invitationId;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("fromUserId: " + _fromUserId + ", ");
        builder.append("invitationType: " + _invitationType + ", ");
        builder.append("expiration: " + _expiration + ",");
        builder.append("invitationId: " + _invitationId);
        builder.append("}");
        return builder.toString();
    }
    
    @Override
    public int hashCode() {
        return _invitationId.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Invitation)) {
            return false;
        }
        
        Invitation casted = (Invitation) obj;
        return casted._invitationId.equals(_invitationId);
    }
    
}
