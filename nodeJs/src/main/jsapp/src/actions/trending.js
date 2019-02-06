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

import * as appActions from './app';

const URL = require('url').URL;

const GET_TRENDING_PULSE_SUBSCRIPTIONS_URL = new URL([urls.controllerUrl(), 'pulse/getTrendingPulseSubscriptions'].join(''));

export default function getTrendingPulseSubscriptions() {
  return (dispatch) => {
    fetchHelper.GET_JSON(GET_TRENDING_PULSE_SUBSCRIPTIONS_URL)
      .then((json) => {
        console.debug('gotTrendingPulseSubscriptions', json);

        // when making subsequent rest calls for Pulse, create PulseId from the long values
        const trending = new Map();
        Object.keys(json).forEach((id) => {
          trending.set(id, json[id]);
        });

        dispatch({
          type: types.TRENDING_PULSE_UPDATED,
          payload: { trendingPulse: trending },
        });
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
      });
  };
}
