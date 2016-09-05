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
import {IndexLink, browserHistory} from 'react-router';
import {Navbar, Nav, NavItem, NavDropdown, MenuItem} from 'react-bootstrap';
import {LinkContainer} from 'react-router-bootstrap';

import {TOPICS, API} from '../../common/PubSub';
import Storage from '../../common/Storage';

class NavBarComponent extends Component {
  
  getInitialState() {
    return {loggedIn: !!Storage.user};
  }
  
  loggedOut() {
    this.state.loggedIn = false;
    Storage.user.clearGeoWatch();
    Storage.user = null;
    
    API.publish(TOPICS.AUTH, {loggedIn: false});
    this.setState(this.state);
  }
  
  _onAuth(auth) {
    const MAIN = '/';
    
    this.state.loggedIn = auth.loggedIn;
    this.setState(this.state);
    
    browserHistory.push(MAIN);
  }
  
  componentDidMount() {
    API.subscribe(TOPICS.AUTH, this._onAuth.bind(this));
  }
  
  componentWillUnmount() {
    API.unsubscribe(TOPICS.AUTH, this._onAuth.bind(this));
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
                <LinkContainer to={{ pathname: '/map/pulse', query: {mapId: 'pulseMap'} }}>
                  <NavItem>Map</NavItem>
                </LinkContainer>
                <NavDropdown title='Pulse Actions'>
                  <LinkContainer to='/createPulse'><NavItem>Create</NavItem></LinkContainer>
                </NavDropdown>
              </Nav>
              
              {(() => {
                if(this.state.loggedIn) {
                  return <Nav pullRight onSelect={this.loggedOut.bind(this)}><LinkContainer to='/'><NavItem>Logout</NavItem></LinkContainer></Nav>
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
