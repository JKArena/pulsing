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

require('./DropDownButton.scss');

import {DropdownButton, MenuItem} from 'react-bootstrap';
import React, {Component} from 'react';

let dDButtonId = 0;

class DropDownButtonComponent extends Component {

  constructor(props) {
    super(props);

    this.state = {
      title: (props.title || 'General')
    };
    this.id = 'dDButton' + dDButtonId++;
    this.menus = props.menus || {};
  }

  addChatMenuItem(menuItem) {
    console.debug('addMenuItem', menuItem);

    this.menus[menuItem.eventKey] = menuItem.text;
    this.setState(this.state);
  }

  removeChatMenuItem(menuItem) {
    console.debug('removeMenuItem', menuItem);

    delete this.menus[menuItem.eventKey];
    this.setState(this.state);
  }

  onSelect(eventKey) {
    console.debug('onSelect ', eventKey);

    this.state.title = this.menus[eventKey];
    this.props.onSelect(eventKey);
    this.setState(this.state);
  }

  render() {
    let menuItems = [];

    Object.keys(this.menus).forEach((key, indx) => {
      let text = this.menus[key];

      menuItems.push(<MenuItem eventKey={key} key={indx}>{text}</MenuItem>);
    });
    
    return (
        <DropdownButton title={this.state.title} bsStyle='primary' id={this.id}
          className='dropdownbutton-component'
          onSelect={this.onSelect.bind(this)}>
          {menuItems}
        </DropdownButton>
    );
  }
}

DropDownButtonComponent.displayName = 'DropDownButtonComponent';

export default DropDownButtonComponent;
