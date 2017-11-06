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

import React from 'react';
import { OverlayTrigger } from 'react-bootstrap';

import * as chatTypes from '../../common/chatTypes';

import urls from '../../common/urls';
import User from '../../avro/User';

require('./ChatArea.scss');

const CHAT_OTHER = 'chat-other';
const CHAT_SELF = 'chat-self';

const Chat = (props) => {
  const isSelf = props.isSelf;
  const clazz = isSelf ? CHAT_SELF : CHAT_OTHER;

  const chat = props.chat;
  const msgViews = chat.messageViews > 0 ? chat.messageViews : '';
  const dateString = (isSelf ? new Date() : new Date(chat.timeStamp)).toLocaleTimeString();
  const chatMsgKey = [dateString, '_msg'].join('');
  const chatContentKey = [dateString, '_time'].join('');

  const chatContent = [<div className="chat-content" key={chatMsgKey}>{chat.message}
    <span className="chat-message-views">{msgViews}</span></div>,
    <div className="chat-time" key={chatContentKey} data-content={dateString} />,
  ];

  if (chat.messageViews > 0) {
    // only chat lobbies has capability of messageViews
    chatContent.push(<div className="chat-message-views">{chat.messageViews}</div>);
  }

  chatContent.push(<div className="chat-time" key={chatContentKey} data-content={dateString} />);
  if (isSelf) {
    chatContent.reverse();
  }

  const triggers = ['hover', 'focus'];
  return (
    <div className={clazz}>

      {(() => {
        const pPath = urls.getPicturePath(chat.picturePath);
        const overlay = !isSelf ?
          (<OverlayTrigger trigger={triggers} placement="bottom">
            <figure>
              <img className="chat-img" src={pPath} alt={chat.name} />
              <figcaption>{chat.name}</figcaption>
            </figure>
          </OverlayTrigger>) : null;

        return overlay;
      })()}

      {chatContent}
    </div>
  );
};

Chat.propTypes = {
  isSelf: React.PropTypes.bool.isRequired,
  chat: React.PropTypes.objectOf(React.PropTypes.object).isRequired,
};

const SystemMessage = props =>
  (<div className="chat-system-message">
    <div className="chat-smessage-content">
      {props.msg}
    </div>
  </div>);

SystemMessage.propTypes = {
  msg: React.PropTypes.string.isRequired,
};

const ChatAreaComponent = (props) => {
  const userId = props.user.id.id;
  const chats = [];

  props.chatMessages.forEach((chat) => {
    if (chat.type === chatTypes.SYSTEM_MESSAGE) {
      chats.push(<SystemMessage msg={chat.message} />);
    } else {
      const isSelf = chat.userId === userId;
      chats.push(<Chat isSelf={isSelf} chat={chat} />);
    }
  });

  return (
    <div className="chatarea-component">
      {chats}
    </div>
  );
};

ChatAreaComponent.propTypes = {
  user: React.PropTypes.objectOf(User).isRequired,
  chatMessages: React.PropTypes.arrayOf(React.PropTypes.object).isRequired,
};

export default ChatAreaComponent;
