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

require('./CreateLocation.scss');

import {Grid, Row, Col, FormGroup, ControlLabel, FormControl, Button, Panel} from 'react-bootstrap';
import React from 'react';

import PillsComponent from '../../common/pills/PillsComponent';
import AbstractComponent from '../../AbstractComponent';
import CreateLocationAction from './actions/CreateLocationAction';

class CreateLocationComponent extends AbstractComponent {

  constructor(props) {
    super(props);

    this.state = {
      validity: {
        name: 0, //-1 invalid, 0 initial, 1 valid
        address: 0
      },
      errorMsg: ''
    };
  }
  
  handleSubmit(evt) {
    if(!super.handleSubmit(evt)) {
      return;
    }
    
    CreateLocationAction.createLocation('createLocationBtn', 'createLocationForm', this.refs.tagsComp.getData())
      .then(() => {
        this.state.errorMsg = '';
        this.refs.tagsComp.clearData();
      })
      .catch(message => {
        this.state.errorMsg = message;
        this.setState(this.state);
      });
  }
  
  render() {
    
    return (
        <div class='createlocation-component'>
          <Grid>
            <Row>
              <Col sm={12}>
                <h1>Create Location</h1>
              </Col>
              <Col sm={12}>
                <form class='form' id='createLocationForm' action=''>
                  <FormGroup controlId='name' validationState={this.getValidState('name')}>
                    <ControlLabel>Name</ControlLabel>
                    <FormControl type='text' name='name' onBlur={this.handleChange.bind(this)}
                      placeholder='Location Name' />
                    <FormControl.Feedback />
                  </FormGroup>

                  <FormGroup controlId='address' validationState={this.getValidState('address')}>
                    <ControlLabel>Address</ControlLabel>
                    <FormControl type='text' name='address' onBlur={this.handleChange.bind(this)} />
                    <FormControl.Feedback />
                  </FormGroup>

                  <FormGroup controlId='description'>
                    <ControlLabel>Description</ControlLabel>
                    <FormControl componentClass="textarea" name='description' />
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
                    <Button id='createLocationBtn' bsSize='large' bsStyle='primary' block onClick={this.handleSubmit.bind(this)}>Create</Button>
                  </div>

                </form>
              </Col>
            </Row>
          </Grid>
        </div>
    );
  }
}

CreateLocationComponent.displayName = 'CreateLocationComponent';

export default CreateLocationComponent;
