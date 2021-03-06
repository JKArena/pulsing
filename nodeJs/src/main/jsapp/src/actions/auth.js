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

import User from '../avro/User';

import * as chatActions from './chat';
import * as trendingActions from './trending';
import * as appActions from './app';

const LOGIN_URL = new URL([urls.controllerUrl(), 'user/validateUser'].join(''));
const CREATE_USER_URL = new URL([urls.controllerUrl(), 'user/createUser'].join(''));
const LOGOUT_PATH = [urls.controllerUrl(), 'user/logout/'].join('');

function onLoggedInActions(dispatch) {
  dispatch(chatActions.getChatLobbies());
  dispatch(trendingActions.getTrendingPulseSubscriptions());
}

export function logIn(btnId, formId) {
  return (dispatch, getState) => {
    console.debug('state', getState());
    const btn = document.getElementById(btnId);
    const fData = new FormData(document.getElementById(formId));

    btn.setAttribute('disabled', 'disabled');

    fetchHelper.POST_JSON(LOGIN_URL, { body: fData }, false)
      .then((result) => {
        console.debug('loginUser', result);

        if (result.code === 'SUCCESS') {
          const user = User.deserialize(JSON.parse(result.data), dispatch);

          dispatch({
            type: types.USER_LOGGED_IN,
            payload: { user },
          });
        }
        onLoggedInActions(dispatch);
        btn.removeAttribute('disabled');
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
        btn.removeAttribute('disabled');
      });
  };
}

export function logOut() {
  return (dispatch, getState) => {
    console.debug('state', getState());
    const url = new URL(LOGOUT_PATH + getState().auth.user.id.serialize());

    fetchHelper.DELETE_JSON(url)
      .then((result) => {
        console.debug('logoutUser', result);

        if (result.code === 'SUCCESS') {
          dispatch({
            type: types.USER_LOGGED_OUT,
            payload: { user: null },
          });
        }
      });
  };
}

export function createUser(btnId, formId, pictureId) {
  return (dispatch, getState) => {
    console.debug('state', getState());
    const btn = document.getElementById(btnId);
    btn.setAttribute('disabled', 'disabled');

    const fData = new FormData();
    const picture = document.getElementById(pictureId).file;

    if (picture) {
      fData.append('picture', picture);
    }

    const cUser = new User();
    cUser.formMap(document.getElementById(formId));
    fData.append('user', cUser.serialize());

    fetchHelper.POST_JSON(CREATE_USER_URL, { body: fData }, false)
      .then((result) => {
        console.debug('createUser', result);

        if (result.code === 'SUCCESS') {
          const user = User.deserialize(JSON.parse(result.data), dispatch);

          dispatch({
            type: types.USER_CREATED,
            payload: { user },
          });
          onLoggedInActions(dispatch);
          btn.removeAttribute('disabled');
        }
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
        btn.removeAttribute('disabled');
      });
  };
}
