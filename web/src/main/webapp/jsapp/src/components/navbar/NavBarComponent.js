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

require('./NavBar.scss');

import React, {Component} from 'react';
import {IndexLink} from 'react-router';
import {Navbar, Nav, NavItem} from 'react-bootstrap';
import {LinkContainer} from 'react-router-bootstrap';

import NavBarStore from './NavBarStore';

const _store = new NavBarStore();

class NavBarComponent extends Component {
  
  constructor(props) {
    super(props);
    
    this.state = {loggedIn: false};
  }
  
  componentDidMount() {
    _store.addChangeListener(this._onChange);
  }
  
  componentWillUnmount() {
    _store.removeChangeListener(this._onChange);
  }
  
  _onChange() {
    console.info('_onChange');
    
  }
  
  logOut() {
    this.state.loggedIn = false;
    
    _store.user = null;
    
    this.setState(this.state);
  }
  
  render() {
    
    return (
        <div class='navbar-component'>
          <Navbar inverse>
            <Navbar.Header>
              <Navbar.Brand>
                <IndexLink to='/'>Pulsing</IndexLink>
              </Navbar.Brand>
              <Navbar.Toggle/>
            </Navbar.Header>
            
            <Navbar.Collapse>
              <Nav>
              </Nav>
              
              {(() => {
                if(this.state.loggedIn) {
                  return <Nav pullRight onSelect={this.logOut}><LinkContainer to='/'><NavItem>Logout</NavItem></LinkContainer></Nav>
                } else {
                  return <Nav pullRight>
                    <LinkContainer to='/signup'><NavItem>Signup</NavItem></LinkContainer>
                    <LinkContainer to='/login'><NavItem>Login</NavItem></LinkContainer>
                  </Nav>;
                }
              })()}
              
            </Navbar.Collapse>
          </Navbar>
        </div>
        );
  }
  
}

NavBarComponent.displayName = 'NavBarComponent';

export default NavBarComponent;