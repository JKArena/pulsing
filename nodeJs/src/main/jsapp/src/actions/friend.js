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

const FRIEND_JOIN_URL = new URL([urls.djangoRootUrl(), 'friend/friendJoin/'].join(''));
const FRIEND_REQUEST_URL = new URL([urls.djangoRootUrl(), 'friend/friendRequest/'].join(''));

const GET_FRIENDS_PATH = [urls.djangoRootUrl(), 'friend/queryFriends/'].join('');
const UN_FRIEND_PATH = [urls.djangoRootUrl(), 'friend/unfriend/'].join('');

export function friendJoin(invitationId, userId) {
  return (dispatch) => {
    
    const fData = new FormData();
    fData.append('userId', userId);
    fData.append('invitationId', invitationId);

    fetchHelper.POST_JSON(FRIEND_JOIN_URL, { body: fData }, false)
      .then((result) => {
        console.debug('friend joined', result);

        if (result.code === 'SUCCESS') {
          dispatch({
            type: types.FRIEND_JOINED,
            payload: { result.data },
          });
        }
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
      });
  };
}

export function friendRequest(userId, friendId) {
  return (dispatch) => {
    
    const fData = new FormData();
    fData.append('userId', userId);
    fData.append('friendId', friendId);

    fetchHelper.POST_JSON(FRIEND_REQUEST_URL, { body: fData }, false)
      .then((result) => {
        console.debug('friend joined', result);

        if (result.code === 'SUCCESS') {
          dispatch({
            type: types.FRIEND_REQUEST,
            payload: { result.data },
          });
        }
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
      });
  };
}

export function getFriends(userId) {
  return (dispatch, getState) => {
    const url = new URL([GET_FRIENDS_PATH, userId.id].join(''));
    const params = {__proto__: null,
                    paging: getState().friend.paging[userId.id] || ''};
    
    fetchHelper.GET_JSON(url, {}, params)
      .then((result) => {
        console.debug('get friends', result);

        if (result.code === 'SUCCESS') {
          dispatch({
            type: types.GET_FRIENDS,
            payload: { result.data },
          });
        }
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
      });
  };
}

export function unfriend(userId, friendId) {
  return (dispatch) => {
    const url = new URL([UN_FRIEND_PATH, userId.id, '/', friendId.id].join(''));

    fetchHelper.DELETE_JSON(url)
      .then((result) => {
        console.debug('unfriend', result);

        if (result.code === 'SUCCESS') {
          dispatch({
            type: types.UN_FRIEND,
            payload: { result.data },
          });
        }
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
      });
  };
}
