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

const CREATE_CHAT_LOBBY_URL = new URL([urls.controllerUrl(), 'chat/createChatLobby/'].join(''));
const GET_CHAT_LOBBIES_PATH = [urls.controllerUrl(), 'chat/queryChatLobbies/'].join('');
const GET_CHAT_LOBBY_MESSAGES_PATH = [urls.controllerUrl(), 'chat/queryChatLobbyMessages/'].join('');
const CHAT_LOBBY_SUBSCRIBE_PATH = [urls.controllerUrl(), 'chat/chatLobbySubscribe/'].join('');
const CHAT_LOBBY_UN_SUBSCRIBE_PATH = [urls.controllerUrl(), 'chat/chatLobbyUnSubscribe/'].join('');

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
            payload: { lobbies: result.data },
          });
        }
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
      });
  };
}

export function getChatLobbyMessages(chatLobbyId) {
  return (dispatch, getState) => {
    const userId = getState().auth.user.id;
    const path = [GET_CHAT_LOBBY_MESSAGES_PATH, chatLobbyId, '/', userId.serialize()].join('');
    const params = { __proto__: null, paging: getState().chat.paging[chatLobbyId] || '' };

    fetchHelper.GET_JSON(new URL(path), {}, params)
      .then((result) => {
        console.debug('getChatLobbyMessages result', result);

        if (result.code === 'SUCCESS') {
          dispatch({
            type: types.GET_CHAT_LOBBY_MESSAGES,
            payload: { lobbyMessages: { chatLobbyId: result.data.data } },
          });
        }
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
      });
  };
}

export function createChatLobby(lobbyName) {
  return (dispatch, getState) => {
    const userId = getState().auth.user.id;

    const fData = new FormData();
    fData.append('userId', userId.serialize());
    fData.append('lobbyName', lobbyName);

    fetchHelper.POST_JSON(CREATE_CHAT_LOBBY_URL, { body: fData })
      .then((result) => {
        console.debug('createChatLobby result', result);

        if (result.code === 'SUCCESS') {
          dispatch({
            type: types.CREATE_CHAT_LOBBY,
            payload: { lobbies: { [result.data]: lobbyName } },
          });
        }
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
      });
  };
}

export function subscribeChatLobby(chatLobby) {
  return (dispatch, getState) => {
    const userId = getState().auth.user.id;
    const chatId = chatLobby.chatId;
    const chatName = chatLobby.chatName;
    const path = [CHAT_LOBBY_SUBSCRIBE_PATH, chatId, '/',
                  chatName, '/', chatLobby.invitationId, '/',
                  userId.serialize()].join('');

    fetchHelper.PUT_JSON(new URL(path))
      .then((result) => {
        console.debug('subscribeChatLobby result', result);

        if (result.code === 'SUCCESS') {
          dispatch({
            type: types.SUBSCRIBE_CHAT_LOBBY,
            payload: { lobbies: { [chatId]: chatName } },
          });
        }
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
      });
  };
}

export function unSubscribeChatLobby(chatLobby) {
  return (dispatch, getState) => {
    const userId = getState().auth.user.id;
    const chatId = chatLobby.chatId;
    const chatName = chatLobby.chatName;
    const path = [CHAT_LOBBY_UN_SUBSCRIBE_PATH, chatId, '/',
                  chatName, '/', userId.serialize()].join('');

    fetchHelper.PUT_JSON(new URL(path))
      .then((result) => {
        console.debug('unSubscribeChatLobby result', result);

        if (result.code === 'SUCCESS') {
          dispatch({
            type: types.UN_SUBSCRIBE_CHAT_LOBBY,
            payload: { lobbies: { [chatId]: null } },
          });
        }
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
      });
  };
}
