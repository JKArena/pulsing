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
package org.jhk.pulsing.storm.topologies;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class UserTopologyRunner {
    
    private static final Logger _LOG = LoggerFactory.getLogger(UserTopologyRunner.class);
    
    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {
        
        if(args == null || args.length == 0) {
            _LOG.debug("UserTopologyRunner: running local");
            runLocalCluster();
        }else {
            _LOG.debug("UserTopologyRunner: running remote");
            runRemoteCluster();
        }
        
    }
    
    private static void runLocalCluster() {
        
        Config config = new Config();
        config.setDebug(true);
        
        LocalCluster cluster = new LocalCluster();
        
        cluster.submitTopology("user-topology", config, UserTopologyBuilder.build());
        
    }
    
    private static void runRemoteCluster() throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {
        
        Config config = new Config();
        config.setNumWorkers(1);
        config.setMessageTimeoutSecs(300);
        StormSubmitter.submitTopology("user-topology", config, UserTopologyBuilder.build());
        
    }
    
}
