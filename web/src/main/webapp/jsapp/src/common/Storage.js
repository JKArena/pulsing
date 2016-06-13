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

/**
 * @author Ji Kim
 */
'use strict';

import User from '../avro/User';

const _PULSING_USER_KEY = 'pulsingUser';
const _MAPPER = new Map();

export default Object.freeze(
    Object.create(null,
      {
        'user' : {
          get: function() {
            if(_MAPPER.has(_PULSING_USER_KEY)) {
              return _MAPPER.get(_PULSING_USER_KEY);
            }
            
            let userStr = sessionStorage.getItem(_PULSING_USER_KEY);
            let user = null;
            
            if(userStr !== null) {
              user = JSON.parse(sessionStorage.getItem(_PULSING_USER_KEY));
              _MAPPER.set(_PULSING_USER_KEY, User.deserialize(user));
            }
            
            return user;
          },
          set: function(user) {
            if(!user) {
              sessionStorage.removeItem(_PULSING_USER_KEY);
              _MAPPER.delete(_PULSING_USER_KEY);
              return;
            }
            
            _MAPPER.set(_PULSING_USER_KEY, User.deserialize(user));
            sessionStorage.setItem(_PULSING_USER_KEY, JSON.stringify(user));
          },
          enumerable: true
        }
      }
    )
);
