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
import org.jhk.pulsing.web.dao.dev.PulseDao;
import org.jhk.pulsing.web.dao.dev.UserDao;
import org.jhk.pulsing.web.service.dev.PulseService;
import org.jhk.pulsing.web.service.dev.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Ji Kim
 */
@Profile("dev")
@Configuration
public class DevServiceConfig implements IServiceConfig {
    
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
        return new UserDao();
    }
    
    @Bean
    @Override
    public IPulseDao getPulseDao() {
        return new PulseDao();
    }
    
}
