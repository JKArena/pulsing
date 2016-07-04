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

import org.jhk.pulsing.web.dao.IPulseDao;
import org.jhk.pulsing.web.dao.IUserDao;
import org.jhk.pulsing.web.dao.prod.PulseDao;
import org.jhk.pulsing.web.dao.prod.RedisUserDao;
import org.jhk.pulsing.web.service.prod.PulseService;
import org.jhk.pulsing.web.service.prod.UserService;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

/**
 * Services will always check redis cache first which will 
 * store the data as avro json and if not existing will fetch 
 * from mySql or etc and store it in redis by constructing the Avro 
 * type so to only work w/ avro type from client <-> server <-> storm
 * 
 * @author Ji Kim
 */
@Profile("prod")
@EnableAspectJAutoProxy
@ComponentScan({"org.jhk.pulsing.web.aspect"})
@EntityScan({"org.jhk.pulsing.db.mysql.model"})
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
        return new PulseService();
    }
    
    @Bean
    @Override
    public IUserDao getUserDao() {
        return new RedisUserDao();
    }
    
    @Bean
    @Override
    public IPulseDao getPulseDao() {
        return new PulseDao();
    }
    
}
