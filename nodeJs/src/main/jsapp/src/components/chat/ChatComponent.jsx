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
import { Grid, Row, Col, FormGroup, FormControl, InputGroup, Button, Panel, DropdownButton } from 'react-bootstrap';
import {render, unmountComponentAtNode} from 'react-dom';

import WebSocket from '../common/webSocket';
import User from '../avro/User';

import ChatAreaComponent from './ChatAreaComponent';

require('./Chat.scss');

//below would have key as the identifier of the chatArea and values being a JSON object of text to display
//and other necessary information
const CHAT_MAPPER = {
  __proto__: null
};
const GENERAL_CHAT_KEY = 'general'; //for general,default chat area (i.e. chat lobby invite, whisper, and etc)

class ChatComponent extends Component {

  constructor(props) {
    super(props);

    this.state = {
      chatId: GENERAL_CHAT_KEY
    };

    this.nodeMaps = new Map();
    this.prevChatAreaNode = null;
    this.handleChatHandler = this.handleChat.bind(this);
  }

  componentDidMount() {
    const user = this.props.user;
    if (user) {
      this.ws = new WebSocket('socket');
      this.ws.connect()
        .then((frame) => {
          console.debug('chat frame', frame);
        });
      const subscribedPulseId = this.props.subscribedPulseId;
      if (subscribedPulseId) {
        this.mountChatAreaComponent([subscribedPulseId.pulseId.id, ''].join(''), 'Pulse');
      }

      this.mountChatAreaComponent(GENERAL_CHAT_KEY, 'Chat');

      Object.keys(this.props.lobbies).forEach((key) => {
        this.mountChatAreaComponent(this.props.lobbies[key], key);
      });
    }
  }

  componentWillUnmount() {
    this.recycleWS();
  }

  mountChatAreaComponent(id, dropDownText) {
    const chatAreaElement = document.createElement('div');
    let subscription = '';
    let eagerConnect = false;

    this.chatPanelNode.appendChild(chatAreaElement);
    CHAT_MAPPER[id] = { text: dropDownText, eventKey: id };

    if (id !== GENERAL_CHAT_KEY) {
      subscription = ['/topics/chat/', id].join('');
      chatAreaElement.style.display = 'none';
    } else {
      // general chat is the default chat area and will also have other chat contents
      // such as whispers, chat lobby invites, system messages, and etc

      eagerConnect = true; // need eager connect for system messages and etc
      subscription = ['/topics/privateChat/', this.props.user.id.id].join('');
      this.switchToNewChatAreaNode(chatAreaElement);
    }
    
    render((<ChatAreaComponent id={id} subscription={subscription} eagerConnect={eagerConnect}
            isChatLobby={this.isChatLobby(id)}></ChatAreaComponent>), chatAreaElement);
    this.nodeMaps.set(id, chatAreaElement);

    // this.refs.chatDropDownButton.addChatMenuItem(CHAT_MAPPER[id]);
  }

  unmountChatAreaComponent(id) {
    const node = this.nodeMaps.get(id);
    this.nodeMaps.delete(id);

    unmountComponentAtNode(node);
    
    // this.refs.chatDropDownButton.removeChatMenuItem(CHAT_MAPPER[id]);
    delete CHAT_MAPPER[id];
  }

  handleChatSelect(eventKey) {
    console.debug('handleChatSelect', eventKey);
    
    this.state.chatId = eventKey;

    // API.publish(TOPICS.CHAT_AREA, {action: 'chatConnect', id: this.state.chatId});
    
    const node = this.nodeMaps.get(eventKey);
    this.switchToNewChatAreaNode(node);
  }

  switchToNewChatAreaNode(chatAreaNode) {
    console.debug('switchToNewChatAreaNode', chatAreaNode);

    if (this.prevChatAreaNode !== null) {
      this.prevChatAreaNode.style.display = 'none';
    }

    this.prevChatAreaNode = chatAreaNode;
    chatAreaNode.style.display = '';
  }

  /*
   * Returns the main types from CHAT_TYPE,
   * note CHAT_LOBBY_INVITE, WHISPER types should not use this function
   * as it should be set manually during the actions
   */
  getRegularChatType(chatId) {
    if (this.isChatLobby(chatId)) {
      return CHAT_TYPE.CHAT_LOBBY;
    } else if (CHAT_MAPPER[chatId].text === 'Pulse') {
      return CHAT_TYPE.PULSE;
    } else {
      return CHAT_TYPE.GENERAL;
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

    const user = this.props.user;

    if (this.chatInputNode.value[0] === '/') {
      // means an action
      this.props.onChat(this.chatInputNode.value);
    } else {
      // usual chat, need to send the type (i.e. for chatLobby need to log the message)
      this.ws.send(['/pulsing/chat/', this.state.chatId].join(''), {},
                  JSON.stringify({ message: this.chatInputNode.value,
                                  type: this.getRegularChatType(this.state.chatId),
                                  userId: user.id.id, name: user.name }));
    }

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

  recycleWS() {
    if (this.ws) {
      this.ws.destroy();
      this.ws = null;
    }
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
              <FormGroup>
                <InputGroup>
                  <InputGroup.Button>
                    <DropDownButtonComponent ref='chatDropDownButton' title="Chat"
                      onSelect={this.handleChatSelect.bind(this)} />
                  </InputGroup.Button>
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
          <Row>
            <Col>
              <Panel ref={chatPanelRef}>
              &nbsp;
              </Panel>
            </Col>
          </Row>
        </Grid>
      </div>
    );
  }
}

ChatComponent.propTypes = {
  user: React.PropTypes.objectOf(User).isRequired,
  subscribedPulseId: React.PropTypes.number.isRequired,
  lobbies: React.PropTypes.object.isRequired,
  lobbyMessages: React.PropTypes.object.isRequired,
  paging: React.PropTypes.object.isRequired,
  onChat: React.PropTypes.func.isRequired,
};

export default ChatComponent;
