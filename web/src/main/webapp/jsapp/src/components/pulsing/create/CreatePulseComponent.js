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
import {TOPICS, API} from '../../../common/PubSub';
import Common from '../../../common/Common';
import AbstractComponent from '../../AbstractComponent';
import CreatePulseAction from './actions/CreatePulseAction';

class CreatePulseComponent extends AbstractComponent {
  
  constructor(props) {
    super(props);

    this.state = {
      validity: {
        value: 0,
        description: 0
      },
      errorMsg: ''
    };
  }

  handleSubmit(evt) {
    console.debug('submitting pulse', evt);

    CreatePulseAction.createPulse('createPulseBtn', 'createPulseForm', this.refs.tagsComp.getData())
      .then(() => {
        //save the user and update the store
        this.state.errorMsg = '';

        API.publish(TOPICS.NAVIGATION_CHANGE, Common.MAIN_NAV_PATH);
      })
      .catch(message => {
        this.state.errorMsg = message;
        this.setState(this.state);
      });
  }

  render() {
    return (
      <div className='createpulse-component'>
        <Grid>
            <Row>
              <Col sm={12}>
                <h1>Create Pulse</h1>
              </Col>
              <Col sm={12}>
                <form class='form' id='createPulseForm' action=''>
                  <FormGroup controlId='value' validationState={this.getValidState('value')}>
                    <ControlLabel>Name</ControlLabel>
                    <FormControl type='text' name='value' onBlur={this.handleChange.bind(this)} />
                    <FormControl.Feedback />
                  </FormGroup>

                  <FormGroup controlId='description' validationState={this.getValidState('description')}>
                    <ControlLabel>Description</ControlLabel>
                    <FormControl componentClass='textarea' name='description' onBlur={this.handleChange.bind(this)} />
                    <FormControl.Feedback />
                  </FormGroup>

                  <PillsComponent label='Tags' ref='tagsComp' />
                  
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
