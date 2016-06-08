import 'core-js/fn/object/assign';
import React from 'react';
import {render} from 'react-dom';
import {Router, Route, IndexRoute, hashHistory} from 'react-router';

import App from './components/App';
import TrendingPulseComponent from './components/pulsing/trending/TrendingPulseComponent';

import SignupComponent from './components/signup/SignupComponent';
import LoginComponent from './components/login/LoginComponent';
import LogoutComponent from './components/logout/LogoutComponent';

// Render the app component into the dom
render((
  <Router history={hashHistory}>
    <Route path='/' component={App}>
      <IndexRoute component={TrendingPulseComponent} />
      <Route path='signup' component={SignupComponent} />
      <Route path="login" component={LoginComponent} />
      <Route path='logout' component={LogoutComponent} />
    </Route>
  </Router>
), document.getElementById('app'));