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

import {Grid, Row, Col, FormGroup, FormControl, InputGroup, Button, Tabs} from 'react-bootstrap';
import {render, findDOMNode, unmountComponentAtNode} from 'react-dom';
import React, {Component} from 'react';
import {TOPICS, API} from '../../common/PubSub';
import Storage from '../../common/Storage';
import WebSockets from '../../common/WebSockets';
import ChatAreaComponent from './area/ChatAreaComponent';
import ChatDropDownButtonComponent from './dropDownButton/ChatDropDownButtonComponent';

const CHAT_PULSE_KEY = {
  __proto__: null,
  'PULSE': {text: 'Pulse', eventKey: ''}
};

class ChatComponent extends Component {

  constructor(props) {
    super(props);

    this.state = {
      chatId: ''
    };
    this.nodeMaps = new Map();

    this.pulseSubscribedHandler = this.pulseSubscribed.bind(this);
    this.pulseUnSubscribedHandler = this.pulseUnSubscribed.bind(this);
  }
  
  componentDidMount() {
    this.chatInputNode = findDOMNode(this.refs.chatInput);
    this.chatTabsNode = findDOMNode(this.refs.chatTabs);

    this.ws = new WebSockets('socket');
    this.ws.connect()
      .then(frame => {
        console.debug('chat frame', frame);
      });

    API.subscribe(TOPICS.PULSE_SUBSCRIBED, this.pulseSubscribedHandler);
    API.subscribe(TOPICS.PULSE_UN_SUBSCRIBED, this.pulseUnSubscribedHandler);
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

    let caEle = document.createElement('div');
    let id = pSubscribed.pulseId.id;
    let subscription = '/topics/chat/' + id;

    this.chatTabsNode.appendChild(caEle);

    render((<ChatAreaComponent title='Pulse' subscription={subscription}></ChatAreaComponent>), caEle);
    this.nodeMaps.set(id, {caEle: caEle});

    CHAT_PULSE_KEY.PULSE.eventKey = id;
    this.refs.chatDropDownButton.addChatMenuItem(CHAT_PULSE_KEY.PULSE);
  }

  pulseUnSubscribed(pUnSubscribed) {
    console.debug('chat pulseUnSubscribed', pUnSubscribed);

    let id = pUnSubscribed.pulseId.id;
    let nodes = this.nodeMaps.get(id);

    unmountComponentAtNode(nodes.caEle);
    this.nodeMaps.delete(id);

    this.refs.chatDropDownButton.removeChatMenuItem(CHAT_PULSE_KEY.PULSE);
  }

  handleChatSelect(eventKey) {
    console.debug('handleChatSelect', eventKey);

    this.state.chatId = eventKey;
  }

  handleSelect() {
    console.debug('handleSelect');

  }

  handleChat() {
    console.debug('handleChat');
    if(this.chatInputNode.value.length === 0) {
      return;
    }
    
    this.ws.send('/pulsing/chat/' + this.state.chatId + '/'+ Storage.user.id.id, {},
                  JSON.stringify({message: this.chatInputNode.value}));
    this.chatInputNode.value = '';
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
                <Tabs onSelect={this.handleSelect.bind(this)} ref='chatTabs' id='chatT'>
                </Tabs>
              </Col>
            </Row>
          </Grid>
        </div>
    );
  }
}

ChatComponent.displayName = 'ChatComponent';

export default ChatComponent;
