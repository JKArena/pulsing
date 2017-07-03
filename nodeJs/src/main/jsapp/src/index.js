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

import 'core-js/fn/object/assign';
import React from 'react';
import {render} from 'react-dom';
import {Router, Route, IndexRoute, browserHistory} from 'react-router';
import {Provider} from 'react-redux';

import App from './components/App';
import indexStore from './indexStore';
import TrendingPulseSubscriptionsComponent from './components/pulsing/trending/TrendingPulseSubscriptionsComponent';
import MapComponent from './components/map/MapComponent';
import CreatePulseComponent from './components/pulsing/create/CreatePulseComponent';
import CreateLocationComponent from './components/location/create/CreateLocationComponent';
import AlertListingComponent from './components/alert/AlertListingComponent';
import SignupComponent from './components/signup/SignupComponent';
import LoginComponent from './components/login/LoginComponent';

// Render the app component into the dom
render((
  <Provider store={indexStore}>
    <Router history={browserHistory}>
      <Route path='/' component={App}>
        <IndexRoute component={TrendingPulseSubscriptionsComponent} />
        <Route path='map/:store' component={MapComponent} />
        <Route path='createPulse' component={CreatePulseComponent} />
        <Route path='createLocation' component={CreateLocationComponent} />
        <Route path='alertListing' component={AlertListingComponent} />
        <Route path='signup' component={SignupComponent} />
        <Route path='login' component={LoginComponent} />
      </Route>
    </Router>
  </Provider>
), document.getElementById('app'));
