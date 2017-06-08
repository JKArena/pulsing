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

import chatLobbyHandler from './chatHandler/ChatLobbyHandler';
import friendHandler from './chatHandler/FriendHandler';
import secretHandler from './chatHandler/SecretHandler';

//types for the Chat message, so to be handled appropriately from the client+server side
const CHAT_TYPE = {
  __proto__: null,
  'PULSE': 'PULSE',
  'CHAT_LOBBY': 'CHAT_LOBBY',
  'CHAT_LOBBY_INVITE': 'CHAT_LOBBY_INVITE',
  'FRIEND_REQUEST': 'FRIEND_REQUEST',
  'GENERAL': 'GENERAL',
  'WHISPER': 'WHISPER',
  'SECRET_MESSAGE': 'SECRET_MESSAGE'
};

/**
 * Must be invoked with bind
 */
function handleChatAction(user) {
  const split = this.chatInputNode.value.split(' ');
  const handler = split[0].toLowerCase();

  if(handler.indexOf('chat') > -1) {

    chatLobbyHandler.call(this, split, user);
  } else if(handler.indexOf('friend') > -1) {
    
    friendHandler.call(this, split, user);
  } else if(handler.indexOf('secret') > -1) {

    secretHandler.call(this, split, user);
  }
}

export { handleChatAction, CHAT_TYPE };
