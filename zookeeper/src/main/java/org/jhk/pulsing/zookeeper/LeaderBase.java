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
package org.jhk.pulsing.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public abstract class LeaderBase extends AbstractBase {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(LeaderBase.class);
    
    private final String path;
    private final byte[] data;
    
    private boolean leader;
    
    private final StringCallback LEADER_RUN_CALLBACK = new StringCallback() {
        @Override
        public void processResult(int responseCode, String path, Object context, String name) {
            switch (Code.get(responseCode)) {
            case CONNECTIONLOSS:
                /*
                 * when client becomes disconnected from a ZooKepper server. Network error, such as network partition,
                 * or the failure of a ZooKeeper server. Unknown to the client whether the request was lost before 
                 * the ZooKeeper servers processed it or if they processed it but the client did not receive the response.
                 * 
                 * ZooKeeper client will reestablish the connection for future request, but must figure out whether a pending 
                 * request has been processed or whether it should reissue the request.
                 */
                checkIsLeader();
                break;
            case OK:
                _LOGGER.error("Is leader for path {}.", path);
                leader = true;
                break;
            case NODEEXISTS:
                _LOGGER.error("Is NOT leader for path {} and monitor leader.", path);
                monitorLeader();
                leader = false;
            default:
                _LOGGER.error("Default case with code {} for path {}.", Code.get(responseCode), path);
                leader = false;
            }
        }
    };
    
    private final DataCallback LEADER_CHECK_CALLBACK = new DataCallback() {
        @Override
        public void processResult(int responseCode, String path, Object context, byte[] data, Stat stat) {
            switch (Code.get(responseCode)) {
            case CONNECTIONLOSS:
                checkIsLeader();
                break;
            case NONODE:
                runForLeader();
                break;
            default:
                _LOGGER.error("Error code {} in leader check with path {}.", Code.get(responseCode), path);
                break;
            }
        }
    };
    
    private final Watcher MONITOR_LEADER_WATCHER = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            switch(event.getType()) {
            case NodeDeleted:
                _LOGGER.info("Original leader was deleted, will run for leader");
                runForLeader();
                break;
            default:
                break;
            }
        }
    };
    
    private final StatCallback MONITOR_LEADER_CALLBACK = new StatCallback() {
        @Override
        public void processResult(int responseCode, String path, Object context, Stat stat) {
            switch(Code.get(responseCode)) {
            case CONNECTIONLOSS:
                monitorLeader();
                break;
            default:
                break;
            }
        }
    };
    
    public LeaderBase(String path, byte[] data) {
        super();
        
        this.path = path;
        this.data = data;
    }
    
    public LeaderBase(String path, byte[] data, String hostPort, int sessionTimeout) {
        super(hostPort, sessionTimeout);
        
        this.path = path;
        this.data = data;
    }
    
    public boolean isLeader() {
        return leader;
    }

    public void runForLeader() {
        zookeeper.create(path,  data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, LEADER_RUN_CALLBACK, getStat());
    }
    
    private void monitorLeader() {
        zookeeper.exists(path, MONITOR_LEADER_WATCHER, MONITOR_LEADER_CALLBACK, getStat());
    }
    
    private void checkIsLeader() {
        zookeeper.getData(path, false, LEADER_CHECK_CALLBACK, getStat());
    }
    
    protected abstract Stat getStat();
    
}
