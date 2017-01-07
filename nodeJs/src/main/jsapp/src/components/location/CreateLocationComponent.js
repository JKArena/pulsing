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

import {Grid, Row, Col, FormGroup, ControlLabel, FormControl, HelpBlock, Button, Panel} from 'react-bootstrap';
import React from 'react';

import {TOPICS, API} from '../../common/PubSub';
import AbstractComponent from '../AbstractComponent';

class CreateLocationComponent extends AbstractComponent {

  constructor(props) {
    super(props);

    this.state = {
      validity: {
        name: 0, //-1 invalid, 0 initial, 1 valid
        lat: 0,
        lng: 0
      },
      errorMsg: ''
    };
  }
  
  handleSubmit(evt) {
    if(!super.handleSubmit(evt)) {
      return;
    }
    
    CreateLocationAction.createLocation('createLocationBtn', 'createLocationForm')
      .then(() => {
        this.state.errorMsg = '';
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

                  <FormGroup controlId='description' validationState={this.getValidState('description')}>
                    <ControlLabel>Description</ControlLabel>
                    <FormControl componentClass="textarea" name='description' onBlur={this.handleChange.bind(this)} />
                    <FormControl.Feedback />
                  </FormGroup>
                  
                  <FormGroup controlId='lat' validationState={this.getValidState('lat')}>
                    <ControlLabel>Latitude</ControlLabel>
                    <FormControl type='number' name='lat' onBlur={this.handleChange.bind(this)} />
                    <FormControl.Feedback />
                  </FormGroup>

                  <FormGroup controlId='lng' validationState={this.getValidState('lng')}>
                    <ControlLabel>Longitude</ControlLabel>
                    <FormControl type='number' name='lng' onBlur={this.handleChange.bind(this)} />
                    <FormControl.Feedback />
                  </FormGroup>
                  
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
