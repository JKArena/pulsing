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

require('./TrendingPulseSubscriptions.scss');

import {Grid, Row, Col, Thumbnail, Button, Badge} from 'react-bootstrap';
import React, {Component} from 'react';

import TrendingPulseSubscriptionsStore from './TrendingPulseSubscriptionsStore';
import {TOPICS, API} from '../../../common/PubSub';
import Storage from '../../../common/Storage';

import {DOC_TYPE, InputSearchComponent} from '../../common/search/InputSearchComponent';

const ES_DOC_TYPES = [DOC_TYPE.PULSE, DOC_TYPE.USER];
let trending = new Map();

class TrendingPulseSubscriptionsComponent extends Component {
  
  constructor(props) {
    super(props);
    
    this.store = new TrendingPulseSubscriptionsStore();
    this.state = {loggedIn: !!Storage.user};
    this.authHandler = this.onAuth.bind(this);
    this.fetchHandler = this.onFetched.bind(this);
  }

  componentDidMount() {

    this.store.addFetchedListener(this.fetchHandler);
    this.store.fetchTrending();
    
    API.subscribe(TOPICS.AUTH, this.authHandler);
  }
  
  componentWillUnmount() {
    
    if(this.store) {
      this.store.removeFetchedListener(this.fetchHandler);
      this.store = null;
    }

    API.unsubscribe(TOPICS.AUTH, this.authHandler);
  }
  
  handleSubscribe(evt) {
    const user = Storage.user;

    console.debug('handleSubscribe', evt.target.id, user.id);
    
  }
  
  onAuth(auth) {
    this.state.loggedIn = auth.loggedIn;
    this.setState(this.state);
  }
  
  onFetched(fetched) {
    console.debug('onFetched', fetched);
    
    trending = fetched;
    this.setState({});
  }

  searchQueryHandle(inputValue) {
    //TODO: Improve later
    return {'term': {'name': {'value': inputValue }}};
  }
  
  render() {
    const cols = [];
    const loggedIn = this.state.loggedIn;
    const subbedPId = Storage.subscribedPulseId;
    
    trending.forEach((value, key) => {
      
      cols.push(<Col xs={12} sm={6} md={4} lg={3} key={key}>
        <Thumbnail>
          <h3>{value} <span><Badge>1</Badge></span></h3>
          <p>
            {(() => {
              if(loggedIn && subbedPId !== key) {
                return <Button bsStyle='primary' id={key} onClick={this.handleSubscribe.bind(this)}>Subscribe</Button>;
              }
            })()}
          </p>
        </Thumbnail>
      </Col>);
    });

    return (
      <div className='trendingpulse-component'>
        <InputSearchComponent store='elasticSearch' index='pulse' docTypes={ES_DOC_TYPES}
          searchQueryHandle={this.searchQueryHandle}
          trigger='Search Pulse' />
        
        {(() => {
          
          return <Grid>
            <Row>
              {cols}
            </Row>
          </Grid>
        })()}

      </div>
    );
  }
  
}

TrendingPulseSubscriptionsComponent.displayName = 'TrendingPulseSubscriptionsComponent';

TrendingPulseSubscriptionsComponent.propTypes = {};
TrendingPulseSubscriptionsComponent.defaultProps = {};

export default TrendingPulseSubscriptionsComponent;
