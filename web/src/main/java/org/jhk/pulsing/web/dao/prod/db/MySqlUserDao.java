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
package org.jhk.pulsing.web.dao.prod.db;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.jhk.pulsing.db.mysql.model.MUser;
import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.web.common.AvroMySqlMappers;
import org.jhk.pulsing.web.service.prod.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Ji Kim
 */
@Repository
@Transactional
public class MySqlUserDao implements IUserOptionalDao {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(UserService.class);
    
    @Inject
    private EntityManagerFactory entityManagerFactory;
    
    /*
     * EntityManager/Session is a single-threaded non-shared object that represents a particular unit of work with the database.
     */
    @PersistenceContext
    private EntityManager eManager;
    
    public Optional<User> getUser(UserId userId) {
        _LOGGER.debug("MySqlUserDao.getUser" + userId);
        
        MUser user = MUser.class.cast(eManager.getReference(MUser.class, userId.getId()));
        
        _LOGGER.debug("User is " + user);
        return Optional.empty();
    }

    public Optional<User> createUser(User user) {
        _LOGGER.debug("MySqlUserDao.createUser" + user);
        
        MUser mUser = AvroMySqlMappers.avroToMysql(user);
        eManager.persist(mUser);
        eManager.flush();
        
        _LOGGER.debug("After create " + mUser);
        
        return Optional.empty();
    }

    public Optional<User> validateUser(String email, String password) {
        _LOGGER.debug("MySqlUserDao.validateUser" + email + " : " + password);
        
        List<?> entries = eManager.createNamedQuery("findUser")
                .setParameter("email", email)
                .setParameter("password", password)
                .getResultList();
        
        _LOGGER.debug("Result " + entries.size() + " : " + entries);
        
        return Optional.empty();
    }
    
    @PostConstruct
    public void init() {
        eManager = entityManagerFactory.createEntityManager();
    }
    
}
