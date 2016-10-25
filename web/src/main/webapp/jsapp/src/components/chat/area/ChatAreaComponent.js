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
import React, {Component} from 'react';
import WebSockets from '../../../common/WebSockets';
import Storage from '../../../common/Storage';
import Url from '../../../common/Url';

const CHAT_OTHER = 'chat-other';
const CHAT_SELF = 'chat-self';

const Chat = (props) => {
  let isSelf = props.isSelf;
  let clazz = isSelf ? CHAT_SELF : CHAT_OTHER;

  let chat = props.chat;
  let date = isSelf ? new Date() : new Date(chat.timeStamp*1000);

  let chatContent = [<div className='chat-content'>{chat.message}</div>,
                  <div className='chat-time' data-content={date.toLocaleTimeString()}></div>];
  if(isSelf) {
    chatContent.reverse();
  }

  return (
    <div className={clazz}>

      {(() => {
        if(!isSelf) {
          
          let pPath = Url.getPicturePath(chat.picturePath);

          return <figure>
            <img className='chat-img' src={pPath} alt={chat.name}></img>
            <figcaption>{chat.name}</figcaption>
          </figure>;
        }
      })()}

      {chatContent}
      
    </div>
  );
};

class ChatAreaComponent extends Component {

  constructor(props) {
    super(props);

    this.chatHandler = this.onChat.bind(this);
    this.subscription = props.subscription;
  }
  
  componentDidMount() {
    this.chatAreaNode = findDOMNode(this.refs.chatArea);

    this.ws = new WebSockets('socket');
    this.ws.connect()
      .then(frame => {
        console.debug('chat area frame', this.subscription, frame);
        this.sub = this.ws.subscribe(this.subscription, this.chatHandler);
      });
  }
  
  componentWillUnmount() {
    if(this.ws) {
      this.ws.destroy();
      this.ws = null;
    }
  }

  onChat(mChat) {
    console.debug('onChat', mChat);

    let user = Storage.user;

    if(mChat && mChat.body) {
      let chat = JSON.parse(mChat.body);
      let isSelf = chat.userId === user.id.id;
      let cEle = document.createElement('div');

      this.chatAreaNode.appendChild(cEle);

      render((<Chat isSelf={isSelf} chat={chat}></Chat>), cEle);
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
