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

import React, { Component } from 'react';

require('./ChatArea.scss');

const SYSTEM_MESSAGE_CHAT_TYPE = 'SYSTEM_MESSAGE';
const CHAT_OTHER = 'chat-other';
const CHAT_SELF = 'chat-self';

const Chat = (props) => {
  const isSelf = props.isSelf;
  const clazz = isSelf ? CHAT_SELF : CHAT_OTHER;

  const chat = props.chat;
  const msgViews = chat.messageViews > 0 ? chat.messageViews : '';
  const dateLTS = (isSelf ? new Date() : new Date(chat.timeStamp)).toLocaleTimeString();
  const chatContent = [<div className='chat-content' key={dateLTS +'_msg'}>{chat.message}
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

          return <OverlayTrigger trigger={['hover', 'focus']} placement="bottom">
            <figure>
              <img className="chat-img" src={pPath} alt={chat.name}></img>
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
  return (
    <div className="chat-system-message">
      <div className="chat-smessage-content">
        {props.msg}
      </div>
    </div>
  );
};

class ChatAreaComponent extends Component {

}

ChatAreaComponent.propTypes = {
  user: React.PropTypes.objectOf(User).isRequired,
  isChatLobby: React.PropTypes.boolean.isRequired,
  chatMessages: React.PropTypes.array.isRequired,
  onChat: React.PropTypes.func.isRequired,
};
