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
import HTML5Backend from 'react-dnd-html5-backend';
import { DragDropContext } from 'react-dnd';
import { connect } from 'react-redux';
import * as types from '../common/storageTypes';
import * as authActions from './actions/auth';

import AppView from '../views/AppView';

class AppContainer extends React.Component {
  render() {
    return (
      <AppView user={this.props.user} onCreateUser={this.props.onCreateUser} onLogOut={this.props.onLogOut}
        onLogIn={this.props.onLogIn} />
    );
  }
}

export function mapStateToProps(state) {
  return {
    user: state.auth.user,
  };
}

export function mapDispatchToProps(dispatch) {
  return {
    onCreateUser: (btnId, formId) => {
      dispatch(authActions.createUser(btnId, formId));
    },
    onLogIn: (btnId, formId) => {
      dispatch(authActions.logIn(btnId, formId));
    },
    onLogOut: (btnId, formId) => {
      dispatch(authActions.logOut(btnId, formId));
    },
  };
}

AppContainer.propTypes = {
  user: React.PropTypes.object,
  onCreateUser: React.PropTypes.func,
  onLogIn: React.PropTypes.func,
  onLogOut: React.PropTypes.func,
};

export default DragDropContext(HTML5Backend)(
  connect(mapStateToProps, mapDispatchToProps)(AppContainer));
