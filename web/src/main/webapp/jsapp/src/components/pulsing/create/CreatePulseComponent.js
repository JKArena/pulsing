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

require('./CreatePulse.scss');

import React from 'react';
import {FormGroup, ControlLabel, FormControl, Panel, Button, Grid, Row, Col} from 'react-bootstrap';

import PillsComponent from '../../common/pills/PillsComponent';
import AbstractComponent from '../../AbstractComponent';

class CreatePulseComponent extends AbstractComponent {
  
  constructor(props) {
    super(props);

    this.props.data = [];
  }

  getInitialState() {
    return {
      validity: {
        value: 0
      },
      errorMsg: ''
    };
  }

  handleSubmit(evt) {

  }

  render() {
    return (
      <div className='createpulse-component'>
        <Grid>
            <Row>
              <Col sm={12}>
                <h1>Login | Register</h1>
              </Col>
              <Col sm={12}>
                <form class='form' id='createPulseForm' action=''>
                  <FormGroup controlId='pulseValue' validationState={this.getValidState('value')}>
                    <ControlLabel>Name</ControlLabel>
                    <FormControl type='text' name='value' onBlur={this.handleChange.bind(this)} />
                    <FormControl.Feedback />
                  </FormGroup>

                  <FormGroup controlId='pulseTags'>
                    <ControlLabel>Name</ControlLabel>
                    <FormControl type='text' name='value' onBlur={this.handleChange.bind(this)} />
                    <FormControl.Feedback />
                  </FormGroup>

                  <PillsComponent type='Text' label='Tags' data={this.props.data} />
                  
                  {(() => {
                    
                    if(this.state.errorMsg) {
                      return <div>
                        <Panel header='Create Error' bsStyle='danger'>
                          {this.state.errorMsg}
                        </Panel>
                      </div>;
                    }
                    
                  })()}
                  
                  <div>
                    <Button id='createPulseBtn' bsSize='large' bsStyle='primary' block onClick={this.handleSubmit.bind(this)}>| Create</Button>
                  </div>

                </form>
              </Col>
            </Row>
          </Grid>
      </div>
    );
  }
}

CreatePulseComponent.displayName = 'CreatePulseComponent';

export default CreatePulseComponent;
