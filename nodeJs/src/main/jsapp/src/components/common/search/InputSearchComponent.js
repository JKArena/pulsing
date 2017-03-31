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

require('./InputSearch.scss');

import React, {Component} from 'react';
import {findDOMNode} from 'react-dom';
import {InputGroup, FormControl, Button} from 'react-bootstrap';

import {STORE_EVENT} from './store/AbstractSearchStore';
import ElasticSearchDjangoStore from './store/ElasticSearchDjangoStore';

const KEY_STORE_MAPPER = Object.freeze(
  {
    __proto__: null,
    'elasticSearch': ElasticSearchDjangoStore
  }
);

class InputSearchComponent extends Component {

  constructor(props) {
    super(props);
    
    this.store = new KEY_STORE_MAPPER[props.store](props.index);
    this.defaultDocType = props.docType;

    this.searchResultHandler = this.searchResult.bind(this);
    this.store.on(STORE_EVENT.SEARCH, this.searchResultHandler);
  }

  componentDidMount() {
    this.searchInputNode = findDOMNode(this.refs.searchInput);
  }

  componentWillUnmount() {
    if(this.store !== null) {
      this.store.removeListener(STORE_EVENT.SEARCH, this.searchResultHandler);
    }

    this.store = null;
  }

  searchResult(data) {
    console.debug('searchResult ', data);

  }

  handleSearch(evt) {
    if(!this.searchInputNode.value) return;

    this.store.search(this.defaultDocType, {'query': {'term': {'name': {'boost': 3.0, 'value': this.searchInputNode.value }}}});
  }

  render() {
    
    return (
      <div className='inputsearch-component'>
        <InputGroup>
          <FormControl type="text" ref='searchInput' />
          <InputGroup.Button>
            <Button onClick={this.handleSearch.bind(this)}>{this.props.trigger}</Button>
          </InputGroup.Button>
        </InputGroup>
      </div>
    );
  }
}

InputSearchComponent.displayName = 'InputSearchComponent';

export default InputSearchComponent;
