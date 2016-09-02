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

const PUB_SUB = new Map();

const TOPICS = Object.freeze({
  __proto__: null,
  
  AUTH: 'AUTH',
  USER_GEO_CHANGE: 'USER_GEO_CHANGE'
});

export { TOPICS };

const API = Object.freeze(
    Object.create(null,
      {
        'subscribe' : {
          get: function() {

            return (topic, listener) => {
              console.debug('subscribing to ', topic, listener);
              
              //can't use WeakMap or WeakSet
              let listeners = PUB_SUB.get(topic);
              if(listeners === undefined) {
                listeners = new Set();
                PUB_SUB.set(topic, listeners);
              }
              
              listeners.add(listener);
            }
          },

          set: function() {},
          enumerable: true
        },
        
        'unsubscribe' : {
          get: function() {

            return (topic, listener) => {
              PUB_SUB.get(topic).delete(listener);
            }
          },

          set: function() {},
          enumerable: true
        },
        
        'publish' : {
          get: function() {

            return (topic, message=Object.create(null)) => {
              console.debug('publishing ', topic, message);
              
              if(!PUB_SUB.has(topic)) {
                return;
              }
              
              PUB_SUB.get(topic).forEach(listener => { listener(message); });
            }
          },

          set: function() {},
          enumerable: true
        }
      }
    )
);

export { API };
