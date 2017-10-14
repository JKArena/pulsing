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
import { Grid, Row, Col, FormGroup, FormControl, InputGroup, Button, Panel } from 'react-bootstrap';

import * as chatTypes from '../common/eventTypes';
import ChatAreaComponent from './ChatAreaComponent';

require('./Chat.scss');

const GENERAL_CHAT_KEY = 'general'; // for general,default chat area (i.e. chat lobby invite, whisper, and etc)

class ChatComponent extends Component {

  constructor(props) {
    super(props);

    this.state = {
      chatId: GENERAL_CHAT_KEY,
    };

    this.handleChatHandler = this.handleChat.bind(this);
  }

  /*
   * Returns the main types from chatTypes,
   * note CHAT_LOBBY_INVITE, WHISPER types should not use this function
   * as it should be set manually during the actions
   */
  getRegularChatType(chatId) {
    if (this.isChatLobby(chatId)) {
      return chatTypes.CHAT_LOBBY;
    } else if (CHAT_MAPPER[chatId].text === 'Pulse') {
      return chatTypes.PULSE;
    } else {
      return chatTypes.GENERAL;
    }
  }

  isChatLobby(chatId) {
    // note others such as WHISPER, CHAT_INVITE, and etc are by chatAction /whisper name and etc
    return CHAT_MAPPER[chatId].text !== 'Pulse' && CHAT_MAPPER[chatId].eventKey !== GENERAL_CHAT_KEY;
  }

  handleChat() {
    console.debug('handleChat');
    if (this.chatInputNode.value.length === 0) {
      return;
    }

    this.props.onChat(this.chatInputNode.value,
      { id: this.state.chatId, type: this.getRegularChatType(this.state.chatId) });

    this.chatInputNode.value = '';
  }

  getChatLobbyInfo(chatLobbyName) {
    let chatInfo = null;

    for (let key of Object.keys(CHAT_MAPPER)) {
      if (CHAT_MAPPER[key].text === chatLobbyName) { //chat name
        chatInfo = CHAT_MAPPER[key];
        break;
      }
    }

    return cInfo;
  }

  render() {
    const chatInputRef = (ele) => {
      this.chatInputNode = ele.node;
    };
    const chatPanelRef = (ele) => {
      this.chatPanelNode = ele.node.querySelector(':scope .panel-body');
    };

    return (
      <div className="chat-component">
        <Grid>
          <Row>
            <Col>
              <Panel ref={chatPanelRef}>
              &nbsp;
              </Panel>
            </Col>
          </Row>
          <Row>
            <Col>
              <FormGroup>
                <InputGroup>
                  <FormControl
                    type="text"
                    inputRef={chatInputRef}
                  />
                  <InputGroup.Button>
                    <Button onClick={this.handleChatHandler}>Send</Button>
                  </InputGroup.Button>
                </InputGroup>
              </FormGroup>
            </Col>
          </Row>
        </Grid>
      </div>
    );
  }
}

ChatComponent.propTypes = {
  subscribedPulseId: PropTypes.number.isRequired,
  lobbies: PropTypes.objectOf(PropTypes.object).isRequired,
  lobbyMessages: PropTypes.objectOf(PropTypes.object).isRequired,
  paging: PropTypes.objectOf(PropTypes.object).isRequired,
  onChat: PropTypes.func.isRequired,
};

export default ChatComponent;
