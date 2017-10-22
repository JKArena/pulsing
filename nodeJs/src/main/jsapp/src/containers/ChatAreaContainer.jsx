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

import React, { Component, PropTypes } from 'react';
import { connect } from 'react-redux';

import * as chatTypes from '../common/chatTypes';
import * as types from '../common/eventTypes';
import WebSocket from '../common/webSocket';

import User from '../avro/User';
import ChatAreaComponent from '../components/chat/ChatAreaComponent';

class ChatAreaContainer extends Component {

  constructor() {
    super();

    this.chatMessageHandler = this.onChatMessage.bind(this);
  }

  componentDidMount() {
    const user = this.props.user;
    if (user) {
      this.ws = new WebSocket('socket');
      this.ws.connect()
        .then((frame) => {
          console.debug('chat frame', frame);
          this.sub = this.ws.subscribe(this.subscription, this.chatMessageHandler);
        });
    }
  }

  componentWillUnmount() {
    this.recycleWS();
  }

  onChatMessage(messageChat) {
    console.debug('onChat', messageChat);

    if (messageChat && messageChat.body) {
      const chat = JSON.parse(messageChat.body);
      this.props.onWebSocketChat(this.props.lobbyId, chat);
    }
  }

  recycleWS() {
    if (this.ws) {
      this.ws.destroy();
      this.ws = null;
    }
  }

  render() {
    const props = this.props;
    return (<ChatAreaComponent
      user={props.user}
      chatMessages={props.lobbyMessages}
    />);
  }
}

export function mapStateToProps(state) {
  return {
    user: state.auth.user,
    lobbyMessages: state.chat.lobbyMessages[this.props.lobbyId],
  };
}

export function mapDispatchToProps(dispatch) {
  return {
    onWebSocketChat: (lobbyId, chat) => {
      if (chat.type === chatTypes.SYSTEM_MESSAGE) {
        dispatch({
          type: types.SYSTEM_MESSAGE_UPDATE,
          payload: { message: chat.message },
        });
      } else {
        dispatch({
          type: types.CHAT_LOBBY_MESSAGE_UPDATE,
          payload: { lobbyId, chat },
        });
      }
    },
  };
}

ChatAreaContainer.propTypes = {
  user: PropTypes.objectOf(User).isRequired,
  subscription: PropTypes.string.isRequired,
  lobbyId: PropTypes.string.isRequired,
  lobbyMessages: PropTypes.arrayOf(PropTypes.object).isRequired,
  onWebSocketChat: PropTypes.func.isRequired,
};

export default connect(mapStateToProps, mapDispatchToProps)(ChatAreaContainer);
