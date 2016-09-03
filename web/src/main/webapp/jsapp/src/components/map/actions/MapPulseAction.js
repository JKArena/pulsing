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
import Pulse from '../../../avro/Pulse';

const GET_MAP_PULSE_DATA_POINTS_PATH = 'pulse/getMapPulseDataPoints';

const MapPulseAction = Object.freeze(
  {
    __proto__: null,

    getMapPulseDataPoints(latLng) {

      let fData = new FormData();
      fData.append('lat', latLng.lat);
      fData.append('lng', latLng.lng);

      return new Promise(function(resolve, reject) {

        Fetch.GET_JSON(GET_MAP_PULSE_DATA_POINTS_PATH, {body: fData})
          .then(function(json) {
            console.debug('getMapPulseDataPoints', json);
            
            //the fetch's json response handler only takes care of array/root
            //position, so need to parse each entry separately (odd)
            let mpDataPoints = [];
            json.forEach(entry => {
              mpDataPoints.push(Pulse.deserialize(JSON.parse(entry)));
            });

            resolve(mpDataPoints);
          })
          .catch(function(err) {
            console.error(err);

            reject(err);
          });

      });
    }
  }
);

export default MapPulseAction;
