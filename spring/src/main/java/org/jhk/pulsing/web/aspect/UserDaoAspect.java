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
package org.jhk.pulsing.web.aspect;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import javax.inject.Inject;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.jhk.pulsing.serialization.avro.records.Picture;
import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.web.common.Result;
import org.jhk.pulsing.web.pojo.light.UserLight;
import org.jhk.pulsing.web.service.IUserService;

import static org.jhk.pulsing.web.common.Result.CODE.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Ji Kim
 */
@Component
@Aspect
public class UserDaoAspect {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(UserDaoAspect.class);
    private static final String _RESOURCE_PREFIX = "/resources/img/";
    
    @Inject
    private WebApplicationContext applicationContext;
    
    @Inject
    private IUserService userService;
    
    @AfterReturning(pointcut="execution(org.jhk.pulsing.web.common.Result+ org.jhk.pulsing.web.service.prod.UserService.*(..))", returning= "result")
    public void patchUser(JoinPoint joinPoint, Result<User> result) {
        if(result.getCode() != SUCCESS) {
            return;
        }
        
        User user = result.getData();
        UserId userId = user.getId();
        user.setPassword(""); //blank out password
        Picture picture = user.getPicture();
        
        _LOGGER.debug("UserDaoAspect.setPictureUrl" + user);
        
        Optional<UserLight> uLight = Optional.empty();
        
        if(picture != null && picture.getName() != null) {
            
            uLight = userService.getUserLight(userId.getId());
            
            if(!uLight.isPresent()) {
            
                ByteBuffer pBuffer = picture.getContent();
                String path = applicationContext.getServletContext().getRealPath("/resources/img");
                File parent = Paths.get(path).toFile();
                if(!parent.exists()) {
                    parent.mkdirs();
                }
                
                String pFileName = user.getId().getId() + "_" + picture.getName();
                File pFile = Paths.get(path, pFileName).toFile();
                
                if(!pFile.exists()) {
                    try(OutputStream fStream = Files.newOutputStream(pFile.toPath(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                        fStream.write(pBuffer.array());
                    }catch(IOException iException) {
                        iException.printStackTrace();
                        pFile = null;
                    }
                }
                
                if(pFile != null) {
                    String pPath = _RESOURCE_PREFIX + pFile.getName();
                    
                    _LOGGER.debug("UserDaoAspect Setting picture url - " + pPath);
                    picture.setUrl(pPath);
                }
            }else {
                picture.setUrl(uLight.get().getPicturePath());
            }
            
        }
        
        if(!uLight.isPresent()) {
            userService.storeUserLight(new UserLight(user));
        }
        
    }
    
}
