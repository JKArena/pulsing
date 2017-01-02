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

require('./Pills.scss');

import React, {Component, PropTypes} from 'react';
import {render, findDOMNode, unmountComponentAtNode} from 'react-dom';
import {FormGroup, ControlLabel, FormControl, InputGroup, Button, Panel} from 'react-bootstrap';

import {PillsWrapper, PillsDelete, TextPill} from './PillStateLess';

class PillsComponent extends Component {

  constructor(props) {
    super(props);
    
    this.tagPanelNode = null;
    this.tagInputNode = null;
    this.nextDataIndex = 0;
    this.data = new Set();
    this.dataNodes = {};
  }

  componentDidMount() {
    
    this.tagPanelNode = findDOMNode(this.refs.tagPanel).querySelector(':scope .panel-body');
    this.tagInputNode = findDOMNode(this.refs.tagInput);
  }

  getData() {
    return this.data;
  }

  handleAdd() {
    let val = this.tagInputNode.value;
    if(val.length === 0 || this.data.has(val)) {
      return;
    }
    this.data.add(val);
    this.addTag(val);
    this.tagInputNode.value = '';
  }

  clearData() {
    Object.keys(this.dataNodes).forEach(removeIndex => {
      this.removeTag(removeIndex, null);
    });
    this.dataNodes = {};
    this.data = new Set();
    this.tagInputNode.value = '';
  }

  removeTag(removeIndex) {
    console.debug('removeTag ', removeIndex);
    if(!(removeIndex in this.dataNodes)) {
      return; //unlikely to happen but for sanity
    }
    
    this.data.delete(this.dataNodes[removeIndex].querySelector(':scope .pills-value').innerHTML);
    unmountComponentAtNode(this.dataNodes[removeIndex]);
    delete this.dataNodes[removeIndex];
  }

  addTag(val) {
    let ele = document.createElement('span');
    let removeIndex = this.nextDataIndex++;

    this.tagPanelNode.appendChild(ele);
    this.dataNodes[removeIndex] = ele;

    render((<PillsWrapper>
        <TextPill value={val} />
        <PillsDelete clickHandler={this.removeTag.bind(this, removeIndex)} />
      </PillsWrapper>), ele);
  }

  render() {

    return (
      <div className='pills-component'>

        <FormGroup>
          <ControlLabel>{this.props.label}</ControlLabel>
          <InputGroup>
            <FormControl type='text' ref='tagInput' />
            <InputGroup.Button>
              <Button onClick={this.handleAdd.bind(this)}>Add</Button>
            </InputGroup.Button>
          </InputGroup>

          <Panel ref='tagPanel'>
          &nbsp;
          </Panel>
        </FormGroup>
        
      </div>
    );
  }
}

PillsComponent.displayName = 'PillsComponent';

PillsComponent.propTypes = {
  label: PropTypes.string.isRequired
};

export default PillsComponent;
