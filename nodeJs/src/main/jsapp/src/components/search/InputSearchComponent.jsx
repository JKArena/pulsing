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

import React, { PropTypes, Component } from 'react';
import { InputGroup, FormControl, Button, DropdownButton, MenuItem } from 'react-bootstrap';

require('./InputSearch.scss');

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

  onDataTypeSelect(eventKey) {
    console.debug('dataType selected', eventKey);

    const entry = this.props.docTypes.filter(value => value.docType === eventKey);

    this.setState({ title: entry.title, selectedDocType: eventKey });
  }

  onSearch() {
    console.debug('onSearch', this.searchInputNode);
    if (this.searchInputNode.value.length === 0) {
      return;
    }

    this.props.onSearch(this.props.index,
      this.state.selectedDocType, this.searchInputNode.value);
  }

  render() {
    const menus = [];
    this.props.docTypes.forEach((ele) => {
      menus.push(<MenuItem
        eventKey={ele.docType}
        key={`search-${ele.docType}`}
      >
        {ele.text}
      </MenuItem>);
    });

    const searchInputRef = (ele) => {
      this.searchInputNode = ele;
    };

    return (
      <div className="inputsearch-component">
        <InputGroup>
          <InputGroup.Button>
            <DropdownButton
              id="searchButton"
              title={this.state.title}
              bsStyle="primary"
              onSelect={this.dataTypeSelectHandler}
            >
              {menus}
            </DropdownButton>
          </InputGroup.Button>
          <FormControl type="text" inputRef={searchInputRef} />
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
    }),
  ),
  trigger: PropTypes.string,
  onSearch: PropTypes.func.isRequired,
};

InputSearchComponent.defaultProps = {
  docTypes: [],
  trigger: '',
};

export default InputSearchComponent;
