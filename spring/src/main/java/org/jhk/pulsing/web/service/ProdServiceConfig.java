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

import org.jhk.pulsing.web.dao.prod.db.redis.RedisPulseDao;
import org.jhk.pulsing.web.dao.prod.db.redis.RedisUserDao;
import org.jhk.pulsing.web.service.prod.InvitationService;
import org.jhk.pulsing.client.chat.internal.ChatService;
import org.jhk.pulsing.client.chat.IChatService;
import org.jhk.pulsing.client.pulse.IPulseService;
import org.jhk.pulsing.client.pulse.internal.PulseService;
import org.jhk.pulsing.client.user.IUserService;
import org.jhk.pulsing.client.user.internal.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
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
@PropertySource({"classpath:application.properties"})
public class ProdServiceConfig implements IServiceConfig {
    
    @Bean
    @Override
    public IInvitationService getInvitationService() {
        return new InvitationService();
    }
    
    @Bean
    @Override
    public IChatService getChatService() {
        return new ChatService();
    }
    
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
    
    @Bean(name="redisPulseDao")
    public RedisPulseDao getRedisPulseDao() {
        return new RedisPulseDao();
    }
    
    @Bean(name="redisUserDao")
    public RedisUserDao getRedisUserDao() {
        return new RedisUserDao();
    }
    
}
