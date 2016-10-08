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

require('./ChatDropDownButton.scss');

import {DropdownButton, MenuItem} from 'react-bootstrap';
import React, {Component} from 'react';

class ChatDropDownButtonComponent extends Component {

  constructor(props) {
    super(props);

    this.state = {};
    this.menus = {};
  }

  addChatMenuItem(menuItem) {
    console.debug('addChatMenuItem', menuItem);

    this.menus[menuItem.eventKey] = menuItem.text;
    this.setState(this.state);
  }

  removeChatMenuItem(menuItem) {
    console.debug('removeChatMenuItem', menuItem);

    delete this.menus[menuItem.eventKey];
    this.setState(this.state);
  }

  render() {
    let menuItems = [];

    Object.keys(this.menus).forEach((key, indx) => {
      let text = this.menus[key];

      menuItems.push(<MenuItem eventKey={key} key={indx}>{text}</MenuItem>);
    });
    
    return (
        <DropdownButton title='Chat' bsStyle='primary' id='chatDdb'
          className='chatdropdownbutton-component'
          ref='chatDropDown' onSelect={this.props.onSelect}>
          {menuItems}
        </DropdownButton>
    );
  }
}

ChatDropDownButtonComponent.displayName = 'ChatDropDownButtonComponent';

export default ChatDropDownButtonComponent;
