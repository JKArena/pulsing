'use strict';

import React, {Component, PropTypes} from 'react';
import {Navbar, Nav, NavItem} from 'react-bootstrap';

require('styles/navbar/NavBar.scss');

class NavBarComponent extends Component {
  
  constructor(props) {
    super(props);
    
    this.state = {loggedIn: false};
  }
  
  render() {
    
    return (
      <div>
        <Navbar inverse>
          <Navbar.Header>
            <Navbar.Brand>
              <a href="#">Interested</a>
            </Navbar.Brand>
            <Navbar.Toggle/>
          </Navbar.Header>
          
          <Navbar.Collapse>
            <Nav>
              <NavItem href='#'>Dashboard</NavItem>
            </Nav>
            
            <Nav pullRight>
              {this.state.loggedIn ? <NavItem href='/logout'>Logout</NavItem> : <NavItem href='/login'>Login</NavItem>}
            </Nav>
          </Navbar.Collapse>
        </Navbar>
      </div>
    );
  }
  
}

NavBarComponent.displayName = 'NavBarComponent';

NavBarComponent.propTypes = {};
NavBarComponent.defaultProps = {
    user: null
};

export default NavBarComponent;
