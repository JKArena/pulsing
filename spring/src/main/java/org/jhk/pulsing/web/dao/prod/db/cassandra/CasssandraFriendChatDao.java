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

import java.util.Map;

import org.jhk.pulsing.db.cassandra.friend.FriendTable;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.web.dao.prod.db.AbstractCassandraDao;
import org.springframework.stereotype.Repository;

/**
 * @author Ji Kim
 */
@Repository
public class CasssandraFriendChatDao extends AbstractCassandraDao {
    
    private static final int _DEFAULT_FRIEND_LISTING = 20;
    
    private FriendTable _friendTable;
    
    public void friend(UserId fId, String fName, UserId sId, String sName, long timeStamp) {
        _friendTable.friend(fId, fName, sId, sName, timeStamp);
    }
    
    public void unfriend(UserId fId, String fName, UserId sId, String sName, long timeStamp) {
        _friendTable.unfriend(fId, fName, sId, sName, timeStamp);
    }
    
    public Map<Long, String> queryFriends(UserId userId) {
        
        return _friendTable.queryFriends(userId, _DEFAULT_FRIEND_LISTING);
    }
    
    public boolean areFriends(UserId userId, UserId friendId) {
        
        return _friendTable.areFriends(userId, friendId);
    }
    
    @Override
    public void init() {
        super.init();
        
        _friendTable = new FriendTable(getSession(), getKeySpace());
    }
    
    @Override
    public void destroy() {
        
        _friendTable.destroy();
        
        super.destroy();
    }

    @Override
    protected String getKeySpace() {
        return CommonConstants.CASSANDRA_KEYSPACE.FRIEND.toString();
    }

}
