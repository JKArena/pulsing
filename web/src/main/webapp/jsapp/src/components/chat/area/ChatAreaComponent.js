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
import {Popover, OverlayTrigger, ButtonGroup, MenuItem} from 'react-bootstrap';
import React, {Component} from 'react';
import DropDownButtonComponent from '../../../dropDownButton/DropDownButtonComponent';
import WebSockets from '../../../common/WebSockets';
import {TOPICS, API} from '../../../common/PubSub';
import Storage from '../../../common/Storage';
import Url from '../../../common/Url';

import GetChatLobbyMessagesAction from '../actions/GetChatLobbyMessagesAction';

const CHAT_OTHER = 'chat-other';
const CHAT_SELF = 'chat-self';

const chatActions = (
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
  let dateLTS = (isSelf ? new Date() : new Date(chat.timeStamp*1000)).toLocaleTimeString();

  let chatContent = [<div className='chat-content' key={dateLTS +'_msg'}>{chat.message}</div>,
                  <div className='chat-time' key={dateLTS +'_time'} data-content={dateLTS}></div>];
  if(isSelf) {
    chatContent.reverse();
  }

  return (
    <div className={clazz}>

      {(() => {
        if(!isSelf) {
          
          let pPath = Url.getPicturePath(chat.picturePath);

          return <OverlayTrigger trigger={['hover', 'focus']} placement='bottom' overlay={chatActions}>
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

    this.firstChat = null;

    this.chatNotificationHandler = this.onChatNotification.bind(this);
  }
  
  componentDidMount() {
    this.chatAreaNode = findDOMNode(this.refs.chatArea);

    API.subscribe(TOPICS.CHAT_AREA, this.chatNotificationHandler);
  }
  
  componentWillUnmount() {
    if(this.ws) {
      this.ws.destroy();
      this.ws = null;
    }

    API.unsubscribe(TOPICS.CHAT_AREA, this.chatNotificationHandler);
  }

  onChatNotification(data) {
    console.debug('chat notification ', data);

    let action = data.action;
    let id = data.id;

    if(action === 'chatConnect' && this.subscription && this.id === id) {

      if(!this.ws) {
        this.ws = new WebSockets('socket');
        this.ws.connect()
          .then(frame => {
            console.debug('chat area frame', this.subscription, frame);
            this.sub = this.ws.subscribe(this.subscription, this.chatHandler);
          });

        if(this.isChatLobby) {
          //divide by 1000 since held as seconds on the server side
          GetChatLobbyMessagesAction.queryChatLobbyMessages(this.subscription, (+new Date())/1000)
            .then((chatMessages) => {
              chatMessages.reverse();

              chatMessages.forEach(message => {

                this.addChat(message, true);
              });
            });
        }
      }
    } else if(action === 'systemMessage') {

      let sMEle = document.createElement('div');
      this.chatAreaNode.appendChild(sMEle);

      render((<SystemMessage msg={data.message}></SystemMessage>), sMEle);
    }

  }

  onChat(mChat) {
    console.debug('onChat', mChat);

    if(mChat && mChat.body) {
      let chat = JSON.parse(mChat.body);
      
      this.addChat(chat);
    }
  }

  addChat(chat, insertBefore=false) {

    let user = Storage.user;
    let isSelf = chat.userId === user.id.id;
    let cEle = document.createElement('div');

    if(!this.firstChat) {
      //before the request for previous messages is finished, chat
      //could have been done so preserve the firstChat to insertBefore it.
      //could have just done firstChild on the container, but then will need
      //to query it so just hold onto it
      this.firstChat = cEle;
    }

    if(insertBefore && this.firstChat) {
      this.chatAreaNode.insertBefore(cEle, this.firstChat);
    } else {
      this.chatAreaNode.appendChild(cEle);
    }
    
    render((<Chat isSelf={isSelf} chat={chat}></Chat>), cEle);
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
