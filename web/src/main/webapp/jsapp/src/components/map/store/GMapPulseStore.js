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

import React from 'react';
import AbstractMapStore from './AbstractMapStore';
import Storage from '../../../common/Storage';
import Url from '../../../common/Url';
import {render} from 'react-dom';
import MapPulseAction from '../actions/MapPulseAction';

const InfoNode = (props) => {
  return (<div className='map-info-node'>
    <h1>{props.header}</h1>
    <p>{props.desc}</p>
    <a href='#' onClick={props.clickHandler}>Subscribe</a>
  </div>);
};

class GMapPulseStore extends AbstractMapStore {

  constructor() {
    super();
    this.dataPoints = [];
  }
  
  fetchDataPoints(map, latLng) {
    this.clearDataPoints();

    MapPulseAction.getMapPulseDataPoints(latLng)
      .then(function(mpDataPoints) {
        console.debug('MapPulseStore retrieved ', mpDataPoints);
        this.dataPoints = [];

        mpDataPoints.forEach(pulse => {

          this.addDataPoint(map, pulse);
        });

        this.emitDataPoints(this.dataPoints);
      }.bind(this));
  }

  _subscribePulse(url) {
    console.debug('subscribePulse ', url);
  }

  getInfoNode(pulse, userId) {
    let iNode = document.createElement('div');
    let header = pulse.value;
    let desc = 'Created: ' + (new Date(pulse.timeStamp*1000));

    let url = new URL(Url.SUBSCRIBE_PULSE_PATH);
    url.searchParams.append('pulseId', pulse.id.serialize());
    url.searchParams.append('userId', userId.serialize());

    render((<InfoNode header={header} desc={desc} 
      clickHandler={this._subscribePulse.bind(this, url)} />), iNode);

    return iNode;
  }

  addDataPoint(map, pulse) {

    let lat = pulse.lat;
    let lng = pulse.lng;

    if(lat && lng) {
      let user = Storage.user;
      let marker = new global.google.maps.Marker({
        position: {lat: lat, lng: lng},
        map: map,
        title: pulse.value
      });
      
      let iWindow = new global.google.maps.InfoWindow({
        content: this.getInfoNode(pulse, user.id)
      });

      marker.addListener('click', function() {
        iWindow.open(map, marker);
      });

      this.dataPoints.push(marker);
    }

  }
  
}

export default GMapPulseStore;
