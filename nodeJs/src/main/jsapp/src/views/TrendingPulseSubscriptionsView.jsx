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

import { Grid, Row, Col, Thumbnail, Button, Badge } from 'react-bootstrap';

import User from '../avro/User';

import { DOC_TYPE, InputSearchComponent } from '../components/search/InputSearchComponent';

const ES_DOC_TYPES = [DOC_TYPE.PULSE, DOC_TYPE.USER];

const TrendingPulseSubscriptionsView = (props) => {
  const cols = [];

  props.trendingPulse.forEach((value, key) => {
    const trendingKey = ['trending-', key].join('');
    cols.push(<Col xs={12} sm={6} md={4} lg={3} key={`${trendingKey}-col`}>
      <Thumbnail>
        <h3>{value} <span><Badge>1</Badge></span></h3>
        <p>
          {(() => {
            const subscribeView = props.user && props.subscribedPulseId !== key ?
              (<Button
                bsStyle="primary"
                id={`${trendingKey}-button`}
                onClick={props.onSubscribe}
              >
                Subscribe
              </Button>) : null;
            return subscribeView;
          })()}
        </p>
      </Thumbnail>
    </Col>);
  });

  return (<div className="trendingpulse-component">
    <InputSearchComponent
      store="elasticSearch"
      index="pulse"
      docTypes={ES_DOC_TYPES}
      trigger="Search Pulse"
    />
    <Grid>
      <Row>
        {cols}
      </Row>
    </Grid>
  </div>);
};

TrendingPulseSubscriptionsView.propTypes = {
  user: React.PropTypes.objectOf(User),
  subscribedPulseId: React.PropTypes.number,
  trendingPulse: React.PropTypes.objectOf(Map),
  onSubscribe: React.PropTypes.func.isRequired,
};

TrendingPulseSubscriptionsView.defaultProps = {
  user: null,
  subscribedPulseId: 0,
  trendingPulse: new Map(),
};

export default TrendingPulseSubscriptionsView;
