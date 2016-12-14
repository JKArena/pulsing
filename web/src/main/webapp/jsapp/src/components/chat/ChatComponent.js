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

require('./Chat.scss');

import {Grid, Row, Col, FormGroup, FormControl, InputGroup, Button, Panel} from 'react-bootstrap';
import {render, findDOMNode, unmountComponentAtNode} from 'react-dom';
import React, {Component} from 'react';
import {TOPICS, API} from '../../common/PubSub';
import Storage from '../../common/Storage';
import WebSockets from '../../common/WebSockets';
import CreateChatLobbyAction from './actions/CreateChatLobbyAction';
import ChatAreaComponent from './area/ChatAreaComponent';
import ChatDropDownButtonComponent from './dropDownButton/ChatDropDownButtonComponent';

const CHAT_PULSE_KEY = {
  __proto__: null
};

class ChatComponent extends Component {

  constructor(props) {
    super(props);

    this.state = {
      chatId: ''
    };
    this.nodeMaps = new Map();
    this.prevChatAreaNode = null;

    this.pulseSubscribedHandler = this.pulseSubscribed.bind(this);
    this.pulseUnSubscribedHandler = this.pulseUnSubscribed.bind(this);
  }
  
  componentDidMount() {
    this.chatInputNode = findDOMNode(this.refs.chatInput);
    this.chatPanelNode = findDOMNode(this.refs.chatPanel).querySelector(':scope .panel-body');

    this.ws = new WebSockets('socket');
    this.ws.connect()
      .then(frame => {
        console.debug('chat frame', frame);
      });

    API.subscribe(TOPICS.PULSE_SUBSCRIBED, this.pulseSubscribedHandler);
    API.subscribe(TOPICS.PULSE_UN_SUBSCRIBED, this.pulseUnSubscribedHandler);

    let subscribedPulseId = Storage.subscribedPulseId;
    if(subscribedPulseId) {
      this.pulseSubscribed({pulseId: subscribedPulseId});
    }
  }
  
  componentWillUnmount() {
    if(this.ws) {
      this.ws.destroy();
      this.ws = null;
    }

    API.unsubscribe(TOPICS.PULSE_SUBSCRIBED, this.pulseSubscribedHandler);
    API.unsubscribe(TOPICS.PULSE_UN_SUBSCRIBED, this.pulseUnSubscribedHandler);
  }

  pulseSubscribed(pSubscribed) {
    console.debug('chat pulseSubscribed', pSubscribed);

    this.mountChatAreaComponent(pSubscribed.pulseId.id+'', 'Pulse');
  }

  pulseUnSubscribed(pUnSubscribed) {
    console.debug('chat pulseUnSubscribed', pUnSubscribed);

    this.unmountChatAreaComponent(pUnSubscribed.pulseId.id+'');
  }

  mountChatAreaComponent(id, dropDownText) {
    let caEle = document.createElement('div');
    let subscription = '/topics/chat/' + id;

    this.chatPanelNode.appendChild(caEle);

    render((<ChatAreaComponent subscription={subscription}></ChatAreaComponent>), caEle);
    this.nodeMaps.set(id, caEle);

    CHAT_PULSE_KEY[id] = {text: dropDownText, eventKey: id};
    this.refs.chatDropDownButton.addChatMenuItem(CHAT_PULSE_KEY[id]);
  }

  unmountChatAreaComponent(id) {
    let node = this.nodeMaps.get(id);
    this.nodeMaps.delete(id);

    unmountComponentAtNode(node);
    
    this.refs.chatDropDownButton.removeChatMenuItem(CHAT_PULSE_KEY[id]);
  }

  handleChatSelect(eventKey) {
    console.debug('handleChatSelect', eventKey);
    let node = this.nodeMaps.get(eventKey);

    this.state.chatId = eventKey;
    this.switchToNewChatAreaNode(node);
  }

  switchToNewChatAreaNode(cAreaNode) {
    if(this.prevChatAreaNode !== null) {
      this.prevChatAreaNode.style.display = 'none';
    }

    this.prevChatAreaNode = cAreaNode;
    cAreaNode.style.display = '';
  }

  handleChat() {
    console.debug('handleChat');
    if(this.chatInputNode.value.length === 0) {
      return;
    }

    let user = Storage.user;

    if(this.chatInputNode.value[0] === '/') {
      //means an action
      this.handleChatAction(user);
    } else {
      //usual chat
      let isChatLobby = CHAT_PULSE_KEY[this.state.chatId].text !== 'Pulse';
      this.ws.send('/pulsing/chat/' + this.state.chatId + '/' + isChatLobby, {},
                  JSON.stringify({message: this.chatInputNode.value, userId: user.id.id, name: user.name}));
    }

    
    this.chatInputNode.value = '';
  }

  handleChatAction(user) {
    let split = this.chatInputNode.value.split(' ');
    
    if(split[0] === '/create') {

      let cLName = split[1];
      CreateChatLobbyAction.createChatLobby(user.id, cLName)
        .then((chatId) => {
          this.mountChatAreaComponent(chatId, cLName);
        });
    }
  }
  
  render() {
    
    return (
        <div className='chat-component'>
          <Grid>
            <Row>
              <Col>
                <FormGroup>
                  <InputGroup>
                    <InputGroup.Button>
                      <ChatDropDownButtonComponent ref='chatDropDownButton' onSelect={this.handleChatSelect.bind(this)} />
                    </InputGroup.Button>
                    <FormControl type='text' ref='chatInput' />
                    <InputGroup.Button>
                      <Button onClick={this.handleChat.bind(this)}>Send</Button>
                    </InputGroup.Button>
                  </InputGroup>
                </FormGroup>
              </Col>
            </Row>
            <Row>
              <Col>
                <Panel ref='chatPanel'>
                &nbsp;
                </Panel>
              </Col>
            </Row>
          </Grid>
        </div>
    );
  }
}

ChatComponent.displayName = 'ChatComponent';

export default ChatComponent;
