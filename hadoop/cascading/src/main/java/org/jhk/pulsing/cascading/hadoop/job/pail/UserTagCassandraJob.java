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
package org.jhk.pulsing.cascading.hadoop.job.pail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.shared.util.HadoopConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ifesdjeen.cascading.cassandra.CassandraTap;
import com.ifesdjeen.cascading.cassandra.cql3.CassandraCQL3Scheme;

/**
 * @author Ji Kim
 */
public final class UserTagCassandraJob {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(UserTagCassandraJob.class);
    
    public static void main(String[] args) {
        _LOGGER.info("UserTagCassandraJob " + Arrays.toString(args));
        
        try {
            Configuration config = new Configuration();
            String fsDefault = config.get(HadoopConstants.CONFIG_FS_DEFAULT_KEY);
            
            Map<String, Object> settings = new HashMap<>();
            settings.put("db.port", "9042");
            settings.put("db.keyspace", CommonConstants.CASSANDRA_KEYSPACE.USER.toString());
            settings.put("db.columnFamily", "userTags");
            settings.put("db.host", CommonConstants.CASSANDRA_CONTACT_POINT);
            
            Map<String, String> types = new HashMap<>();
            types.put("userId", "LongType");
            types.put("tags", "UTF8Type");
            
            settings.put("types", types);
            settings.put("mappings.source", Arrays.asList("userId", "tags"));
            
            CassandraCQL3Scheme scheme = new CassandraCQL3Scheme(settings);
            CassandraTap tap = new CassandraTap(scheme);
            
        } catch (Exception exception) {
            _LOGGER.error("Crud something went wrong!!!!!!!!!!");
            exception.printStackTrace();
        }
    }
    
}
