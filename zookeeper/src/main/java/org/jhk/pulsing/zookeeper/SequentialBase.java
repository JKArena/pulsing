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
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public abstract class SequentialBase extends AbstractBase {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(SequentialBase.class);
    
    /**
     * @param path sequential that one should create of, should be of "/persistentpath/path-"
     * @throws InterruptedException 
     * @throws KeeperException 
     */
    public String createSequential(String path, byte[] data, boolean persistent) throws KeeperException, InterruptedException {
        CreateMode mode = persistent ? CreateMode.PERSISTENT_SEQUENTIAL : CreateMode.EPHEMERAL_SEQUENTIAL;
        
        while (true) {
            try {
                return zookeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
            } catch(NodeExistsException nodeExists) {
                _LOGGER.warn("Already exists.", nodeExists);
            } catch(ConnectionLossException connection) {
            }
        }
    }
    
}
