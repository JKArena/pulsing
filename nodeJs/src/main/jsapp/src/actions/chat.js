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

const GET_CHAT_LOBBIES_PATH = [urls.controllerUrl(), 'chat/queryChatLobbies/'].join('');

export function getChatLobbies() {
  return (dispatch, getState) => {
    const userId = getState().auth.user.id;
    const url = new URL(GET_CHAT_LOBBIES_PATH + userId.serialize());

    fetchHelper.GET_JSON(url)
      .then((result) => {
        console.debug('getChatLobbies result', result);

        if (result.code === 'SUCCESS') {
          dispatch({
            type: types.GET_CHAT_LOBBIES,
            payload: { chatLobbies: result.data },
          });
        }
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
      });
  };
}
