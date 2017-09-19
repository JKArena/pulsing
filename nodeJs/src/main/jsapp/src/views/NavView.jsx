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

import React from 'react';
import { IndexLink } from 'react-router';
import { Navbar, Nav, NavItem, NavDropdown, Badge } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';

import User from '../avro/User';

const NavView = props =>
  (<div className="navbar-component">
    <Navbar inverse>
      <Navbar.Header>
        <Navbar.Brand>
          <IndexLink to="/">Pulsing</IndexLink>
        </Navbar.Brand>
        <Navbar.Toggle />
      </Navbar.Header>

      <Navbar.Collapse>
        {(() => {
          const geoEnabled = props.geo.user && props.geo.user.lat && props.geo.user.lng;
          const mapView = props.user && geoEnabled ?
            (<Nav>
              <LinkContainer to={{ pathname: '/map/pulse', query: { mapId: 'pulseMap' } }}>
                <NavItem>Map</NavItem>
              </LinkContainer>
            </Nav>) : null;
          return mapView;
        })()}

        {(() => {
          const createView = props.user ?
            (<Nav>
              <NavDropdown id="createActions" title="Create">
                <LinkContainer to="/createPulse"><NavItem>Pulse</NavItem></LinkContainer>
                <LinkContainer to="/createLocation"><NavItem>Locations</NavItem></LinkContainer>
              </NavDropdown>
            </Nav>) : null;
          return createView;
        })()}

        {(() => {
          const logView = props.user ?
            (<Nav pullRight onSelect={props.onLogOut}>
              <LinkContainer to="/"><NavItem>Logout</NavItem></LinkContainer>
            </Nav>) :
            (<Nav pullRight>
              <LinkContainer to="/createUser"><NavItem>Signup</NavItem></LinkContainer>
              <LinkContainer to="/login"><NavItem>Login</NavItem></LinkContainer>
            </Nav>);
          return logView;
        })()}

        {(() => {
          const alertsCount = props.alerts.length;
          const alertView = props.user ?
            (<Nav pullRight>
              <LinkContainer to="/alertListing">
                <NavItem>Alert <Badge>{alertsCount}</Badge></NavItem>
              </LinkContainer>
            </Nav>) : null;
          return alertView;
        })()}
      </Navbar.Collapse>
    </Navbar>
  </div>);

NavView.propTypes = {
  user: React.PropTypes.objectOf(User),
  geo: React.PropTypes.shape({
    user: React.PropTypes.shape({
      lat: React.PropTypes.number,
      lng: React.PropTypes.number,
    }),
    pulse: React.PropTypes.shape({
      lat: React.PropTypes.number,
      lng: React.PropTypes.number,
    }),
  }),
  alerts: React.PropTypes.arrayOf(
    React.PropTypes.shape({
      invitationId: React.PropTypes.string,
      invitationType: React.PropTypes.string,
      fromUserId: React.PropTypes.number,
      expiration: React.PropTypes.number,
    }),
  ),
  onLogOut: React.PropTypes.func.isRequired,
};

NavView.defaultProps = {
  user: null,
  geo: null,
  alerts: [],
};

export default NavView;
