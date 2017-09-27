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

import React from 'react';

import { Grid, Row, Col, FormGroup, ControlLabel, FormControl, HelpBlock, Button, Panel } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';

const LoginView = props =>
  (<div className="login-component">
    <Grid>
      <Row>
        <Col sm={12}>
          <h1>Login | Register</h1>
        </Col>
        <Col sm={12}>
          <form className="form" id="loginform" action="">
            <FormGroup
              controlId="email"
              validationState={props.getValidState('email')}
            >
              <ControlLabel>Email</ControlLabel>
              <FormControl
                type="email"
                name="email"
                onBlur={props.handleChange}
                placeholder="foobar@email.com"
              />
              <FormControl.Feedback />
            </FormGroup>

            <FormGroup
              controlId="password"
              validationState={props.getValidState('password')}
            >
              <ControlLabel>Password</ControlLabel>
              <FormControl
                type="password"
                name="password"
                onBlur={props.handleChange}
                placeholder="wsad"
              />
              <FormControl.Feedback />
              <HelpBlock>wsad best password</HelpBlock>
            </FormGroup>

            {(() => {
              const errorView = props.errorMessage ?
                (<div>
                  <Panel
                    header="Login Error"
                    bsStyle="danger"
                  >
                    {props.errorMessage}
                  </Panel>
                </div>) : null;
              return errorView;
            })()}

            <div>
              <Button
                id="loginBtn"
                bsSize="large"
                bsStyle="primary"
                block
                onClick={props.handleSubmit}
              >
                | Login
              </Button>
              <LinkContainer to="/signup">
                <Button
                  bsSize="large"
                  block
                >
                  | Signup
                </Button>
              </LinkContainer>
            </div>

            <hr />

            <div>
              <Button
                id="oauthFacebook"
                bsSize="large"
                block
                onClick={props.onLogInOauth}
              >
                | Login with Facebook
              </Button>
            </div>
          </form>
        </Col>
      </Row>
    </Grid>
  </div>);

LoginView.propTypes = {
  errorMessage: React.PropTypes.string.isRequired,
  getValidState: React.PropTypes.func.isRequired,
  handleChange: React.PropTypes.func.isRequired,
  handleSubmit: React.PropTypes.func.isRequired,
  onLogInOauth: React.PropTypes.func.isRequired,
};

export default LoginView;
