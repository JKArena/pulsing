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

import utils from '../common/utils';
import * as authActions from '../actions/auth';

import AbstractFormContainer from './AbstractFormContainer';

import CreateUserView from '../views/CreateUserView';

class CreateUserContainer extends AbstractFormContainer {

  constructor(props) {
    super(props);

    this.state = {
      validity: {
        email: 0, // -1 invalid, 0 initial, 1 valid
        password: 0,
        name: 0,
      },
      errorMessage: '',
    };
  }

  componentDidMount() {
    const dropcontainer = document.getElementById('avatar');

    dropcontainer.addEventListener('dragenter', utils.eventCanceller, false);
    dropcontainer.addEventListener('dragover', utils.eventCanceller, false);
    dropcontainer.addEventListener('drop', this.onHandleDrop, false);
  }

  componentWillUnmount() {
    const dropcontainer = document.getElementById('avatar');

    dropcontainer.removeEventListener('dragenter', utils.eventCanceller);
    dropcontainer.removeEventListener('dragover', utils.eventCanceller);
    dropcontainer.removeEventListener('drop', this.onHandleDrop);
  }

  onHandleDrop(evt) {
    utils.eventCanceller(evt);

    const dt = evt.dataTransfer;
    const file = dt.files[0];

    if (!/^image\//.test(file.type)) {
      this.state.avatar.state = -1;
      return;
    }

    const preview = document.getElementById('avatar');
    preview.file = file;

    const reader = new FileReader();
    reader.onload = (event) => { preview.src = event.target.result; };
    reader.readAsDataURL(file);
  }

  onHandleSubmit(evt) {
    console.debug('signing up');
    if (super.onHandleSubmit(evt)) {
      // do submit
    }
  }

  render() {
    return (<CreateUserView
      errorMessage={this.state.errorMessage}
      onCreateUser={this.props.onCreateUser}
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
    onCreateUser: (btnId, formId, pictureId) => {
      dispatch(authActions.createUser(btnId, formId, pictureId));
    },
  };
}

CreateUserContainer.propTypes = {
  onCreateUser: React.PropTypes.func.isRequired,
};

export default connect(mapStateToProps, mapDispatchToProps)(CreateUserContainer);
