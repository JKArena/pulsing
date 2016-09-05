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

import AbstractMapStore from './AbstractMapStore';
import MapPulseAction from '../actions/MapPulseAction';

class GMapPulseStore extends AbstractMapStore {

  constructor() {
    super();
    this.dataPoints = [];
  }
  
  fetchDataPoints(map, latLng) {

    MapPulseAction.getMapPulseDataPoints(latLng)
      .then(function(mpDataPoints) {
        console.debug('MapPulseStore retrieved ', mpDataPoints);
        this.dataPoints = [];

        mpDataPoints.forEach(pulse => {
          let coordinates = pulse.coordinates;

          if(coordinates && coordinates.length === 2) {
            let marker = new global.google.maps.Marker({
              position: {lat: coordinates[0], lng: coordinates[1]},
              map: map,
              title: pulse.value
            });

            let cnt = 'Prob display approximate sub count for ' + pulse.value;
            if(pulse.timeStamp) {
              //multiply by 1000 since millisecond in client whereas seconds in server
              cnt += ' created: ' + (new Date(pulse.timeStamp*1000));
            }
            let iWindow = new global.google.maps.InfoWindow({
              content: cnt
            });

            marker.addListener('click', function() {
              iWindow.open(map, marker);
            });

            this.dataPoints.push(marker);
          }
          
        });

        this.emitDataPoints(this.dataPoints);
      }.bind(this));
  }
  
}

export default GMapPulseStore;
