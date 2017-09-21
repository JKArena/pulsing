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

import User from '../avro/User';

import * as pulseActions from '../actions/pulse';

import TrendingPulseSubscriptionsView from '../views/TrendingPulseSubscriptionsView';

const TrendingPulseSubscriptionsContainer = props =>
  (<TrendingPulseSubscriptionsView
    subscribedPulseId={props.subscribedPulseId}
    trendingPulse={props.trendingPulse}
    onSubscribe={props.onSubscribe}
  />);

export function mapStateToProps(state) {
  return {
    user: state.auth.user,
    subscribedPulseId: state.pulse.subscribedPulseId,
    trendingPulse: state.trending.trendingPulse,
  };
}

export function mapDispatchToProps(dispatch) {
  return {
    onSubscribe: (evt) => {
      console.debug('onSubscribe', evt);
      dispatch(pulseActions.subscribePulse());
    },
  };
}

TrendingPulseSubscriptionsContainer.propTypes = {
  user: React.PropTypes.objectOf(User),
  subscribedPulseId: React.PropTypes.number,
  trendingPulse: React.PropTypes.objectOf(Map),
  onSubscribe: React.PropTypes.func.isRequired,
};

TrendingPulseSubscriptionsContainer.defaultProps = {
  user: null,
  subscribedPulseId: 0,
  trendingPulse: new Map(),
};

export default connect(mapStateToProps, mapDispatchToProps)(TrendingPulseSubscriptionsContainer);
