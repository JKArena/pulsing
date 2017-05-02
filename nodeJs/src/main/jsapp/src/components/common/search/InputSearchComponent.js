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

import DropDownButtonComponent from '../dropDownButton/DropDownButtonComponent';
import {STORE_EVENT} from './store/AbstractSearchStore';
import ElasticSearchDjangoStore from './store/ElasticSearchDjangoStore';

const KEY_STORE_MAPPER = Object.freeze(
  {
    __proto__: null,
    'elasticSearch': ElasticSearchDjangoStore
  }
);

const DOC_TYPE = Object.freeze(
  {
    __proto__: null,
    'PULSE': {'docType': 'pulse_tags', 'text': 'Pulse'},
    'USER': {'docType': 'user_tags', 'text': 'User'}
  }
);

class InputSearchComponent extends Component {

  constructor(props) {
    super(props);
    
    this.store = new KEY_STORE_MAPPER[props.store](props.index, props.pathPrefix);
    this.docTypes = props.docTypes || [];
    this.state = {selectedType: this.docTypes[0]};

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

  handleDataTypeSelect(eventKey) {
    console.debug('handleDataTypeSelect', eventKey);
    
    this.state.selectedType = eventKey;
  }

  handleSearch() {
    if(!this.searchInputNode.value) return;

    this.store.search(this.state.selectedType, {'term': {'name': {'boost': 3.0, 'value': this.searchInputNode.value }}});
    //this.store.search(this.state.selectedType, {'match_all': {}});
  }

  render() {
    
    const menus = {};
    this.docTypes.forEach(function(ele) {
      menus[ele.docType] = ele.title;
    });

    return (
      <div className='inputsearch-component'>
        <InputGroup>
          <InputGroup.Button>
            <DropDownButtonComponent title={DOC_TYPE[this.state.selectedType]} menus={menus}
              onSelect={this.handleDataTypeSelect.bind(this)}/>
            }
          </InputGroup.Button>
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

export {InputSearchComponent, DOC_TYPE};
