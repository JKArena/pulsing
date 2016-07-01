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

require('./TrendingPulse.scss');

import {Grid, Row, Col, Thumbnail, Button, Badge} from 'react-bootstrap';
import React, {Component} from 'react';
import TrendingPulseStore from './TrendingPulseStore';
import WebSockets from '../../../common/WebSockets';
import {TOPICS, API} from '../../../common/PubSub';
import Storage from '../../../common/Storage';

let _trending = new Map();

class TrendingPulseComponent extends Component {
  
  constructor(props) {
    super(props);
    
    this.state = {loggedIn: !!Storage.user};
  }
  
  componentDidMount() {
    console.debug('mounted');
    
    this.store = new TrendingPulseStore();
    this.store.addFetchedListener(this._onFetched.bind(this));
    this.store.fetchTrending();
    
    this.ws = new WebSockets('pulseSubscribeSocketJS');
    this.ws.connect()
      .then(frame => {
        console.debug('frame', frame);
        this.sub = this.ws.subscribe('/pulsingTopic/pulseSubscribe', this._onPulseSubscribe.bind(this));
      });
    
    API.subscribe(TOPICS.AUTH, this._onAuth.bind(this));
  }
  
  componentWillUnmount() {
    console.debug('unmounted');
    
    this.store.removeFetchedListener(this._onFetched.bind(this));
    this.store = null;
    
    this.ws.destroy();
    this.sub.unsubscribe();
    
    this.sub = null;
    this.ws = null;
    
    API.unsubscribe(TOPICS.AUTH, this._onAuth.bind(this));
  }
  
  handleSubscribe(evt) {
    
    let user = Storage.user;
    
    console.debug('handleSubscribe', evt.target.id, user.id);
    this.ws.send('/pulsingSocket/pulseSubscribeSocketJS', {}, JSON.stringify({pulseId: evt.target.id, userId: user.id.id}));
  }
  
  _onAuth(auth) {
    this.state.loggedIn = auth.loggedIn;
    this.setState(this.state);
  }
  
  _onPulseSubscribe(pulse) {
    console.debug('_onPulseSubscribe', pulse);
    
  }
  
  _onFetched(trending) {
    console.debug('_onFetched', trending);
    
    _trending = trending;
    this.setState({});
  }
  
  render() {
    let trending = _trending;
    let cols = [];
    let loggedIn = this.state.loggedIn;
    
    trending.forEach((value, key) => {
      
      cols.push(<Col xs={12} sm={6} md={4} lg={3} key={key}>
        <Thumbnail>
          <h3>{value} <span><Badge>1</Badge></span></h3>
          <p>Description</p>
          <p>
            {(() => {
              if(loggedIn) {
                return <Button bsStyle="primary" id={key} onClick={this.handleSubscribe.bind(this)}>Subscribe</Button>;
              }
            })()}
          </p>
        </Thumbnail>
      </Col>);
    });
    
    return (
      <div class='trendingpulse-component'>

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

TrendingPulseComponent.displayName = 'TrendingPulseComponent';

TrendingPulseComponent.propTypes = {};
TrendingPulseComponent.defaultProps = {};

export default TrendingPulseComponent;
