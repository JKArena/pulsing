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
import Pulse from '../../../avro/Pulse';
import {render} from 'react-dom';
import MapPulseAction from '../actions/MapPulseAction';

const _ROOT_URL = Url.rootUrl();

const InfoNode = (props) => {
  let pulse = props.pulse;
  let userLights = props.userLights;
  let desc = new Date(pulse.timeStamp*1000).toLocaleString(); //since held as seconds on server
  let subscribed = [];

  userLights.forEach(uLight => {
    let pPath = uLight.picturePath ? (_ROOT_URL + uLight.picturePath) : Url.DEFAULT_PICTURE_PATH;

    subscribed.push(<li className='map-subscribed-entry' key={uLight.id}>
        <img src={pPath} className='map-subscribed-img'></img>
        <h3 className='map-subscribed-name'>{uLight.name}</h3>
        <p className='map-subscribed-detail'>Foobar</p>
      </li>);
  });

  return (<div className='map-info-node'>
    <h2 className='map-info-header'>{pulse.value}</h2>
    <div className='map-info-date'>
      <span className='map-info-date-text'>{desc}</span>
    </div>
    <ul className='map-subscriptions'>
      {subscribed}
    </ul>
    {(() => {
      if(props.clickHandler) {
        return <a href='#' onClick={props.clickHandler}>Subscribe</a>;
      }
    })()}
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
        console.debug('GMapPulseStore retrieved ', mpDataPoints);
        this.dataPoints = [];

        Object.keys(mpDataPoints).forEach(pulse => {

          this.addDataPoint(map, Pulse.deserialize(JSON.parse(pulse)), mpDataPoints[pulse]);
        });

        this.emitDataPoints(this.dataPoints);
      }.bind(this));
  }

  _subscribePulse(url) {
    console.debug('subscribePulse ', url);
  }

  _getInfoNode(pulse, userId, userLights) {
    let iNode = document.createElement('div');
    let isSubscribed = userLights.filter(uLight => {
      return uLight.id === userId.id.id.long;
    });

    if(isSubscribed.length > 0) {
      //no need for subscribe
      render((<InfoNode pulse={pulse} userLights={userLights} />), iNode);
    } else {
      let url = new URL(Url.SUBSCRIBE_PULSE_PATH);
      url.searchParams.append('pulseId', pulse.id.serialize());
      url.searchParams.append('userId', userId.serialize());

      render((<InfoNode pulse={pulse} userLights={userLights}
        clickHandler={this._subscribePulse.bind(this, url)} />), iNode);
    }

    return iNode;
  }

  addDataPoint(map, pulse, userLights) {
    console.debug('addDataPoint', pulse, userLights);

    let user = Storage.user;
    let marker = new global.google.maps.Marker({
      position: {lat: pulse.lat, lng: pulse.lng},
      map: map,
      title: pulse.value
    });

    let iWindow = new global.google.maps.InfoWindow({
      content: this._getInfoNode(pulse, user.id, userLights)
    });

    marker.addListener('click', function() {
      iWindow.open(map, marker);
    });

    this.dataPoints.push(marker);

  }
  
}

export default GMapPulseStore;
