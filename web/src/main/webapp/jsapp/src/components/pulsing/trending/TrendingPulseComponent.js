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

import React, {Component} from 'react';
import TrendingPulseStore from './TrendingPulseStore';

const _store = new TrendingPulseStore();

class TrendingPulseComponent extends Component {
  
  constructor(props) {
    super(props);
    
    TrendingPulseStore.trending
      .then(function(result) {
        
        this.state = result;
      }, function(err) {
        
        console.error(err);
      }).bind(this);
  }
  
  componentDidMount() {
    _store.addChangeListener(this._onChange);
  }
  
  componentWillUnmount() {
    _store.removeChangeListener(this._onChange);
  }
  
  _onChange() {
    this.setState(TrendingPulseStore.trending);
  }
  
  render() {
    return (
      <div>
        <div>Trending</div>
      </div>
    );
  }
  
}

TrendingPulseComponent.displayName = 'TrendingPulseComponent';

TrendingPulseComponent.propTypes = {};
TrendingPulseComponent.defaultProps = {};

export default TrendingPulseComponent;