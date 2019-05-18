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
package org.jhk.pulsing.shared.util;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Ji Kim
 */
public final class CommonConstants {
    
    public enum SERVICE_ENV_KEY {
        USER_SERVICE_ENDPOINT, CHAT_SERVICE_ENDPOINT;
    }
    
    public static final String DEFAULT_BOOTSTRAP_HOST;
    public static final int DEFAULT_BOOTSTRAP_PORT;
    public static final String DEFAULT_BOOTSTRAP_URL;
    public static final int DEFAULT_STORM_TICK_TUPLE_FREQ_SECONDS;
    public static final int DEFAULT_STORM_INTERVAL_SECONDS;
    
    public static final String CASSANDRA_CONTACT_POINT;
    public static final String PROJECT_POINT;
    public static final String APP_NAME;
    public static final String MAP_API_KEY;
    public static final String SPARK_YARN_CLUSTER_MASTER;
    
    public static final String TIME_INTERVAL_ID_VALUE_DELIM = "0x07";
    public static final String TIME_INTERVAL_PERSIST_TIMESTAMP_DELIM = "0x13";
    
    public static final int HASH_CODE_INIT_VALUE = 3;
    public static final int HASH_CODE_MULTIPLY_VALUE = 31;
    
    public static final double DEFAULT_PULSE_RADIUS = 5.0d;
    
    public static final int ELASTICSEARCH_REST_PORT;
    public static final int ELASTICSEARCH_NODE_PORT;
    
    public static final String ZOOKEEPER_HOST_PORT;
    public static final int ZOOKEEPER_SESSION_TIMEOUT;
    
    public enum TOPICS {
		PULSE_SUBSCRIBE, USER_CREATE, PULSE_CREATE, LOCATION_CREATE, FRIEND;
	};
	
	public enum CASSANDRA_KEYSPACE {
	    CHAT, FRIEND, USER;
	};
	
	static {
        
        Properties props = new Properties();
        
        try {
            props.load(RedisConstants.class.getResourceAsStream("common.properties"));
            
            DEFAULT_BOOTSTRAP_HOST = props.getProperty("bootstrap_host");
            DEFAULT_BOOTSTRAP_PORT = Integer.parseInt(props.getProperty("bootstrap_port"));
            DEFAULT_BOOTSTRAP_URL = DEFAULT_BOOTSTRAP_HOST + ":" + DEFAULT_BOOTSTRAP_PORT;
            DEFAULT_STORM_TICK_TUPLE_FREQ_SECONDS = Integer.parseInt(props.getProperty("default_storm_tick_tuple_freq_seconds"));
            DEFAULT_STORM_INTERVAL_SECONDS = Integer.parseInt(props.getProperty("default_storm_interval_seconds"));
            
            PROJECT_POINT = props.getProperty("project_point");
            CASSANDRA_CONTACT_POINT = PROJECT_POINT;
            
            APP_NAME = props.getProperty("app_name");
            MAP_API_KEY = props.getProperty("map_api_key");
            SPARK_YARN_CLUSTER_MASTER = props.getProperty("spark_yarn_cluster_master");
            
            ELASTICSEARCH_REST_PORT = Integer.parseInt(props.getProperty("elasticsearch_rest_port"));
            ELASTICSEARCH_NODE_PORT = Integer.parseInt(props.getProperty("elasticsearch_node_port"));
            
            ZOOKEEPER_HOST_PORT = props.getProperty("zookeeper_host_port");
            ZOOKEEPER_SESSION_TIMEOUT = Integer.parseInt(props.getProperty("zookeeper_session_timeout"));
        } catch (IOException ioExcept) {
            throw new RuntimeException("Error while reading common.properties", ioExcept);
        }
    }
    
    private CommonConstants() {
        super();
    }
    
}
