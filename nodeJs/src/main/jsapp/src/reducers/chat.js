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

import * as chatTypes from '../common/chatTypes';
import * as types from '../common/eventTypes';

const SYSTEM_MESSAGE_CAP = 20;
const STATE = {
  lobbies: {}, // key lobbyName, value UUID
  lobbyMessages: {},  // key UUID, value array of Chat object
  systemMessage: [], // system messages
  paging: {}, // key UUID, value PagingState
};

export default function app(state = STATE, action) {
  switch (action.type) {
    case types.CREATE_CHAT_LOBBY: {
      return { ...state, ...action.payload };
    }
    case types.SUBSCRIBE_CHAT_LOBBY: {
      return { ...state, ...action.payload };
    }
    case types.UN_SUBSCRIBE_CHAT_LOBBY: {
      return { ...state, ...action.payload };
    }
    case types.GET_CHAT_LOBBIES: {
      return { ...state, ...action.payload };
    }
    case types.GET_CHAT_LOBBY_MESSAGES: {
      return { ...state, ...action.payload };
    }
    case types.CHAT_LOBBY_MESSAGE_UPDATE: {
      const chat = action.payload.chat;
      const type = chat.type;
      const lobbyMessages = state.lobbyMessages[action.payload.lobbyId];
      lobbyMessages.push(chat);

      if (type === chatTypes.CHAT_LOBBY_INVITE || type === chatTypes.FRIEND_REQUEST) {
        // Storage.invitation = chat.data;
      }
      return { ...state, ...lobbyMessages };
    }
    case types.SYSTEM_MESSAGE_UPDATE: {
      if (state.systemMessage.length === SYSTEM_MESSAGE_CAP) {
        state.systemMessage.splice(0, 1);
      }
      state.systemMessage.push(action.payload.message);
      const message = state.message;
      return { ...state, ...message };
    }
    default:
      return state;
  }
}
