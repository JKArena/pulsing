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

import Storage from '../../../common/Storage';
import {TOPICS, API} from '../../../common/PubSub';

import CreateChatLobbyAction from '../actions/CreateChatLobbyAction';
import ChatLobbySubscribeAction from '../actions/ChatLobbySubscribeAction';
import ChatLobbyUnSubscribeAction from '../actions/ChatLobbyUnSubscribeAction';

export default function (split, user) {
  if(split[0] === '/createChatLobby' && split.length === 2) {

    let cLName = split[1];
    CreateChatLobbyAction.createChatLobby(user.id, cLName)
      .then((chatId) => {

        this.mountChatAreaComponent(chatId, cLName);
        API.publish(TOPICS.CHAT_AREA, {action: 'systemMessage', id: this.state.chatId,
                      message: 'Chat Lobby : ' + cLName + ' created successfully!'});
      });
  } else if(split[0] === '/chatLobbyInvite' && split.length > 2) {

    //ChatArea's popover will allow easy access for id, but for /chatInvite perhaps store in Redis
    //the mapping of userId => {name: userId} from the chatLobbyMessages + friends (TODO)
    let uId = split[1]; //temp for now, assume knows the uId
    let cInfo = this.getChatLobbyInfo(split[2]);

    if(cInfo) {
      let cMessage = `Chat Lobby Invite from: ${user.name}. Type /chatLobbyJoin ${cInfo.text}`;

      this.ws.send('/pulsing/privateChat/' + uId, {},
              JSON.stringify({message: cMessage, userId: user.id.id, type: CHAT_TYPE.CHAT_LOBBY_INVITE,
                              data: {chatName: cInfo.text, chatId: cInfo.eventKey}, name: user.name}));
    }

  } else if(split[0] === '/chatLobbyJoin' && split.length === 2) {

    //will be an Array of chatName, chatId, and invitationId (maybe Map later)
    let chatLobby = Storage.invitation.filter(entry => {
      //if different invitation then entry.chatName would be undefined so ok
      return entry.chatName === split[1];
    });

    if(chatLobby.length === 1) {

      ChatLobbySubscribeAction.chatLobbySubscribe(chatLobby[0], Storage.user.id)
        .then(() => {
          this.mountChatAreaComponent(chatLobby[0].chatId, chatLobby[0].chatName);
        });
    }
  } else if(split[0] === '/chatLobbyLeave' && split.length === 2) {

    let cLName = split[1];
    let cInfo = this.getChatLobbyInfo(cLName);

    if(cInfo) {
      let chatId = cInfo.eventKey;

      ChatLobbyUnSubscribeAction.chatLobbyUnSubscribe(chatId, cLName, Storage.user.id)
        .then(() => {

          this.unmountChatAreaComponent(chatId);
          API.publish(TOPICS.CHAT_AREA, {action: 'systemMessage', id: this.state.chatId,
                      message: 'Chat Lobby : Left ' + cLName + '...'});
        });
    }
  }
};
