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
package org.jhk.pulsing.client.user.internal;

import org.jhk.pulsing.client.AbstractService;
import org.jhk.pulsing.client.payload.Result;
import org.jhk.pulsing.client.user.IUserService;
import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.shared.util.CommonConstants.SERVICE_ENV_KEY;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * @author Ji Kim
 */
@Service
public class UserService extends AbstractService 
                            implements IUserService {

    @Autowired
    private Environment environment;
    
    @Override
    public Result<User> getUser(UserId userId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Result<User> createUser(User user) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Result<User> validateUser(String email, String password) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Result<String> logout(UserId userId) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getBaseUrl() {
        return environment.getProperty(SERVICE_ENV_KEY.USER_SERVICE_ENDPOINT.name()); 
    }
    
}
