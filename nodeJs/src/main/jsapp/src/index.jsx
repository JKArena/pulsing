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

import 'core-js/fn/object/assign';
import React from 'react';
import { render } from 'react-dom';
import { Router, Route, IndexRoute, browserHistory } from 'react-router';
import { Provider } from 'react-redux';

import AppContainer from './containers/AppContainer';
import CreateUserContainer from './containers/CreateUserContainer';
import LoginContainer from './containers/LoginContainer';
import TrendingPulseSubscriptionsContainer from './containers/TrendingPulseSubscriptionsContainer';

import indexStore from './indexStore';

// Render the app component into the dom
render((
  <Provider store={indexStore}>
    <Router history={browserHistory}>
      <Route path="/" component={AppContainer}>
        <IndexRoute component={TrendingPulseSubscriptionsContainer} />
        <Route path="createUser" component={CreateUserContainer} />
        <Route path="login" component={LoginContainer} />
      </Route>
    </Router>
  </Provider>
), document.getElementById('app'));
