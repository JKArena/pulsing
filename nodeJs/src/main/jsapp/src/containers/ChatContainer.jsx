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

import User from '../avro/User';
import ChatComponent from '../components/chat/ChatComponent';

class ChatContainer extends Component {
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
    onChat: (value) => {
      console.debug('onChat', value);
    },
  };
}

ChatContainer.propTypes = {
  user: React.PropTypes.objectOf(User).isRequired,
  subscribedPulseId: React.PropTypes.number.isRequired,
  lobbies: React.PropTypes.object.isRequired,
  lobbyMessages: React.PropTypes.object.isRequired,
  paging: React.PropTypes.object.isRequired,
  onChat: React.PropTypes.func.isRequired,
};

export default connect(mapStateToProps, mapDispatchToProps)(ChatContainer);
