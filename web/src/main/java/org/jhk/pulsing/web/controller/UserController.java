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
package org.jhk.pulsing.web.controller;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.web.common.Result;
import org.jhk.pulsing.web.dao.IUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Ji Kim
 */
@Controller
@RequestMapping("/user")
public final class UserController extends AbstractController {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private IUserDao userDao;
    
    @RequestMapping(value="/createUser", method=RequestMethod.POST, consumes={MediaType.MULTIPART_FORM_DATA_VALUE})
    public @ResponseBody Result<User> createUser(@RequestParam User user, @RequestParam(name="picture", required=false) MultipartFile picture) {
        _LOGGER.info("createUser: " + user + "; " + (picture != null ? ("picture size is: " + picture.getSize()) : "picture not submitted"));
        
        if(picture != null) {
            try {
                user.setPicture(ByteBuffer.wrap(picture.getBytes()));
            }catch(IOException iException) {
                _LOGGER.error("Could not get picture bytes", iException);
            }
        }
        
        return userDao.createUser(user);
    }
    
    @RequestMapping(value="/getUser", method=RequestMethod.GET)
    public @ResponseBody Result<User> getUser(UserId userId) {
        _LOGGER.info("getUser: " + userId);
        
        return userDao.getUser(userId);
    }
    
    @RequestMapping(value="/validateUser", method=RequestMethod.POST, consumes={MediaType.MULTIPART_FORM_DATA_VALUE})
    public @ResponseBody Result<User> validateUser(@RequestParam("email") String email, @RequestParam("password") String password) {
        _LOGGER.info("validateUser: " + email + ", " + password);
        
        return userDao.validateUser(email, password);
    }
    
}
