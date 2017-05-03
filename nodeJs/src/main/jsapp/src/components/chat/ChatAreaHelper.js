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

import React from 'react';
import {Popover, Table} from 'react-bootstrap';

import chatLobbyHandler from './chatHandler/ChatLobbyHandler';
import friendHandler from './chatHandler/FriendHandler';

const CHAT_ACTION_HELP = (
  <div className='chat-action-help'>
    <Popover title='Chat Actions' id='chatActionHelp'>
      <Table responsive>
        <thead>
          <tr>
            <th>Command</th>
            <th>Description</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>/createChatLobby chatLobbyName</td>
            <td>Creates a chat lobby of chatLobbyName</td>
          </tr>
          <tr>
            <td>/chatLobbyInvite userId chatLobbyName</td>
            <td>Invites userId to chatLobbyName</td>
          </tr>
          <tr>
            <td>/chatLobbyJoin chatLobbyName</td>
            <td>Joins chatLobbyName</td>
          </tr>
          <tr>
            <td>/chatLobbyLeave chatLobbyName</td>
            <td>Leave chatLobbyName</td>
          </tr>
        </tbody>
      </Table>
    </Popover>
  </div>
);

//types for the Chat message, so to be handled appropriately from the client+server side
const CHAT_TYPE = {
  __proto__: null,
  'PULSE': 'PULSE',
  'CHAT_LOBBY': 'CHAT_LOBBY',
  'CHAT_LOBBY_INVITE': 'CHAT_LOBBY_INVITE',
  'FRIEND_REQUEST': 'FRIEND_REQUEST',
  'GENERAL': 'GENERAL',
  'WHISPER': 'WHISPER'
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
  }
}

export { handleChatAction, CHAT_ACTION_HELP, CHAT_TYPE };
