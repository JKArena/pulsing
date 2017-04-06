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
package org.jhk.pulsing.db.cassandra.friend;

import java.util.HashMap;
import java.util.Map;

import org.jhk.pulsing.db.cassandra.ICassandraTable;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;

/**
 * @author Ji Kim
 */
public final class FriendTable implements ICassandraTable {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(FriendTable.class);
    
    private static final String _FRIEND_TABLE = "FRIEND_TABLE";
    
    private final PreparedStatement _FRIEND_QUERY;
    private final PreparedStatement _FRIEND_INSERT;
    
    private final Session _session;
    private final String _keySpace;
    
    public FriendTable(Session session, String keySpace) {
        super();
        
        _session = session;
        _keySpace = keySpace;
        
        _session.execute("CREATE TABLE " + _keySpace + "." + _FRIEND_TABLE + " (" +
                "user_id bigint," +
                "name text," +
                "friend_user_id bigint," +
                "friend_name text," +
                "rank int," +
                "timestamp bigint," +
                "PRIMARY KEY (user_id, rank, friend_user_id)" + //user_id for partitioning and rank for clustering
                " )" + 
                "WITH CLUSTERING ORDER BY (rank DESC);");
        
        _FRIEND_QUERY = _session.prepare("SELECT * FROM " + _FRIEND_TABLE + " WHERE user_id=? AND rank > 0 LIMIT ?");
        _FRIEND_INSERT = _session.prepare("INSERT INTO " + _FRIEND_TABLE + " (user_id, name, friend_user_id, friend_name, rank, timestamp) VALUES (?, ?, ?, ?, ?, ?)");
    }
    
    public Map<Long, String> queryFriends(UserId userId, int limit) {
        _LOGGER.info("FriendTable.queryFriends : " + userId);
        
        ResultSet fResult = _session.execute(_FRIEND_QUERY.bind(userId, limit));
        
        _LOGGER.info("FriendTable.queryFriends fResult : " + fResult);
        Map<Long, String> friends = new HashMap<>();
        
        fResult.forEach(friendShip -> {
            
            long friendId = friendShip.getLong("friend_user_id");
            String friendName = friendShip.getString("friend_name");
            int rank = friendShip.getInt("rank");
            
            _LOGGER.info("FriendTable.queryFriends : " + friendId + "/" + friendName + " - " + rank);
            
            friends.put(friendId, friendName);
        });
        
        return friends;
    }
    
    /**
     * TODO use Spark SQL for query of whether the entry exists, since need to query all
     * 
     * @param userId
     * @param friendId
     * @return
     */
    public boolean areFriends(UserId userId, UserId friendId) {
        Map<Long, String> friends = queryFriends(userId, Integer.MAX_VALUE);
        return friends.containsKey(friendId);
    }
    
    public void friend(UserId fId, String fName, UserId sId, String sName, long timeStamp) {
        _LOGGER.info("FriendTable.friend : " + fId + ", " + fName + " - " + sId + ", " + sName);
        
        _session.executeAsync(_FRIEND_INSERT.bind(fId, fName, sId, sName, 1, timeStamp));
        _session.executeAsync(_FRIEND_INSERT.bind(sId, sName, fId, fName, 1, timeStamp));
    }
    
    public void unfriend(UserId fId, String fName, UserId sId, String sName, long timeStamp) {
        _LOGGER.info("FriendTable.unfriend : " + fId + ", " + fName + " - " + sId + ", " + sName);
        
        _session.executeAsync(_FRIEND_INSERT.bind(fId, fName, sId, sName, -1, timeStamp));
    }
    
    @Override
    public void destroy() {
        
        _session.execute(SchemaBuilder.dropTable(_FRIEND_TABLE));
    }

}
