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
import {findDOMNode} from 'react-dom';
import {IndexLink, browserHistory} from 'react-router';
import {Navbar, Nav, NavItem, NavDropdown, Badge} from 'react-bootstrap';
import {LinkContainer} from 'react-router-bootstrap';

import {TOPICS, API} from '../../common/PubSub';
import WebSockets from '../../common/WebSockets';
import Storage from '../../common/Storage';
import Common from '../../common/Common';

class NavBarComponent extends Component {

  constructor(props) {
    super(props);

    this.state = {loggedIn: !!Storage.user, lat: 0, lng: 0, alerts: 0};
    this.authHandler = this.onAuth.bind(this);
    this.navigationChangeHandler = this.onNavigationChange.bind(this);
    this.alertHandler = this.onAlert.bind(this);
  }
  
  loggedOut() {
    this.state.loggedIn = false;
    Storage.user.clearGeoWatch();
    Storage.user = null;
    
    API.publish(TOPICS.AUTH, {loggedIn: false});
    this.setState(this.state);
    this.recycleWS();
  }
  
  onAuth(auth) {
    
    this.state.loggedIn = auth.loggedIn;
    this.setState(this.state);
    browserHistory.push(Common.MAIN_NAV_PATH);

    if(this.state.loggedIn) {
      this.badgeNode = findDOMNode(this.refs.badge).querySelector(':scope .badge');
      this.ws = new WebSockets('socket');
      this.ws.connect()
        .then(frame => {
          console.debug('alert frame', frame);
          this.sub = this.ws.subscribe('/topics/alert/' + Storage.user.id.id, this.alertHandler);
        });
    } else {
      this.recycleWS();
    }
  }
  
  componentDidMount() {
    API.subscribe(TOPICS.AUTH, this.authHandler);
    API.subscribe(TOPICS.NAVIGATION_CHANGE, this.navigationChangeHandler);
  }
  
  componentWillUnmount() {
    API.unsubscribe(TOPICS.AUTH, this.authHandler);
    API.unsubscribe(TOPICS.NAVIGATION_CHANGE, this.navigationChangeHandler);
    this.recycleWS();
  }

  recycleWS() {
    if(this.ws) {
      this.ws.destroy();
      this.ws = null;
    }
  }

  onNavigationChange(newNav) {
    console.debug('onNavigationChange', newNav);
    
    this.setState(this.state);

    browserHistory.push(newNav);
  }

  onAlert(alertMsg) {
    console.debug('alertMsg', alertMsg);

    this.badgeNode.innerHTML = ++this.state.alerts;
  }
  
  render() {

    let user = Storage.user;

    this.state.loggedIn = !!user;
    if(this.state.loggedIn) {
      this.state.lat = user.lat;
      this.state.lng = user.lng;
    }

    return (
        <div className='navbar-component'>
          <Navbar inverse>
            <Navbar.Header>
              <Navbar.Brand>
                <IndexLink to='/'>Pulsing</IndexLink>
              </Navbar.Brand>
              <Navbar.Toggle/>
            </Navbar.Header>
            
            <Navbar.Collapse>

              {(() => {
                if(this.state.loggedIn && this.state.lat && this.state.lng) {
                  return <Nav>
                      <LinkContainer to={{ pathname: '/map/pulse', query: {mapId: 'pulseMap'} }}>
                        <NavItem>Map</NavItem>
                      </LinkContainer>
                    </Nav>;
                }
              })()}

              {(() => {
                if(this.state.loggedIn) {
                  return <Nav>
                      <NavDropdown id='createActions' title='Create'>
                        <LinkContainer to='/createPulse'><NavItem>Pulse</NavItem></LinkContainer>
                        <LinkContainer to='/createLocation'><NavItem>Locations</NavItem></LinkContainer>
                      </NavDropdown>
                    </Nav>;
                }
              })()}

              {(() => {
                if(this.state.loggedIn) {
                  return <Nav pullRight onSelect={this.loggedOut.bind(this)}>
                      <LinkContainer to='/'><NavItem>Logout</NavItem></LinkContainer>
                    </Nav>;
                } else {
                  return <Nav pullRight>
                    <LinkContainer to='/signup'><NavItem>Signup</NavItem></LinkContainer>
                    <LinkContainer to='/login'><NavItem>Login</NavItem></LinkContainer>
                  </Nav>;
                }
              })()}

              {(() => {
                if(this.state.loggedIn) {
                  return <Nav pullRight>
                      <LinkContainer to='/alertListing'><NavItem ref='badge'>Alert <Badge>{this.state.alerts}</Badge></NavItem></LinkContainer>
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
