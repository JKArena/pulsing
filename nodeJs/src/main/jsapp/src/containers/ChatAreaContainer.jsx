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
import { connect } from 'react-redux';

import WebSocket from '../common/webSocket';

import User from '../avro/User';
import ChatAreaComponent from '../components/chat/ChatAreaComponent';

class ChatAreaContainer extends Component {

  componentDidMount() {
    const user = this.props.user;
    if (user) {
      this.ws = new WebSocket('socket');
      this.ws.connect()
        .then((frame) => {
          console.debug('chat frame', frame);
          this.sub = this.ws.subscribe(this.subscription, this.chatHandler);
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
    return (<ChatAreaComponent
      user={props.user}
      isChatLobby={props.isChatLobby}
      chatMessages={props.chatMessages}
      onChat={props.onChat}
    />);
  }
}

export function mapStateToProps(state) {
  return {
    user: state.auth.user,
  };
}

export function mapDispatchToProps(dispatch) {
  return {
    onChat: (value, chatInfo) => {
    },
  };
}

ChatAreaContainer.propTypes = {
  user: React.PropTypes.objectOf(User).isRequired,
  subscription: React.PropTypes.string.isRequired,
  isChatLobby: React.PropTypes.boolean.isRequired,
  chatMessages: React.PropTypes.array.isRequired,
  onChat: React.PropTypes.func.isRequired,
};

export default connect(mapStateToProps, mapDispatchToProps)(ChatAreaContainer);
