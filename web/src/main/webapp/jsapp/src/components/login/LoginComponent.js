'use strict';

require('./Login.scss');

import {Grid, Row, Col, FormGroup, ControlLabel, FormControl, HelpBlock, Button} from 'react-bootstrap';
import {LinkContainer} from 'react-router-bootstrap';
import React, {Component} from 'react';

class LoginComponent extends Component {
  
  constructor(props) {
    super(props);
    
    this.state = {
        email: {
          state: 0, //-1 invalid, 0 initial, 1 valid
          value: ''
        },
        password: {
          state: 0, //-1 invalid, 0 initial, 1 valid
          value: ''
        }
    };
  }
  
  handleSubmit() {
    console.info('logging in');
  }
  
  handleChange(evt) {
    console.info('handleChange ', evt);
  }
  
  getValidState(elementId) {
    console.info('elementId', elementId);
    
    switch(this.state[elementId].state) {
    case 0: return '';
    case 1: return 'success';
    case -1: return 'error';
    }
  }
  
  loginOauth(oauthType) {
    console.info('oauthType ', oauthType);
  }
  
  render() {
    
    return (
        <div class='login-component'>
          <Grid>
            <Row>
              <Col sm={12}>
                <h1>Login</h1>
              </Col>
              <Col sm={12}>
                <form class='form' name='loginform'>
                  <FormGroup controlId='email' validationState={this.getValidState('email')}>
                    <ControlLabel>Email</ControlLabel>
                    <FormControl type='email' pattern='[^ @]*@[^ @]' onChange={this.handleChange}
                      value={this.state.email.value} placeholder='foobar@email.com' />
                    <FormControl.Feedback />
                  </FormGroup>
                  
                  <FormGroup controlId='password' validationState={this.getValidState('password')}>
                    <ControlLabel>Password</ControlLabel>
                    <FormControl type='password' onChange={this.handleChange}
                      value={this.state.password.value} placeholder='wsad best' />
                    <FormControl.Feedback />
                    <HelpBlock>wsad best password</HelpBlock>
                  </FormGroup>
                  
                  {(() => {
                    if(this.state === -1) {
                      <FormGroup>
                        <p class='help-block'>
                          | Please enter your email and password.
                        </p>
                      </FormGroup>
                    }
                  })()}
                  
                  <div>
                    <Button bsSize='large' block type='submit' onClick={this.handleSubmit}>| Login</Button>
                    <LinkContainer to='/signup'><Button bsSize='large' block>| Register</Button></LinkContainer>
                  </div>
                  
                  <hr />
                  
                  <div>
                    <Button bsSize='large' block onClick={this.loginOauth('facebook')}>| Connect with Facebook</Button>
                  </div>
                </form>
              </Col>
            </Row>
            
            <hr />
          </Grid>
        </div>
    );
  }
}

LoginComponent.displayName = 'LoginComponent';

export default LoginComponent;
