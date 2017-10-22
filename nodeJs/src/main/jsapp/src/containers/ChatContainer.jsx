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

import WebSocket from '../common/webSocket';

import User from '../avro/User';
import ChatComponent from '../components/chat/ChatComponent';

class ChatContainer extends Component {

  componentDidMount() {
    const user = this.props.user;
    if (user) {
      this.ws = new WebSocket('socket');
      this.ws.connect()
        .then((frame) => {
          console.debug('chat frame', frame);
        });
    }
  }

  componentWillUnmount() {
    this.recycleWS();
  }

  recycleWS() {
    if (this.ws) {
      this.ws.destroy();
      this.ws = null;
    }
  }

  render() {
    const props = this.props;
    return (<ChatComponent
      user={props.user}
      subscribedPulseId={props.subscribedPulseId}
      lobbies={props.lobbies}
      lobbyMessages={props.lobbyMessages}
      paging={props.paging}
    />);
  }
}

export function mapStateToProps(state) {
  return {
    user: state.auth.user,
    subscribedPulseId: state.pulse.subscribedPulseId,
    lobbies: state.chat.lobbies,
    lobbyMessages: state.chat.lobbyMessages,
    paging: state.chat.paging,
  };
}

export function mapDispatchToProps(dispatch) {
  return {
    onChat: (value, chatInfo) => {
      console.debug('onChat', value, chatInfo, this);
      console.debug('dispatch', dispatch);
      if (value[0] === '/') {
        // means an action
        // this.handleChatActionHandler(user);
      } else {
        // usual chat, need to send the type (i.e. for chatLobby need to log the message)
        // this.ws.send('/pulsing/chat/' + chatInfo.id, {},
        // JSON.stringify({ message: value, type: chatInfo.type,
        // userId: user.id.id, name: user.name }));
      }
    },
  };
}

ChatContainer.propTypes = {
  user: PropTypes.objectOf(User).isRequired,
  subscribedPulseId: PropTypes.number.isRequired,
  lobbies: PropTypes.objectOf(PropTypes.object).isRequired,
  lobbyMessages: PropTypes.objectOf(PropTypes.object).isRequired,
  paging: PropTypes.objectOf(PropTypes.object).isRequired,
  onChat: PropTypes.func.isRequired,
};

export default connect(mapStateToProps, mapDispatchToProps)(ChatContainer);
