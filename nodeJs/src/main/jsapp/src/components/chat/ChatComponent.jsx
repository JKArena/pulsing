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
import { Grid, Row, Col, FormGroup, FormControl, InputGroup, Button, Tab, Nav, NavItem } from 'react-bootstrap';

import User from '../../avro/User';
import * as chatTypes from '../../common/chatTypes';
import ChatAreaContainer from '../../containers/ChatAreaContainer';

require('./Chat.scss');

class ChatComponent extends Component {

  constructor(props) {
    super(props);

    this.state = {
      chatId: chatTypes.GENERAL_CHAT_KEY,
    };

    this.handleChatHandler = this.handleChat.bind(this);
  }

  getTabNavItems() {
    const navItems = [<NavItem eventKey={chatTypes.GENERAL_CHAT_KEY}>General</NavItem>];
    const lobbies = this.props.lobbies;

    Object.keys(lobbies).forEach((lobbyName) => {
      navItems.push(<NavItem eventKey={lobbies[lobbyName]}>{lobbyName}</NavItem>);
    });
    return navItems;
  }

  getTabContents() {
    const userId = this.props.user.id.id;
    const generalSubscription = ['/topics/privateChat/', userId].join('');
    const contents = [<Tab.Pane eventKey={chatTypes.GENERAL_CHAT_KEY}>
      <ChatAreaContainer
        chatLobbyId={chatTypes.GENERAL_CHAT_KEY}
        subscription={generalSubscription}
      />
    </Tab.Pane>];

    const lobbies = this.props.lobbies;
    Object.keys(lobbies).forEach((lobbyName) => {
      const lobbyId = lobbies[lobbyName];
      const lobbySubscription = ['/topics/chat/', lobbyId].join('');

      contents.push(<Tab.Pane eventKey={lobbyId}>
        <ChatAreaContainer
          lobbyId={lobbyId}
          subscription={lobbySubscription}
        />
      </Tab.Pane>);
    });

    return contents;
  }

  handleChat() {
    console.debug('handleChat');
    if (this.chatInputNode.value.length === 0) {
      return;
    }

    this.props.onChat(this.chatInputNode.value,
      { id: this.state.chatId });

    this.chatInputNode.value = '';
  }

  render() {
    const chatInputRef = (ele) => {
      this.chatInputNode = ele.node;
    };

    return (
      <div className="chat-component">
        <Grid>
          <Row>
            <Col>
              <Tab.Container ref={this.chatTabRef} defaultActiveKey={chatTypes.GENERAL_CHAT_KEY}>
                <Row className="clearfix">
                  <Col sm={12}>
                    <Nav bsStyle="tabs">
                      {this.getTabNavItems()}
                    </Nav>
                  </Col>
                  <Col sm={12}>
                    <Tab.Content animation>
                      {this.getTabContents()}
                    </Tab.Content>
                  </Col>
                </Row>
              </Tab.Container>
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
  user: PropTypes.objectOf(User).isRequired,
  subscribedPulseId: PropTypes.number.isRequired,
  lobbies: PropTypes.objectOf(PropTypes.object).isRequired,
  lobbyMessages: PropTypes.objectOf(PropTypes.object).isRequired,
  paging: PropTypes.objectOf(PropTypes.object).isRequired,
  onChat: PropTypes.func.isRequired,
};

export default ChatComponent;
