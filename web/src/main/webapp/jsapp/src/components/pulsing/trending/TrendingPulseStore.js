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

import Fetch from '../../../common/Fetch';
import {EventEmitter} from 'events';

let CHANGE_EVENT = 'change';
let _trending;

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

      Fetch.GET_JSON('pulse/getTrendingPulse')
        .then(function(json) {
          console.info('gotTrendingPulse', json);
          //when making subsequent rest calls for Pulse, create PulseId from the long values
          _trending = new Map();
          
          json.forEach(pulseStr => {
            let pulse = JSON.parse(pulseStr);
            
            _trending.set(pulse.id.id.long, pulse.value.string);
          });
          
          resolve(_trending);
        })
        .catch(function(err) {
          console.error(err);
          
          reject(err);
        });

    });
  }
  
}

export default TrendingPulseStore;