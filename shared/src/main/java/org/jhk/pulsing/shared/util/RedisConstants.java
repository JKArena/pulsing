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

/**
 * Created a separate one, since might increase in size due to keys
 * 
 * @author Ji Kim
 */
public final class RedisConstants {
    
    private RedisConstants() {
        super();
    }
    
    public static final String REDIS_HOST = "localhost";
    public static final int REDIS_PORT = 6379;
    public static final String REDIS_PASSWORD = "wsad";
    
    public static final int DEFAULT_CACHE_EXPIRE_SECONDS = 86400;
    
    public enum REDIS_KEY {
        TRENDING_PULSE_, USER_;
    }
    
}
