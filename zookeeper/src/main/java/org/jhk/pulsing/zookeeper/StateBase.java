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
import org.apache.zookeeper.data.Stat;

import java.util.Arrays;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public abstract class StateBase extends AbstractBase {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(StateBase.class);
    
    private final StringCallback INITIAL_SETUP_CALLBACK = new StringCallback() {
        @Override
        public void processResult(int responseCode, String path, Object context, String name) {
            switch (Code.get(responseCode)) {
            case CONNECTIONLOSS:
                initialSetup();
                break;
            case OK:
                _LOGGER.error("Setup properly for path {} with state {}.", path, context);
                break;
            case NODEEXISTS:
                break;
            default:
                _LOGGER.error("Error code {} in creating bootstrap with path {}.", Code.get(responseCode), path);
            }
        }
    };
    
    private final StatCallback STATE_UPDATE_CALLBACK = new StatCallback() {
        @Override
        public void processResult(int responseCode, String path, Object context, Stat stat) {
            switch(Code.get(responseCode)) {
            case CONNECTIONLOSS:
                updateState((byte[]) context);
                break;
            default:
                break;
            }
        }
    };
    
    private final String path;
    private byte[] data;
    
    public StateBase(String path, byte[] data) {
        super();
        
        this.path = path;
        this.data = data;
    }
    
    public StateBase(String path, byte[] data, String hostPort, int sessionTimeout) {
        super(hostPort, sessionTimeout);
        
        this.path = path;
        this.data = data;
    }
    
    public void initialSetup() {
        zookeeper.create(path,  data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, INITIAL_SETUP_CALLBACK, data);
    }
    
    private synchronized void updateState(byte[] data) {
        /*
         * Since using asynchronous request with retry on connection loss, things may get out of order. So before making a request 
         * update, ensure that one is requeuing the current status; otherwise abort.
         */
        if (Arrays.equals(this.data, data)) {
            //unconditional update with version of -1.
            zookeeper.setData(path,  data, -1, STATE_UPDATE_CALLBACK, data);            
        }
    }
    
    public void setState(byte[] data) {
        this.data = data;
    }
    
}
