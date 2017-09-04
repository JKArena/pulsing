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

import React, {Component} from 'react';
import { connect } from 'react-redux';

import NavView from '../views/NavView';

class NavBarContainer extends Component {

  constructor(props) {
    super(props);

    this.state = {loggedIn: !!Storage.user, lat: 0, lng: 0, alerts: 0};
    this.authHandler = this.onAuth.bind(this);
    this.navigationChangeHandler = this.onNavigationChange.bind(this);
    this.alertHandler = this.onAlert.bind(this);
  }

};

export function mapStateToProps(state) {
  return {
    user: state.auth.user,
    geo: state.geo,
  };
}

export function mapDispatchToProps(dispatch) {
  return {
    onCreateUser: (btnId, formId, pictureId) => {
      dispatch(authActions.createUser(btnId, formId, pictureId));
    },
    onLogIn: (btnId, formId) => {
      dispatch(authActions.logIn(btnId, formId));
    },
    onLogOut: () => {
      dispatch(authActions.logOut());
    },
  };
}

AppContainer.propTypes = {
  user: React.PropTypes.objectOf(User).isRequired,
  geo: React.PropTypes.shape.isRequired({
    user: React.PropTypes.shape({
      lat: React.PropTypes.number,
      lng: React.PropTypes.number,
    }),
    pulse: React.PropTypes.shape({
      lat: React.PropTypes.number,
      lng: React.PropTypes.number,
    }),
  }),
  onCreateUser: React.PropTypes.func.isRequired,
  onLogIn: React.PropTypes.func.isRequired,
  onLogOut: React.PropTypes.func.isRequired,
};

export default connect(mapStateToProps, mapDispatchToProps)(NavBarContainer);
