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
package org.jhk.pulsing.web.service;

import java.sql.SQLException;

import javax.inject.Named;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jhk.pulsing.web.dao.IPulseDao;
import org.jhk.pulsing.web.dao.IUserDao;
import org.jhk.pulsing.web.dao.prod.PulseDao;
import org.jhk.pulsing.web.dao.prod.UserDao;
import org.jhk.pulsing.web.dao.prod.db.MySqlUserDao;
import org.jhk.pulsing.web.dao.prod.db.RedisUserDao;
import org.jhk.pulsing.web.service.prod.PulseService;
import org.jhk.pulsing.web.service.prod.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Services will always check redis cache first which will 
 * store the data as avro json and if not existing will fetch 
 * from mySql or etc and store it in redis by constructing the Avro 
 * type so to only work w/ avro type from client <-> server <-> storm
 * 
 * @author Ji Kim
 */
@Profile("prod")
@EnableTransactionManagement
@Configuration
public class ProdServiceConfig implements IServiceConfig {
    
    @Bean
    @Override
    public IUserService getUserService() {
        return new UserService();
    }
    
    @Bean
    @Override
    public IPulseService getPulseService() {
        return new org.jhk.pulsing.web.service.dev.PulseService();
    }
    
    @Bean
    @Override
    public IUserDao getUserDao() {
        return new UserDao();
    }
    
    @Bean
    public RedisUserDao getRedisUserDao() {
        return new RedisUserDao();
    }
    
    @Bean
    public MySqlUserDao getMySqlUserDao() {
        return new MySqlUserDao();
    }
    
    @Bean
    @Override
    public IPulseDao getPulseDao() {
        return new org.jhk.pulsing.web.dao.dev.PulseDao();
    }
    
    @Bean
    public LocalEntityManagerFactoryBean entityManagerFactory() {
        LocalEntityManagerFactoryBean emFactory = new LocalEntityManagerFactoryBean();
        emFactory.setPersistenceUnitName("pulsing");
        return emFactory;
    }
    
    @Bean
    public PlatformTransactionManager transactionManager() throws SQLException {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return txManager;
    }
    
}
