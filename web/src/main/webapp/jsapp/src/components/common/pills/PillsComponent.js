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

import React, {Component, Children, PropTypes} from 'react';
import {FormGroup, ControlLabel, FormControl, InputGroup, Button} from 'react-bootstrap';

import PillTypes from './PillTypes';

class PillsComponent extends Component {

  constructor(props) {
    super(props);

  }

  render() {

    return (
      <div className='pills-component'>

        <FormGroup>
          <ControlLabel>{this.props.label}</ControlLabel>
          <InputGroup>
            <FormControl type='text' />
            <InputGroup.Button>
              <Button>Add</Button>
            </InputGroup.Button>
          </InputGroup>
          <Panel>
            Tag area
          </Panel>
        </FormGroup>
        
      </div>
    );
  }
}

PillsComponent.displayName = 'CommonPillsPillsComponent';

PillsComponent.propTypes = {
  type: PropTypes.string,
  label: PropTypes.string.isRequired,
  data: PropTypes.array.isRequired
};
PillsComponent.defaultProps = {
  type: 'Text'
};

export default PillsComponent;
