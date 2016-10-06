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
import PulseId from '../avro/PulseId';

const _PULSING_USER_KEY = 'pulsingUser';
const _PULSING_SUBSCRIBED_PULSE_ID_KEY = 'pulsingSubscribedPulseId';
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
              user = User.deserialize(JSON.parse(userStr));
              _MAPPER.set(_PULSING_USER_KEY, user);
            }
            
            return user;
          },
          set: function(userJson) {
            if(!userJson) {
              sessionStorage.removeItem(_PULSING_USER_KEY);
              _MAPPER.delete(_PULSING_USER_KEY);
              return;
            }
            
            let user = User.deserialize(userJson);
            
            _MAPPER.set(_PULSING_USER_KEY, user);
            sessionStorage.setItem(_PULSING_USER_KEY, user.serialize());
          },
          enumerable: true
        },

        'subscribedPulseId' : {
          get: function() {
            if(_MAPPER.has(_PULSING_SUBSCRIBED_PULSE_ID_KEY)) {
              return _MAPPER.get(_PULSING_SUBSCRIBED_PULSE_ID_KEY);
            } else {
              return null;
            }
          },
          set: function(pulseIdJson) {
            if(!pulseIdJson) {
              _MAPPER.delete(_PULSING_SUBSCRIBED_PULSE_ID_KEY);
              return;
            }
            
            let pulseId = PulseId.deserialize(pulseIdJson);
            
            _MAPPER.set(_PULSING_SUBSCRIBED_PULSE_ID_KEY, pulseId);
          },
          enumerable: true
        }
      }
    )
);
