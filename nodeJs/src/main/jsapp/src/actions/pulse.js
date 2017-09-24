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

import * as types from '../common/eventTypes';
import fetchHelper from '../common/fetchHelper';
import urls from '../common/urls';

import Pulse from '../avro/Pulse';
import PulseId from '../avro/PulseId';

import * as appActions from './app';

const CREATE_PULSE_URL = new URL([urls.controllerUrl(), 'pulse/createPulse'].join(''));
const SUBSCRIBE_PULSE_PATH = [urls.controllerUrl(), 'pulse/subscribePulse/'].join('');
const UN_SUBSCRIBE_PULSE_PATH = [urls.controllerUrl(), 'pulse/unSubscribePulse/'].join('');

export function createPulse(btnId, formId, tags) {
  return (dispatch) => {
    const btn = document.getElementById(btnId);
    btn.setAttribute('disabled', 'disabled');

    const tagsArray = [];
    tags.forEach((val) => {
      tagsArray.push(val);
    });

    const fData = new FormData();
    const pulse = new Pulse();
    const user = Storage.user;
    pulse.formMap(document.getElementById(formId));
    pulse.userId = user.id.raw;
    pulse.tags = tagsArray;
    pulse.lat = user.lat;
    pulse.lng = user.lng;

    fData.append('pulse', pulse.serialize());

    fetchHelper.POST_JSON(CREATE_PULSE_URL, { body: fData }, false)
      .then((result) => {
        console.debug('create pulse', result);

        if (result.code === 'SUCCESS') {
          const pulseResult = Pulse.deserialize(JSON.parse(result.data));

          dispatch({
            type: types.PULSE_CREATED,
            payload: { subscribedPulseId: pulseResult.id.raw },
          });
        }

        btn.removeAttribute('disabled');
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
      });
  };
}

export function subscribePulse() {
  return (dispatch, getState) => {
    const pulseId = getState().pulse.id;
    const userId = getState().user.id;
    const url = new URL([SUBSCRIBE_PULSE_PATH, pulseId.serialize(), '/', userId.serialize()].join(''));

    fetchHelper.PUT_JSON(url)
      .then((result) => {
        console.debug('subscribePulse', result);

        if (result.code === 'SUCCESS') {
          const subscribedPulseId = PulseId.deserialize(JSON.parse(result.data));

          dispatch({
            type: types.PULSE_SUBSCRIBED,
            payload: { subscribedPulseId },
          });
        }
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
      });
  };
}

export function unsubscribePulse() {
  return (dispatch, getState) => {
    const pulseId = getState().pulse.id;
    const userId = getState().user.id;
    const url = new URL([UN_SUBSCRIBE_PULSE_PATH, pulseId.serialize(), '/', userId.serialize()].join(''));

    fetchHelper.PUT_JSON(url)
      .then((result) => {
        console.debug('unSubscribePulse', result);

        if (result.code === 'SUCCESS') {
          dispatch({
            type: types.PULSE_UNSUBSCRIBED,
            payload: { subscribedPulseId: null },
          });
        }
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
      });
  };
}
