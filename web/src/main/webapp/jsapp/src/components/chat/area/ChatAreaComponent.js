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

require('./ChatArea.scss');

import {Panel, Tab} from 'react-bootstrap';
import {render, findDOMNode} from 'react-dom';
import React, {Component} from 'react';
import WebSockets from '../../../common/WebSockets';

class ChatAreaComponent extends Component {

  constructor(props) {
    super(props);

    this.chatHandler = this.onChat.bind(this);
    this.subscription = props.subscription;
  }
  
  componentDidMount() {
    this.ws = new WebSockets('pulseSocketJS');
    this.ws.connect()
      .then(frame => {
        console.debug('chat area frame', this.subscription, frame);
        this.sub = this.ws.subscribe(this.subscription, this.chatHandler);
      });
  }
  
  componentWillUnmount() {
    if(this.ws) {
      this.ws.destroy();
      this.ws = null;
    }
  }

  onChat(mChat) {
    console.debug('onChat', mChat);

    if(mChat && mChat.body) {

    }
  }
  
  render() {
    
    return (
        <Tab className='chatarea-component' eventKey={this.props.subscription} title={this.props.title}>
          <Panel>
            &nbsp;
          </Panel>
        </Tab>
    );
  }
}

ChatAreaComponent.displayName = 'ChatAreaComponent';

export default ChatAreaComponent;
