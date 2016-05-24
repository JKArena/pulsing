require('normalize.css/normalize.css');
require('bootstrap/dist/css/bootstrap.css');
require('styles/App.css');

import React from 'react';
import NavBarComponent from './navbar/NavBarComponent';

class AppComponent extends React.Component {
  render() {
    return (
      <div>
        <NavBarComponent></NavBarComponent>
      </div>
    );
  }
}

AppComponent.defaultProps = {
};

export default AppComponent;
