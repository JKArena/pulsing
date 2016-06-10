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

require('./Signup.scss');

import {Grid, Row, Col, FormGroup, ControlLabel, FormControl, HelpBlock, Button, Image} from 'react-bootstrap';
import React from 'react';

import AbstractComponent from '../AbstractComponent';
import Common from '../../common/Common';

class SignupComponent extends AbstractComponent {
  
  constructor(props) {
    super(props);
    
    this.state = {
        validity: {
          email: 0, //-1 invalid, 0 initial, 1 valid
          password: 0,
          name: 0,
          address: 0,
          picture: 0
        },
        signupErrorMsg: ''
    };
  }
  
  componentDidMount() {
    let dropcontainer = document.getElementById('picture');
    
    dropcontainer.addEventListener('dragenter', Common.eventCanceller, false);
    dropcontainer.addEventListener('dragover', Common.eventCanceller, false);
    dropcontainer.addEventListener('drop', this.handleDrop, false);
  }
  
  componentWillUnmount() {
    let dropcontainer = document.getElementById('picture');
    
    dropcontainer.removeEventListener('dragenter', Common.eventCanceller);
    dropcontainer.removeEventListener('dragover', Common.eventCanceller);
    dropcontainer.removeEventListener('drop', this.handleDrop);
  }
  
  handleDrop(evt) {
    Common.eventCanceller(evt);
    
    let dt = evt.dataTransfer;
    let file = dt.files[0];

    if(!/^image\//.test(file.type)) {
      this.state.picture.state = -1;
      return;
    }
    
    let preview = document.getElementById('picture');
    preview.file = file;
    
    let reader = new FileReader();
    reader.onload = e => {preview.src = e.target.result};
    reader.readAsDataURL(file);
  }
  
  handleSubmit() {
    console.info('signing up');
    
  }
  
  render() {
    
    return (
        <div class='signup-component'>
          <Grid>
            <Row>
              <Col sm={12}>
                <h1>Sign up</h1>
              </Col>
              <Col sm={12}>
                <form class='form' id='signupform' action=''>
                  <FormGroup controlId='name' validationState={this.getValidState('name')}>
                    <ControlLabel>Name</ControlLabel>
                    <FormControl type='text' name='name' onBlur={this.handleChange.bind(this)} />
                    <FormControl.Feedback />
                  </FormGroup>
                  
                  <FormGroup controlId='email' validationState={this.getValidState('email')}>
                    <ControlLabel>Email</ControlLabel>
                    <FormControl type='email' name='email' onBlur={this.handleChange.bind(this)}
                      placeholder='foobar@email.com' />
                    <FormControl.Feedback />
                  </FormGroup>
                  
                  <FormGroup controlId='password' validationState={this.getValidState('password')}>
                    <ControlLabel>Password</ControlLabel>
                    <FormControl type='password' name='password' onBlur={this.handleChange.bind(this)} placeholder='wsad' />
                    <FormControl.Feedback />
                    <HelpBlock>wsad best password</HelpBlock>
                  </FormGroup>
                  
                  <FormGroup controlId='address' validationState={this.getValidState('address')}>
                    <ControlLabel>Name</ControlLabel>
                    <FormControl type='text' name='address' onBlur={this.handleChange.bind(this)} />
                    <FormControl.Feedback />
                  </FormGroup>
                  
                  <FormGroup controlId='picture' validationState={this.getValidState('picture')}>
                    <ControlLabel>Picture</ControlLabel>
                    <div>
                      <Image id='picture' rounded src='/images/dropzone.png' style={{'maxHeight': '300px'}} />
                      <FormControl.Feedback />
                    </div>
                  </FormGroup>
                  
                  {(() => {
                    
                    if(this.state.signupErrorMsg) {
                      return <div>
                        <Panel header='Signup Error' bsStyle='danger'>
                          {this.state.signupErrorMsg}
                        </Panel>
                      </div>;
                    }
                    
                  })()}
                  
                  <hr />
                  
                  <div>
                    <Button id='signupBtn' bsSize='large' bsStyle='primary' block type='submit'
                      onClick={this.handleSubmit.bind(this)}>| Signup</Button>
                  </div>
                  
                </form>
              </Col>
            </Row>
          </Grid>
        </div>
    );
  }
}

SignupComponent.displayName = 'SignupComponent';

export default SignupComponent;
