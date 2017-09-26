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

import { connect } from 'react-redux';

import * as pulseActions from '../actions/pulse';

import AbstractFormContainer from './AbstractFormContainer';
import CreatePulseView from '../views/CreatePulseView';

class CreatePulseContainer extends AbstractFormContainer {

  constructor(props) {
    super(props);

    this.state = {
      validity: {
        value: 0, // -1 invalid, 0 initial, 1 valid
        description: 0,
      },
      errorMessage: '',
    };
  }

  onHandleSubmit(evt) {
    console.debug('create pulse');
    if (super.onHandleSubmit(evt)) {
      this.props.onCreatePulse('createPulseBtn', 'createPulseForm', 'tags');
    }
  }

  render() {
    return (<CreatePulseView
      errorMessage={this.state.errorMessage}
      onCreatePulse={this.props.onCreateUser}
      handleChange={this.handleChange}
      handleSubmit={this.handleSubmit}
      getValidState={this.getValidState}
    />);
  }
}

export function mapStateToProps() {
  return {};
}

export function mapDispatchToProps(dispatch) {
  return {
    onCreatePulse: (btnId, formId, tags) => {
      dispatch(pulseActions.createPulse(btnId, formId, tags));
    },
  };
}

CreatePulseContainer.propTypes = {
  onCreatePulse: React.PropTypes.func.isRequired,
};

export default connect(mapStateToProps, mapDispatchToProps)(CreatePulseContainer);
