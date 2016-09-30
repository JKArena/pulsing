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
package org.jhk.pulsing.shared.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Created a separate one, since might increase in size due to keys
 * 
 * @author Ji Kim
 */
public final class RedisConstants {
    
    public static final String REDIS_HOST;
    public static final int REDIS_PORT;
    public static final String REDIS_PASSWORD;
    
    public static final int CACHE_EXPIRE_DAY;
    
    public enum REDIS_KEY {
        PULSE_TRENDING_SUBSCRIBE_, PULSE_, PULSE_GEO_, PULSE_SUBSCRIBE_USERID_SET_,
        USER_PICTURE_PATH_;
    }
    
    static {
        
        Properties props = new Properties();
        
        try {
            props.load(RedisConstants.class.getResourceAsStream("redis.properties"));
            
            REDIS_HOST = props.getProperty("host");
            REDIS_PORT = Integer.parseInt(props.getProperty("port"));
            REDIS_PASSWORD = props.getProperty("password");
            CACHE_EXPIRE_DAY = Integer.parseInt(props.getProperty("cache_expire_day"));
        } catch (IOException ioExcept) {
            throw new RuntimeException("Error while reading redis.properties", ioExcept);
        }
    }
    
    private RedisConstants() {
        super();
    }
    
}
