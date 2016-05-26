'use strict';

import React, {Component, PropTypes} from 'react';
import {Navbar, Nav, NavItem} from 'react-bootstrap';

require('./NavBar.scss');

class NavBarComponent extends Component {
  
  constructor(props) {
    super(props);
    
    this.state = {loggedIn: false};
    this.sMenus = [];
    
    props.staticMenu.forEach(function(menu) {
      this.sMenus.push(<NavItem href={menu.href}>{menu.label}</NavItem>);
    }.bind(this));
  }
  
  render() {
    let userNavItems = [];
    let userNavs = this.state.loggedIn ? [{href: '/logout', label: 'Logout'}] : [{href:'/login', label: 'Login'}, {href: '/signup', label: 'Signup'}];
    
    userNavs.forEach(function(navs) {
      userNavItems.push(<NavItem href={navs.href}>{navs.label}</NavItem>);
    });
    
    return (
      <div>
        <Navbar inverse>
          <Navbar.Header>
            <Navbar.Brand>
              <a href='#'>Interested</a>
            </Navbar.Brand>
            <Navbar.Toggle/>
          </Navbar.Header>
          
          <Navbar.Collapse>
            <Nav>
              {this.sMenus}
            </Nav>
            
            <Nav pullRight>
              {userNavItems}
            </Nav>
          </Navbar.Collapse>
        </Navbar>
      </div>
    );
  }
  
}

NavBarComponent.displayName = 'NavBarComponent';

NavBarComponent.propTypes = {
  staticMenu: React.PropTypes.arrayOf(React.PropTypes.object),
  user: React.PropTypes.object
};
NavBarComponent.defaultProps = {
  staticMenu: [
               {
                 label: 'Trending',
                 href: '#'
               }
               ],
  user: null
};

export default NavBarComponent;