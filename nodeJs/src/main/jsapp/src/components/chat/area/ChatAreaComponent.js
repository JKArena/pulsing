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

require('./ChatArea.scss');

import {render, findDOMNode} from 'react-dom';
import {Popover, OverlayTrigger, ButtonGroup} from 'react-bootstrap';
import React, {Component} from 'react';
import DropDownButtonComponent from '../../common/dropDownButton/DropDownButtonComponent';
import WebSockets from '../../../common/WebSockets';
import {TOPICS, API} from '../../../common/PubSub';
import Storage from '../../../common/Storage';
import Url from '../../../common/Url';

import GetChatLobbyMessagesAction from '../actions/GetChatLobbyMessagesAction';

const SYSTEM_MESSAGE_CHAT_TYPE = 'SYSTEM_MESSAGE';
const CHAT_OTHER = 'chat-other';
const CHAT_SELF = 'chat-self';

const CHAT_ACTIONS = (
  <Popover title='Actions'>
    <ButtonGroup vertical>
      <DropDownButtonComponent title='Invite to ChatLobby'></DropDownButtonComponent>
    </ButtonGroup>
  </Popover>
);

const Chat = (props) => {
  let isSelf = props.isSelf;
  let clazz = isSelf ? CHAT_SELF : CHAT_OTHER;

  let chat = props.chat;
  let msgViews = chat.messageViews > 0 ? chat.messageViews : '';
  let dateLTS = (isSelf ? new Date() : new Date(chat.timeStamp)).toLocaleTimeString();
  let chatContent = [<div className='chat-content' key={dateLTS +'_msg'}>{chat.message}
                      <span className='chat-message-views'>{msgViews}</span></div>,
                  <div className='chat-time' key={dateLTS +'_time'} data-content={dateLTS}></div>];

  if(chat.messageViews > 0) {
    //only chat lobbies has capability of messageViews
    chatContent.push(<div className='chat-message-views'>{chat.messageViews}</div>);
  }

  chatContent.push(<div className='chat-time' key={dateLTS +'_time'} data-content={dateLTS}></div>);
  if(isSelf) {
    chatContent.reverse();
  }

  return (
    <div className={clazz}>

      {(() => {
        if(!isSelf) {
          
          let pPath = Url.getPicturePath(chat.picturePath);

          return <OverlayTrigger trigger={['hover', 'focus']} placement='bottom' overlay={CHAT_ACTIONS}>
            <figure>
              <img className='chat-img' src={pPath} alt={chat.name}></img>
              <figcaption>{chat.name}</figcaption>
            </figure>
          </OverlayTrigger>;
        }
      })()}

      {chatContent}
      
    </div>
  );
};

const SystemMessage = (props) => {
  let msg = props.msg;
  
  return (
    <div className='chat-system-message'>
      <div className='chat-smessage-content'>
        {msg}
      </div>
    </div>
  );
};

class ChatAreaComponent extends Component {

  constructor(props) {
    super(props);

    this.chatHandler = this.onChat.bind(this);
    this.id = props.id;
    this.subscription = props.subscription;
    this.isChatLobby = props.isChatLobby;
    this.eagerConnect = !!props.eagerConnect;

    this.chatNotificationHandler = this.onChatNotification.bind(this);
  }
  
  componentDidMount() {
    this.chatAreaNode = findDOMNode(this.refs.chatArea);

    if(this.eagerConnect) {
      this.connectWS();
    }

    API.subscribe(TOPICS.CHAT_AREA, this.chatNotificationHandler);
  }
  
  componentWillUnmount() {
    if(this.ws) {
      this.ws.destroy();
      this.ws = null;
    }

    API.unsubscribe(TOPICS.CHAT_AREA, this.chatNotificationHandler);
  }

  connectWS() {
    if(!this.ws) {
      this.ws = new WebSockets('socket');
      this.ws.connect()
        .then(frame => {
          console.debug('chat area frame', this.subscription, frame);
          this.sub = this.ws.subscribe(this.subscription, this.chatHandler);
        });

      if(this.isChatLobby) {
        //need to fetch previous chat messages
        let splitted = this.subscription.split('/');
        GetChatLobbyMessagesAction.queryChatLobbyMessages(splitted[splitted.length-1], Storage.user.id, (+new Date()))
          .then((chatMessages) => {
            chatMessages.reverse();

            chatMessages.forEach(message => {
              this.addChat(message);
            });
          });
      }
    }
  }

  onChatNotification(data) {
    console.debug('chat notification ', data);
    if(this.id !== data.id) return;

    let action = data.action;
    if(action === 'chatConnect' && this.subscription) {
      this.connectWS();
    } else if(action === 'systemMessage') {
      
      this.addSystemMessage(data.message);
    }

  }

  onChat(mChat) {
    console.debug('onChat', mChat);

    if(mChat && mChat.body) {
      let chat = JSON.parse(mChat.body);

      if(chat.type === SYSTEM_MESSAGE_CHAT_TYPE) {
        this.addSystemMessage(chat.message);
      } else {
        this.addChat(chat);
        this.processChatData(chat);
      }
    }
  }

  addSystemMessage(message) {
    let sMEle = document.createElement('div');
    this.chatAreaNode.appendChild(sMEle);

    render((<SystemMessage msg={message}></SystemMessage>), sMEle);
  }

  addChat(chat) {

    let user = Storage.user;
    let isSelf = chat.userId === user.id.id;
    let cEle = document.createElement('div');

    this.chatAreaNode.appendChild(cEle);
    
    render((<Chat isSelf={isSelf} chat={chat}></Chat>), cEle);
  }

  /*
   * This function is to process any data that is sent for specific actions
   * (i.e. /chatLobbyInvite)
   */
  processChatData(chat) {
    if(chat.type === 'CHAT_LOBBY_INVITE') {
      Storage.chatLobbyInvitation = chat.data;
    }
  }
  
  render() {
    
    return (
        <div className='chatarea-component' ref='chatArea'>
        </div>
    );
  }
}

ChatAreaComponent.displayName = 'ChatAreaComponent';

export default ChatAreaComponent;
