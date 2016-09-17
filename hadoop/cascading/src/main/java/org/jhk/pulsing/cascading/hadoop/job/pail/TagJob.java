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
import org.jhk.pulsing.cascading.cascalog.function.EmitDataUnitFieldFunction;
import org.jhk.pulsing.cascading.cascalog.function.EmitDataUnitFieldFunction.EMIT_DATA_UNIT_FIELD;
import org.jhk.pulsing.pail.common.PailTapUtil;
import org.jhk.pulsing.serialization.thrift.data.DataUnit;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.shared.util.HadoopConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backtype.cascading.tap.PailTap;
import com.ifesdjeen.cascading.cassandra.CassandraTap;
import com.ifesdjeen.cascading.cassandra.cql3.CassandraCQL3Scheme;
import com.twitter.maple.tap.StdoutTap;

import jcascalog.Api;
import jcascalog.Subquery;

/**
 * @author Ji Kim
 */
public final class TagJob {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(TagJob.class);
    
    public static void main(String[] args) {
        _LOGGER.info("TagJob " + Arrays.toString(args));
        
        try {
            Configuration config = new Configuration();
            String fsDefault = config.get(HadoopConstants.CONFIG_FS_DEFAULT_KEY);
            
            PailTap masterTagEdges = PailTapUtil.attributetap(fsDefault + HadoopConstants.PAIL_MASTER_WORKSPACE, 
                    DataUnit._Fields.TAG);
            
            PailTap masterTagProperty = PailTapUtil.attributetap(fsDefault + HadoopConstants.PAIL_MASTER_WORKSPACE, 
                    DataUnit._Fields.TAG_PROPERTY);
            
            Map<String, Object> settings = new HashMap<>();
            settings.put("db.port", "9042");
            settings.put("db.keyspace", CommonConstants.CASSANDRA_KEYSPACE.TAG.toString());
            settings.put("db.columnFamily", "tags");
            settings.put("db.host", CommonConstants.CASSANDRA_CONTACT_POINT);
            
            Map<String, String> types = new HashMap<>();
            types.put("userId", "LongType");
            types.put("tags", "UTF8Type");
            
            settings.put("types", types);
            settings.put("mappings.source", Arrays.asList("userId", "tags"));
            
            CassandraCQL3Scheme scheme = new CassandraCQL3Scheme(settings);
            CassandraTap tap = new CassandraTap(scheme);
            
            Api.execute(new StdoutTap(), 
                        new Subquery("?tag", "?userId", "?coordinates")
                        .predicate(masterTagEdges, "_", "?tagEdges")
                        .predicate(new EmitDataUnitFieldFunction(EMIT_DATA_UNIT_FIELD.TAG_EDGE), "?tagEdges").out("?tag", "?userId")
                        .predicate(masterTagProperty, "?tagProperties")
                        .predicate(new EmitDataUnitFieldFunction(EMIT_DATA_UNIT_FIELD.TAG_PROPERTY), "?tagProperties").out("?tag", "?coordinates")
                        );
            
        } catch (Exception exception) {
            _LOGGER.error("Crud something went wrong!!!!!!!!!!");
            exception.printStackTrace();
        }
    }
    
}
