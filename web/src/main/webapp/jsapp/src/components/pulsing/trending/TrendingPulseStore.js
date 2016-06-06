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

var EventEmitter = require('events').EventEmitter;
var CHANGE_EVENT = 'change';
var _trending;

class TrendingPulseStore extends EventEmitter {
  
  emitChange() {
    this.emit(CHANGE_EVENT);
  }
  
  addChangeListener(callback) {
    this.on(CHANGE_EVENT, callback);
  }
  
  removeChangeListener(callback) {
    this.removeListener(CHANGE_EVENT, callback);
  }
  
  static get trending() {
    return _trending ? Promise.resolve(_trending) : new Promise(function(resolve, reject) {
      let request = new Request("/controller/getTrendingPulse");
      
      fetch(request, {
        method: 'GET',
        mode: 'same-origin'
      })
      .then(function(response) {
        _trending = new Map();
        
        response.forEach(pulse => {
          _trending.set(pulse.id, pulse);
        });
        
        resolve(_trending);
      })
      .catch(function(err) {
        console.error("Failure in getting trending ", err);
      });
      
    });
  }
  
}

export default TrendingPulseStore;