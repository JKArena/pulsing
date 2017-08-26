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

import fetchHelper from '../common/fetchHelper';
import urls from '../common/urls';
import * as types from '../common/storageTypes';

const LOGIN_URL = new URL(urls.controllerUrl() + 'user/validateUser');

export function logIn(btnId, formId) {
  return (dispatch, getState) => {
    console.info('state', getState());
    const btn = document.getElementById(btnId);
    const fData = new FormData(document.getElementById(formId));

    btn.setAttribute('disabled', 'disabled');

    fetchHelper.POST_JSON(LOGIN_URL, {body: fData}, false)
      .then(function(result) {
        console.debug('loginUser', result);

        if(result.code === 'SUCCESS') {
          dispatch({
            type: types.USER_LOGGED_IN,
            payload: { user: JSON.parse(result.data) },
          });
        }

        btn.removeAttribute('disabled');
      })
      .catch(function(err) {
        btn.removeAttribute('disabled');
      });
    }
  }
}

export function logOut(btnId, formId) {
  return (dispatch, getState) => {
    console.info('state', getState());
    const btn = document.getElementById(btnId);
    const fData = new FormData(document.getElementById(formId));

    btn.setAttribute('disabled', 'disabled');

    fetchHelper.POST_JSON(LOGIN_URL, {body: fData}, false)
      .then(function(result) {
        console.debug('loginUser', result);

        if(result.code === 'SUCCESS') {
          dispatch({
            type: types.USER_LOGGED_IN,
            payload: { user: JSON.parse(result.data) },
          });
        }

        btn.removeAttribute('disabled');
      })
      .catch(function(err) {
        btn.removeAttribute('disabled');
      });
    }
  }
}

export function createUser(btnId, formId) {
  return (dispatch, getState) => {
    console.info('state', getState());
    const btn = document.getElementById(btnId);
    const fData = new FormData(document.getElementById(formId));

    btn.setAttribute('disabled', 'disabled');

    fetchHelper.POST_JSON(LOGIN_URL, {body: fData}, false)
      .then(function(result) {
        console.debug('loginUser', result);

        if(result.code === 'SUCCESS') {
          dispatch({
            type: types.USER_LOGGED_IN,
            payload: { user: JSON.parse(result.data) },
          });
        }

        btn.removeAttribute('disabled');
      })
      .catch(function(err) {
        btn.removeAttribute('disabled');
      });
    }
  }
}
