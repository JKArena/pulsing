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

import { Grid, Row, Col, FormGroup, ControlLabel, FormControl, HelpBlock, Button, Image, Panel } from 'react-bootstrap';

const CreateUserView = props =>
  (<div className="createuser-component">
    <form className="form" id="createUserForm" action="">
      <Grid>
        <Row>
          <Col sm={12} md={12}>
            <h1>Sign up</h1>
          </Col>
        </Row>
        <Row>
          <Col sm={12} md={4}>
            <FormGroup controlId="avatar">
              <ControlLabel>Picture</ControlLabel>
              <div>
                <Image
                  id="avatar"
                  rounded
                  src="/images/dropzone.png"
                  style={{ maxHeight: '300px' }}
                />
                <FormControl.Feedback />
              </div>
            </FormGroup>
          </Col>

          <Col sm={12} md={8}>
            <FormGroup controlId="name" validationState={props.getValidState('name')}>
              <ControlLabel>Name</ControlLabel>
              <FormControl
                type="text"
                name="name"
                onBlur={props.handleChange}
              />
              <FormControl.Feedback />
            </FormGroup>

            <FormGroup controlId="email" validationState={props.getValidState('email')}>
              <ControlLabel>Email</ControlLabel>
              <FormControl
                type="email"
                name="email"
                onBlur={props.handleChange}
                placeholder="foobar@email.com"
              />
              <FormControl.Feedback />
            </FormGroup>

            <FormGroup controlId="password" validationState={props.getValidState('password')}>
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
          </Col>
        </Row>
        <Row>
          <Col sm={12} md={12}>
            {(() => {
              const errorView = props.errorMessage ?
                (<div>
                  <Panel
                    header="Signup Error"
                    bsStyle="danger"
                  >
                    {props.errorMessage}
                  </Panel>
                </div>) : null;
              return errorView;
            })()}

            <hr />

            <div>
              <Button
                id="createUserBtn"
                bsSize="large"
                bsStyle="primary"
                block
                onClick={props.handleSubmit}
              >
                | Signup
              </Button>
            </div>
          </Col>
        </Row>
      </Grid>
    </form>
  </div>);

CreateUserView.propTypes = {
  errorMessage: React.PropTypes.string.isRequired,
  getValidState: React.PropTypes.func.isRequired,
  handleChange: React.PropTypes.func.isRequired,
  handleSubmit: React.PropTypes.func.isRequired,
};

export default CreateUserView;
