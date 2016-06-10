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

require('./Login.scss');

import {Grid, Row, Col, FormGroup, ControlLabel, FormControl, HelpBlock, Button} from 'react-bootstrap';
import {LinkContainer} from 'react-router-bootstrap';
import {browserHistory} from 'react-router';
import React from 'react';

import AbstractComponent from '../AbstractComponent';
import LoginAction from './actions/LoginAction';

class LoginComponent extends AbstractComponent {
  
  constructor(props) {
    super(props);
    
    this.state = {
        email: {
          state: 0, //-1 invalid, 0 initial, 1 valid
          value: ''
        },
        password: {
          state: 0,
          value: ''
        }
    };
  }
  
  handleSubmit() {
    console.info('logging in');
    const path = '/';
    
    LoginAction.loginUser('loginBtn', 'loginform')
      .then(function(user) {
        //save the user and update the store
        browserHistory.push(path);
      })
      .catch(function(message) {
        console.error(message);
        //display message
      })
  }
  
  loginOauth(evt) {
    console.info('oauthType ', evt.target.id);
  }
  
  render() {
    
    return (
        <div class='login-component'>
          <Grid>
            <Row>
              <Col sm={12}>
                <h1>Login | Register</h1>
              </Col>
              <Col sm={12}>
                <form class='form' id='loginform' action=''>
                  <FormGroup controlId='email' validationState={this.getValidState('email')}>
                    <ControlLabel>Email</ControlLabel>
                    <FormControl type='email' name='email' onBlur={this.handleChange.bind(this)}
                      placeholder='foobar@email.com' />
                    <FormControl.Feedback />
                  </FormGroup>
                  
                  <FormGroup controlId='password' validationState={this.getValidState('password')}>
                    <ControlLabel>Password</ControlLabel>
                    <FormControl type='password' name='password' onBlur={this.handleChange.bind(this)}
                      placeholder='wsad' />
                    <FormControl.Feedback />
                    <HelpBlock>wsad best password</HelpBlock>
                  </FormGroup>
                  
                  <div>
                    <Button id='loginBtn' bsSize='large' bsStyle='primary' block onClick={this.handleSubmit.bind(this)}>| Login</Button>
                    <LinkContainer to='/signup'><Button bsSize='large' block>| Signup</Button></LinkContainer>
                  </div>
                  
                  <hr />
                  
                  <div>
                    <Button id='oauthFacebook' bsSize='large' block onClick={this.loginOauth.bind(this)}>| Login with Facebook</Button>
                  </div>
                </form>
              </Col>
            </Row>
          </Grid>
        </div>
    );
  }
}

LoginComponent.displayName = 'LoginComponent';

export default LoginComponent;
