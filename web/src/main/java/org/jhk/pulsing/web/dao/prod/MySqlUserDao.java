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
package org.jhk.pulsing.web.dao.prod;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.jhk.pulsing.db.mysql.model.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.web.service.prod.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class MySqlUserDao {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(UserService.class);
    
    private static EntityManagerFactory _emFactory;
    
    /*
     * EntityManager/Session is a single-threaded non-shared object that represents a particular unit of work with the database.
     */
    private EntityManager _eManager;
    
    public Session getSession() {
        return Session.class.cast(_eManager.getDelegate());
    }
    
    public User getUser(UserId userId) {
        _LOGGER.debug("MySqlUserDao.getUser" + userId);
        
        User user = User.class.cast( _eManager.getReference(User.class, userId.getId()) );
        
        return null;
    }
    
    @PostConstruct
    public void init() {
        _LOGGER.debug("MySqlUserDao.init");
        
        if(_emFactory == null) {
            _emFactory = Persistence.createEntityManagerFactory("jpa");
        }
        
        _eManager = _emFactory.createEntityManager();
    }

    @PreDestroy
    public void destroy() {
        _LOGGER.debug("MySqlUserDao.destroy");
        
        if(_emFactory != null) {
            _emFactory.close();
        }
    }
    
}
