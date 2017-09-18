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

import { Component } from 'react';

class AbstractFormContainer extends Component {

  constructor(props) {
    super(props);

    this.handleChange = this.onHandleChange.bind(this);
    this.handleSubmit = this.onHandleSubmit.bind(this);
    this.getValidState = this.onGetValidState.bind(this);
  }

  onHandleChange(evt) {
    console.debug('onHandleChange ', evt);

    const target = evt.target;
    const eleId = target.id;
    const oldState = this.state.validity[eleId];

    this.state.validity[eleId] = target.value.trim().length > 0 ? 1 : -1;
    if (oldState !== this.state.validity[eleId]) {
      this.setState(this.state);
    }
  }

  onHandleSubmit() {
    console.debug('checking validity');

    const validity = this.state.validity;
    if (!validity) {
      return true;
    }

    // just to remember "of" loop, guess only time to use this loop
    // is when one wishes to break + return from (otherwise forEach)
    const msg = [];
    Object.keys(validity).forEach((key) => {
      if (validity[key] !== 1) {
        msg.push(key);
      }
    });

    if (msg.length > 0) {
      this.state.errorMessage = ['Fields: ', msg.join(', '), ' invalid.'].join('');
      this.setState(this.state);
      return false;
    }

    return true;
  }

  onGetValidState(elementId) {
    let state;

    switch (this.state.validity[elementId]) {
      case 0: break;
      case 1: state = 'success'; break;
      case -1: state = 'error'; break;
      default: state = 'identity crisis...'; break;
    }

    return state;
  }
}

export default AbstractFormContainer;
