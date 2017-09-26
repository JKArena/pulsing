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

import React, { Component, PropTypes } from 'react';
import { render, unmountComponentAtNode } from 'react-dom';
import { FormGroup, ControlLabel, FormControl, InputGroup, Button, Panel } from 'react-bootstrap';

import { PillsWrapper, PillsDelete, TextPill } from './PillStateLess';

require('./Pills.scss');

class PillsComponent extends Component {

  constructor(props) {
    super(props);

    this.addHandler = this.onAdd.bind(this);
    this.nextDataIndex = 0;
    this.data = new Set();
    this.dataNodes = {};
  }

  onAdd() {
    const val = this.tagInputNode.value;
    if (val.length === 0 || this.data.has(val)) {
      return;
    }
    this.data.add(val);
    this.addTag(val);
    this.tagInputNode.value = '';
  }

  getData() {
    return this.data;
  }

  clearData() {
    Object.keys(this.dataNodes).forEach((removeIndex) => {
      this.removeTag(removeIndex, null);
    });
    this.dataNodes = {};
    this.data = new Set();
    this.tagInputNode.value = '';
  }

  removeTag(removeIndex) {
    console.debug('removeTag ', removeIndex);
    if (!(removeIndex in this.dataNodes)) {
      return; // unlikely to happen but for sanity
    }

    this.data.delete(this.dataNodes[removeIndex].querySelector(':scope .pills-value').innerHTML);
    unmountComponentAtNode(this.dataNodes[removeIndex]);
    delete this.dataNodes[removeIndex];
  }

  addTag(val) {
    const ele = document.createElement('span');
    const removeIndex = this.nextDataIndex;

    this.nextDataIndex = this.nextDataIndex + 1;
    this.tagPanelNode.appendChild(ele);
    this.dataNodes[removeIndex] = ele;

    const pillsDeleterHandler = this.removeTag.bind(this, removeIndex);

    render((<PillsWrapper>
      <TextPill value={val} />
      <PillsDelete clickHandler={pillsDeleterHandler} />
    </PillsWrapper>), ele);
  }

  render() {
    const tagInputRef = (ele) => {
      this.tagInputNode = ele.node;
    };
    const tagPanelRef = (ele) => {
      this.tagPanelNode = ele.node.querySelector(':scope .panel-body');
    };

    return (
      <div className="pills-component">
        <FormGroup>
          <ControlLabel>{this.props.label}</ControlLabel>
          <InputGroup>
            <FormControl type="text" inputRef={tagInputRef} />
            <InputGroup.Button>
              <Button onClick={this.addHandler}>Add</Button>
            </InputGroup.Button>
          </InputGroup>
          <Panel ref={tagPanelRef}>
            &nbsp;
          </Panel>
        </FormGroup>
      </div>
    );
  }
}

PillsComponent.displayName = 'PillsComponent';

PillsComponent.propTypes = {
  label: PropTypes.string.isRequired,
};

export default PillsComponent;
