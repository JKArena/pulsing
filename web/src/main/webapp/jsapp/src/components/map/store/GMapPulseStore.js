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

import {render} from 'react-dom';
import React from 'react';

import AbstractMapStore from './AbstractMapStore';
import Storage from '../../../common/Storage';
import SubscribePulseAction from '../../common/actions/SubscribePulseAction';
import UnSubscribePulseAction from '../../common/actions/UnSubscribePulseAction';
import {InfoNodeStateLess, SUBSCRIBE_ACTION, UN_SUBSCRIBE_ACTION} from '../info/InfoNodeStateLess';

class GMapPulseStore extends AbstractMapStore {
  
  subscribeUnSubscribe(actionParam) {
    console.debug('subscribeUnSubscribe ', arguments, actionParam);

    let userId = Storage.user.id;
    let pulseId = actionParam.pulseId;
    let Action = actionParam.type === SUBSCRIBE_ACTION ? SubscribePulseAction.subscribePulse :
      UnSubscribePulseAction.unSubscribePulse;

    Action(pulseId, userId)
      .then(() => {
        console.debug('success in subscribeUnSubscribe', actionParam.type,
          pulseId, userId);

        this.fetchDataPoints(this.map, this.prevLatLng);
      })
      .catch(() => {
        console.error('error in subscribeUnSubscribe', actionParam.type, pulseId, userId);
      });

  }

  getInfoNode(pulse, userId, userLights) {
    let iNode = document.createElement('div');
    let isSubscribed = userLights.filter(uLight => {
      return uLight.id === userId.id;
    });
    
    let type = isSubscribed.length > 0 ? UN_SUBSCRIBE_ACTION : SUBSCRIBE_ACTION;
    let actionParam = {pulseId: pulse.id, type: type};

    render((<InfoNodeStateLess pulse={pulse} userLights={userLights} actionType={type}
        clickHandler={this.subscribeUnSubscribe.bind(this, actionParam)} />), iNode);

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
      content: this.getInfoNode(pulse, user.id, userLights)
    });

    marker.addListener('click', function() {
      iWindow.open(map, marker);
    });

    this.dataPoints.push(marker);
  }

  removeDataPoint(index) {
    this.dataPoints[index].setMap(null);
    this.dataPoints[index] = null;
  }
  
}

export default GMapPulseStore;
