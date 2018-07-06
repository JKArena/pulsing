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
package org.jhk.pulsing.db.cassandra.chat;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jhk.pulsing.shared.util.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ProtocolOptions.Compression;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;

/**
 * Create a session per keyspace
 * 
 * @author Ji Kim
 */
public abstract class AbstractCassandraDao {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(AbstractCassandraDao.class);
    private static final Set<String> _INIT_LOGGER = new HashSet<>(); 
    
    private Session _session;
    
    protected abstract String getKeySpace();
    
    protected Session getSession() {
        return _session;
    }
    
    /**
     * The RoundRobinPolicy allocates requests across the nodes in the cluster in a repeating pattern to spread
     * the processing load. The DCAwareRoundRobinPolicy is similar, but focuses its query plans on nodes in the local 
     * data center. This policy can add a configurable number of nodes in remote data centers to query plans, but the 
     * remote nodes will always come after local nodes in priority. The local data center can be identified explicitly 
     * or you can allow the driver to discover it automatically.
     * 
     * A second mode is token awareness, which uses the token value of the partition key in order to select a node which 
     * is replica for the desired data, thus minimizing the number of nodes that must be queries. This is implemented by 
     * wrapping the selected policy with a TokenAwarePolicy.
     */
    @PostConstruct
    public void init() {
        
        QueryOptions qOpts = new QueryOptions();
        qOpts.setConsistencyLevel(ConsistencyLevel.QUORUM);
        
        Cluster cluster = Cluster.builder()
                .addContactPoint(CommonConstants.CASSANDRA_CONTACT_POINT)
                .withProtocolVersion(ProtocolVersion.V4) //if not provided, uses the version supported by the first node it connects to
                .withCompression(Compression.LZ4)
                .withLoadBalancingPolicy(new TokenAwarePolicy(new DCAwareRoundRobinPolicy.Builder().build()))
                .withQueryOptions(qOpts)
                //.withAuthProvider(authProvider)
                .build();
        
        cluster.init();
        
        _session = cluster.connect(getKeySpace());
        
        Metadata mData = cluster.getMetadata();
        
        String cName = mData.getClusterName();
        
        if(!_INIT_LOGGER.contains(cName)) {
            _INIT_LOGGER.add(cName);
            
            _LOGGER.debug(getClass().getName() + " - Connected to " + cName);
            
            for(Host host : mData.getAllHosts()) {
                _LOGGER.debug("Data Center : {}, Rack : {}, Host : {}", host.getDatacenter(), host.getRack(), host.getAddress());
            }
        }
    }
    
    @PreDestroy
    public void destroy() {
        
        if(_session != null && !_session.isClosed()) {
            _session.close();
        }
        
    }
    
}
