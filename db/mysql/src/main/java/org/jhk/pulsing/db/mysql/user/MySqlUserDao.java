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
package org.jhk.pulsing.db.mysql.user;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jhk.pulsing.db.mysql.model.MUser;
import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.db.mysql.common.AvroMySqlMappers;
import org.jhk.pulsing.client.payload.Result;
import static org.jhk.pulsing.client.payload.Result.CODE.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Ji Kim
 */
@Repository
@Transactional
public class MySqlUserDao {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(MySqlUserDao.class);
    
    @Inject
    private SessionFactory sessionFactory;
    
    public Optional<User> getUser(UserId userId) {
        _LOGGER.debug("MySqlUserDao.getUser" + userId);
        
        MUser mUser = getSession().find(MUser.class, userId.getId());
        
        if(mUser != null) {
            _LOGGER.debug("User is " + mUser);
            return Optional.of(AvroMySqlMappers.mySqlToAvro(mUser));
        }else {
            return Optional.empty();
        }
    }
    
    public Result<User> createUser(User user) {
        _LOGGER.debug("MySqlUserDao.createUser" + user);
        
        MUser mUser = AvroMySqlMappers.avroToMysql(user);
        getSession().persist(mUser);
        getSession().flush();
        
        UserId userId = UserId.newBuilder().build();
        userId.setId(mUser.getId()); //use the generated id
        user.setId(userId);
        
        return new Result<>(SUCCESS, user);
    }
    
    public boolean isEmailTaken(String email) {
        _LOGGER.debug("MySqlUserDao.isEmailTaken " + email);
        
        List<?> entries = getSession().createNamedQuery("checkEmailTaken")
                .setParameter("email", email)
                .getResultList();
        
        _LOGGER.debug("Result " + entries.size() + " : " + entries);
        
        return entries == null || entries.size() > 0;
    }

    public Optional<User> validateUser(String email, String password) {
        _LOGGER.debug("MySqlUserDao.validateUser" + email + " : " + password);
        
        List<?> entries = getSession().createNamedQuery("findUser")
                .setParameter("email", email)
                .setParameter("password", password)
                .getResultList();
        
        _LOGGER.debug("Result " + entries.size() + " : " + entries);
        
        if(entries == null || entries.size() == 0) {
            return Optional.empty();
        }
        
        return Optional.of(AvroMySqlMappers.mySqlToAvro((MUser) entries.get(0)));
    }
    
    /*
     * EntityManager/Session is a single-threaded non-shared object that represents a particular unit of work with the database.
     */
    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
    
}
