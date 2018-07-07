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

import java.io.IOException;
import java.util.Map;

import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public abstract class AbstractBase implements Watcher {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(AbstractBase.class);
    
    protected ZooKeeper zookeeper;
    
    private final StringCallback bootstrapCallback = new StringCallback() {
        @Override
        public void processResult(int responseCode, String path, Object context, String name) {
            switch(Code.get(responseCode)) {
            case CONNECTIONLOSS:
                createBootstrapPath(path, (byte[]) context);
                break;
            case OK:
                _LOGGER.info("Bootstrap path {} was created with context {}.", path, context);
                break;
            case NODEEXISTS:
                _LOGGER.info("Bootstrap path {} already exists.", path);
                break;
            default:
                _LOGGER.error("Error code {} in creating bootstrap with path {}.", Code.get(responseCode), path);
            }
        }
    };
    
    private final String hostPort;
    private final int sessionTimeout;
    
    public AbstractBase() {
        hostPort = CommonConstants.ZOOKEEPER_HOST_PORT;
        sessionTimeout = CommonConstants.ZOOKEEPER_SESSION_TIMEOUT;
    }
    
    public AbstractBase(String hostPort, int sessionTimeout) {
        this.hostPort = hostPort;
        this.sessionTimeout = sessionTimeout;
    }
    
    /**
     * Assume all calls are made in proper order (i.e. in similar way hasNext() is invoked prior to next())
     * @throws IOException
     */
    protected void startZooKeeper() throws IOException {
        zookeeper = new ZooKeeper(hostPort, sessionTimeout, this);
    }
    
    protected void bootstrap(Map<String, byte[]> persistentMetaData) {
        persistentMetaData.entrySet().stream().forEach(entry -> {
            createBootstrapPath(entry.getKey(), entry.getValue());
        });
    }
    
    private void createBootstrapPath(String path, byte[] data) {
        zookeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, bootstrapCallback, data);
    }
    
}
