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

require('normalize.css/normalize.css');
require('bootstrap/dist/css/bootstrap.css');
require('./App.css');

import React from 'react';
import NavBarComponent from './navbar/NavBarComponent';
import {TOPICS, API} from './common/PubSub';

class AppComponent extends React.Component {

  constructor(props) {
    super(props);

    this.errorHandler = this.onError.bind(this);
  }

  componentDidMount() {
    API.subscribe(TOPICS.ERROR_MESSAGE, this.errorHandler);
  }

  componentWillUnmount() {
    API.unsubscribe(TOPICS.ERROR_MESSAGE, this.errorHandler);
  }

  /*
   * @param errorData json object with {error: err, additional : {msg: '', serverLog: false, args: []}
   */
  onError(errorData) {
    console.error('Error', errorData);

  }

  render() {
    return (
      <div>
        <NavBarComponent></NavBarComponent>
        
        {this.props.children}
      </div>
    );
  }
}

export default AppComponent;
