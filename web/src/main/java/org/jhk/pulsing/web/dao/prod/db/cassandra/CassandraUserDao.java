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
package org.jhk.pulsing.web.dao.prod.db.cassandra;

import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.web.dao.prod.db.AbstractCassandraDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.datastax.driver.core.schemabuilder.SchemaStatement;

/**
 * Various tables created from Hadoop batch views and queried from here
 * 
 * @author Ji Kim
 */
@Repository
public class CassandraUserDao extends AbstractCassandraDao {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(CassandraUserDao.class);
    private static final String _USER_TBD_TABLE = "USER_TBD_TABLE";
    
    /*
    @Override
    public void init() {
        super.init();
        
        //Since want to test it out and etc, create schema dynamically and destroy it after done
        SchemaStatement userSchemaStatement = SchemaBuilder.createTable(_USER_TBD_TABLE)
                .addPartitionKey("id", DataType.bigint());
        
        getSession().execute(userSchemaStatement);
    }*/
    
    @Override
    public void destroy() {
        
        getSession().execute(SchemaBuilder.dropTable(_USER_TBD_TABLE));
        
        super.destroy();
    }
    
    @Override
    protected String getKeySpace() {
        return CommonConstants.CASSANDRA_KEYSPACE.USER.toString();
    }
    
}
