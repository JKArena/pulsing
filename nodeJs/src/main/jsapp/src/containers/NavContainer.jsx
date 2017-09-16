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

import React, { Component } from 'react';
import { connect } from 'react-redux';

import WebSocket from '../common/webSocket';

import User from '../avro/User';
import * as authActions from '../actions/auth';

import NavView from '../views/NavView';

class NavBarContainer extends Component {

  constructor(props) {
    super(props);

    this.alertHandler = this.onAlert.bind(this);
  }

  componentDidMount() {
    const user = this.props.user;
    if (user) {
      this.ws = new WebSocket('socket');
      this.ws.connect()
        .then((frame) => {
          console.debug('alert frame', frame);
          this.sub = this.ws.subscribe(['/topics/alert/', user.id.id].join(''), this.alertHandler);
        });
    }
  }

  componentWillUnmount() {
    this.recycleWS();
  }

  onAlert(alertMsg) {
    console.debug('alertMsg', alertMsg, this);
  }

  recycleWS() {
    if (this.ws) {
      this.ws.destroy();
      this.ws = null;
    }
  }

  render() {
    const props = this.props;
    return (<NavView
      user={props.user}
      geo={props.geo}
      onCreateUser={props.onCreateUser}
      onLogOut={props.onLogOut}
      onLogIn={props.onLogIn}
    />);
  }
}

export function mapStateToProps(state) {
  return {
    user: state.auth.user,
    geo: state.geo,
    alerts: state.app.alerts,
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

NavBarContainer.propTypes = {
  user: React.PropTypes.objectOf(User),
  geo: React.PropTypes.shape({
    user: React.PropTypes.shape({
      lat: React.PropTypes.number,
      lng: React.PropTypes.number,
    }),
    pulse: React.PropTypes.shape({
      lat: React.PropTypes.number,
      lng: React.PropTypes.number,
    }),
  }),
  alerts: React.PropTypes.arrayOf(
    React.PropTypes.shape({
      invitationId: React.PropTypes.string,
      invitationType: React.PropTypes.string,
      fromUserId: React.PropTypes.number,
      expiration: React.PropTypes.number,
    }),
  ),
  onCreateUser: React.PropTypes.func.isRequired,
  onLogIn: React.PropTypes.func.isRequired,
  onLogOut: React.PropTypes.func.isRequired,
};

NavBarContainer.defaultProps = {
  user: null,
  geo: null,
  alerts: [],
};

export default connect(mapStateToProps, mapDispatchToProps)(NavBarContainer);
