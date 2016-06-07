'use strict';

require('./NavBar.scss');

import React, {Component, PropTypes} from 'react';
import {Navbar, Nav, NavItem} from 'react-bootstrap';
import {Router, Route, Link, IndexRoute, browserHistory} from 'react-router';
import {LinkContainer, IndexLinkContainer} from 'react-router-bootstrap';

import TrendingPulseComponent from '../pulsing/trending/TrendingPulseComponent';

class NavBarComponent extends Component {
  
  constructor(props) {
    super(props);
    
    this.state = {loggedIn: false};
  }
  
  render() {
    
    return (
        <Navbar inverse>
          <Navbar.Header>
            <Navbar.Brand>
              <a href='/'>Interested</a>
            </Navbar.Brand>
            <Navbar.Toggle/>
          </Navbar.Header>
          
          <Navbar.Collapse>
            <Nav>
              <NavItem href='/'><TrendingPulseComponent></TrendingPulseComponent></NavItem>
            </Nav>
            
            <Nav pullRight>
              {this.state.loggedIn ? <NavItem href='/logout'>Logout</NavItem> : <NavItem href='/login'>Login</NavItem>}
            </Nav>
          </Navbar.Collapse>
        </Navbar>
        );
  }
  
}

NavBarComponent.displayName = 'NavBarComponent';

NavBarComponent.propTypes = {
  user: React.PropTypes.object
};
NavBarComponent.defaultProps = {
  user: null
};

export default NavBarComponent;