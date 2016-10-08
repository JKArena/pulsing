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

const DATA_POINTS_EVENT = 'dataPoints';

class AbstractMapStore extends EventEmitter {
  
  emitDataPoints(dataPoints) {
    this.emit(DATA_POINTS_EVENT, dataPoints);
  }
  
  addDataPointsListener(callback) {
    this.on(DATA_POINTS_EVENT, callback);
  }
  
  removeDataPointsListener(callback) {
    this.removeListener(DATA_POINTS_EVENT, callback);
  }
  
  fetchDataPoints(map, latLng) {
    throw new Error('AbstractMapStore should not be used standalone ' + latLng);
  }

  addDataPoint(map, data) {
    throw new Error('AbstractMapStore should not be used standalone ' + data);
  }

  removeDataPoint(index) {
    throw new Error('AbstractMapStore should not be used standalone ' + index);
  }

  clearDataPoints() {
    this.dataPoints.forEach((val, index) => {
      this.removeDataPoint(index);
    });
    
    this.dataPoints.length = 0;
  }
  
}

export default AbstractMapStore;
