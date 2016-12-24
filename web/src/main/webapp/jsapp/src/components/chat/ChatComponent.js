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
import DropDownButtonComponent from '../../dropDownButton/DropDownButtonComponent';
import CreateChatLobbyAction from './actions/CreateChatLobbyAction';
import GetChatLobbiesAction from './actions/GetChatLobbiesAction';
import ChatAreaComponent from './area/ChatAreaComponent';

const CHAT_PULSE_KEY = {
  __proto__: null
};

const GENERAL_CHAT_KEY = 'general';

class ChatComponent extends Component {

  constructor(props) {
    super(props);

    this.state = {
      chatId: GENERAL_CHAT_KEY
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

    this.mountChatAreaComponent(GENERAL_CHAT_KEY, 'Chat');

    GetChatLobbiesAction.queryChatLobbies(Storage.user.id)
      .then((chatLobbies) => {
        Object.keys(chatLobbies).forEach(key => {

          this.mountChatAreaComponent(chatLobbies[key], key);
        });
      });
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
    let subscription = '';

    this.chatPanelNode.appendChild(caEle);
    CHAT_PULSE_KEY[id] = {text: dropDownText, eventKey: id};

    if(id !== GENERAL_CHAT_KEY) {
      subscription = '/topics/chat/' + id;
      caEle.style.display = 'none';
    } else {
      this.switchToNewChatAreaNode(caEle);
    }
    
    render((<ChatAreaComponent id={id} subscription={subscription} isChatLobby={this.isChatLobby(id)}></ChatAreaComponent>), caEle);
    this.nodeMaps.set(id, caEle);

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
    
    this.state.chatId = eventKey;

    API.publish(TOPICS.CHAT_AREA, {action: 'chatConnect', id: this.state.chatId});
    
    let node = this.nodeMaps.get(eventKey);
    this.switchToNewChatAreaNode(node);
  }

  switchToNewChatAreaNode(cAreaNode) {
    console.debug('switchToNewChatAreaNode', cAreaNode);

    if(this.prevChatAreaNode !== null) {
      this.prevChatAreaNode.style.display = 'none';
    }

    this.prevChatAreaNode = cAreaNode;
    cAreaNode.style.display = '';
  }

  isChatLobby(chatId) {
    return CHAT_PULSE_KEY[chatId].text !== 'Pulse' && CHAT_PULSE_KEY[chatId].eventKey !== GENERAL_CHAT_KEY;
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
      //usual chat, need to send whether chatLobby as need to log
      
      this.ws.send('/pulsing/chat/' + this.state.chatId + '/' + this.isChatLobby(this.state.chatId), {},
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
          API.publish(TOPICS.CHAT_AREA, {action: 'systemMessage', id: this.state.chatId,
              message: 'Chat Lobby : ' + cLName + ' created successfully!'});
        });
    } else if(split[0] === '/invite') {

      //so to allow inviting friends to the chatLobby
      if(this.isChatLobby(this.state.chatId)) {
        //let uName = split[1];

      }
      
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
                      <DropDownButtonComponent ref='chatDropDownButton' title='Chat' onSelect={this.handleChatSelect.bind(this)} />
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
