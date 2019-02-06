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

import Location from '../../../../avro/Location';
import * as appActions from './app';

const URL = require('url').URL;

const CREATE_LOCATION_URL = new URL([urls.djangoRootUrl(), 'location/add/'].join(''));

export default function createLocation(btnId, formId, tags) {
  return (dispatch, getState) => {
    const btn = document.getElementById(btnId);
    btn.setAttribute('disabled', 'disabled');

    const tagsArray = [];
    tags.forEach((val) => {
      tagsArray.push(val);
    });

    const fData = new FormData();
    const loc = new Location();
    const user = getState().auth.user;
    loc.formMap(document.getElementById(formId));
    loc.userId = user.id.raw;
    loc.tags = tagsArray;

    fData.append('location', loc.serialize());

    fetchHelper.POST_JSON(CREATE_LOCATION_URL, { body: fData }, false)
      .then((result) => {
        console.debug('create location', result);

        if (result.code === 'SUCCESS') {
          dispatch({
            type: types.LOCATION_CREATED,
          });
        }

        btn.removeAttribute('disabled');
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
      });
  };
}
