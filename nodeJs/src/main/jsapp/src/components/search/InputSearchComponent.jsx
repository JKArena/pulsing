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

require('./InputSearch.scss');

import React, { PropTypes, Component } from 'react';
import { findDOMNode } from 'react-dom';
import { InputGroup, FormControl, Button, DropdownButton, MenuItem } from 'react-bootstrap';

const DOC_TYPE = Object.freeze(
  {
    __proto__: null,
    PULSE: { docType: 'pulse_tags', text: 'Pulse' },
    USER: { docType: 'user_tags', text: 'User' },
  }
);

class InputSearchComponent extends Component {

  constructor(props) {
    super(props);

    this.state = {
      title: props.docTypes[0].text,
      selectedDocType: props.docTypes[0].docType,
    };

    this.dataTypeSelectHandler = this.onDataTypeSelect.bind(this);
    this.searchHandler = this.onSearch.bind(this);
  }

  componentDidMount() {
    this.searchInputNode = findDOMNode(this.refs.searchInput);
  }

  onDataTypeSelect(eventKey) {
    console.debug('dataType selected', eventKey);
    this.setState({ selectedDocType: eventKey, selectedDocType: });
  }

  onSearch() {
    console.debug('onSearch');

    if (this.searchInputNode.value.length === 0) {
      return;
    }

    this.props.onSearch(this.searchInputNode.value);
  }

  render() {
    const menus = [];
    this.props.docTypes.forEach(function(ele) {
      menus.push(<MenuItem
        eventKey={ele.docType}
        key={`search-${ele.docType}`}
      >
        {ele.text}
      </MenuItem>);
    });

    return (
      <div className="inputsearch-component">
        <InputGroup>
          <InputGroup.Button>
            <DropdownButton
              title={this.state.title}
              bsStyle='primary'
              onSelect={this.dataTypeSelectHandler}>
              {menus}
            </DropdownButton>
          </InputGroup.Button>
          <FormControl type="text" ref="searchInput" />
          <InputGroup.Button>
            <Button
              onClick={this.searchHandler}
            >
              {this.props.trigger}
            </Button>
          </InputGroup.Button>
        </InputGroup>
      </div>
    );
  }
}

InputSearchComponent.propTypes = {
  index: PropTypes.string.isRequired,
  docTypes: PropTypes.arrayOf(
    PropTypes.shape({
      docType: PropTypes.string,
      text: PropTypes.string,
    })
  ),
  trigger: PropTypes.string,
  onSearch: PropTypes.func.isRequired,
};

export { DOC_TYPE, InputSearchComponent };
