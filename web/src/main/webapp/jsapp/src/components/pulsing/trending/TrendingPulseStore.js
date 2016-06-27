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

import {EventEmitter} from 'events';

import TrendingPulseAction from './actions/TrendingPulseAction';

const FETCHED_EVENT = 'fetched';

class TrendingPulseStore extends EventEmitter {
  
  emitFetched(trending) {
    this.emit(FETCHED_EVENT, trending);
  }
  
  addFetchedListener(callback) {
    this.on(FETCHED_EVENT, callback);
  }
  
  removeFetchedListener(callback) {
    this.removeListener(FETCHED_EVENT, callback);
  }
  
  fetchTrending() {
    
    TrendingPulseAction.getTrendingPulse
      .then(function(trending) {
        
        this.emitFetched(trending);
      }.bind(this));
    
  }
  
}

export default TrendingPulseStore;