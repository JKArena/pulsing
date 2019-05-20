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
package org.jhk.pulsing.db.mysql.controller;

import static org.jhk.pulsing.client.payload.Result.CODE.FAILURE;
import static org.jhk.pulsing.client.payload.Result.CODE.SUCCESS;

import java.util.Optional;

import javax.annotation.Resource;

import org.jhk.pulsing.client.payload.Result;
import org.jhk.pulsing.db.mysql.user.MySqlUserDao;
import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Consider re-impl using https://github.com/airlift/airlift
 * 
 * Note that if transactional bean is implementing an interface, by default the proxy will be a Java Dynamic Proxy. 
 * This means that only external method calls that come in through the proxy will be intercepted any self-invocation calls 
 * will not start any transaction even if the method is annotated with @Transactional.
 * 
 * Also rollback only occur during RuntimeException so paradigm is to throw a RuntimeException when you wish for a 
 * rollback
 * 
 * @author Ji Kim
 */
@CrossOrigin(origins="*")
@Controller
@RequestMapping("/userService")
public class UserServiceController {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(UserServiceController.class);
    
    @Resource(name="mySqlUserDao")
    private MySqlUserDao mySqlUserDao;
    
    @RequestMapping(value="/createUser", method=RequestMethod.POST, consumes={MediaType.MULTIPART_FORM_DATA_VALUE})
    public Result<User> createUser(User user) {
        _LOGGER.info("Create user {}", user);
        
        if(mySqlUserDao.isEmailTaken(user.getEmail().toString())) {
            return new Result<User>(FAILURE, null, "Email is already taken " + user.getEmail());
        }
        
        Result<User> cUser = new Result<User>(FAILURE, null, "Failed in creating " + user);
        
        try {
            cUser = mySqlUserDao.createUser(user);
        } catch(RuntimeException eException) {
        }
        
        return cUser;
    }
    
    @RequestMapping(value="/validateUser", method=RequestMethod.POST, consumes={MediaType.MULTIPART_FORM_DATA_VALUE})
    public @ResponseBody Result<User> validateUser(@RequestParam String email, @RequestParam String password) {
        _LOGGER.debug("validateUser {}, {}", email, password);
        
        Optional<User> user = mySqlUserDao.validateUser(email, password);
        
        return user.isPresent() ? new Result<>(SUCCESS, user.get()) : new Result<>(FAILURE, null, "Failed in validating " + email + " : " + password);
    }
    
    @RequestMapping(value="/getUser", method=RequestMethod.GET)
    public @ResponseBody Result<User> getUser(@RequestParam UserId userId) {
        _LOGGER.debug("getUser {}", userId);
        
        Result<User> result = new Result<>(FAILURE, null, "Unable to find " + userId);
        
        Optional<User> user = mySqlUserDao.getUser(userId);
        if(user.isPresent()) {
            result = new Result<>(SUCCESS, user.get());
        }
        
        return result;
    }

}
