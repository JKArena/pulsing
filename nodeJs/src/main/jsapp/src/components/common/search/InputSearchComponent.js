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

require('./Map.scss');

import React, {Component} from 'react';
import {FormGroup, InputGroup, FormControl, Glyphicon} from 'react-bootstrap';

import ElasticSearchDjangoStore from './store/ElasticSearchDjangoStore';

const KEY_STORE_MAPPER = Object.freeze(
  {
    __proto__: null,
    'elasticSearch': () => { return GMapPulseStore; }
  }
);

class InputSearchComponent extends Component {

  constructor(props) {
    super(props);
    
    this.store = KEY_STORE_MAPPER[props.params.store](props.params.index);

  }

  componentDidMount() {
    
  }

  componentWillUnmount() {
    this.store = null;

  }

  render() {
    
    return (
      <div className='inputsearch-component'>
        <InputGroup>
          <FormControl type="text" />
          <InputGroup.Addon>
            <Glyphicon glyph="globe" />
          </InputGroup.Addon>
        </InputGroup>
      </div>
    );
  }
}

InputSearchComponent.displayName = 'InputSearchComponent';

export default InputSearchComponent;
