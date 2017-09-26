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

import React, { PropTypes, Component } from 'react';

import { FormGroup, ControlLabel, FormControl, Panel, Button, Grid, Row, Col } from 'react-bootstrap';
import PillsComponent from '../components/pills/PillsComponent';

class CreatePulseView extends Component {

  constructor(props) {
    super(props);

    this.valueInputNode = null;
    this.descInputNode = null;
    this.tagComp = null;
  }

  render() {
    const valueInputRef = (ele) => {
      this.valueInputNode = ele;
    };

    const descInputRef = (ele) => {
      this.descInputNode = ele;
    };

    const tagCompRef = (ele) => {
      this.tagComp = ele;
    };

    return (
      <div className="createpulse-component">
        <Grid>
          <Row>
            <Col sm={12}>
              <h1>Create Pulse</h1>
            </Col>
            <Col sm={12}>
              <form className="form" id="createPulseForm" action="">
                <FormGroup
                  controlId="value"
                  validationState={this.props.getValidState('value')}
                >
                  <ControlLabel>Name</ControlLabel>
                  <FormControl
                    type="text"
                    inputRef={valueInputRef}
                    name="value"
                    onBlur={this.props.handleChange}
                  />
                  <FormControl.Feedback />
                </FormGroup>

                <FormGroup
                  controlId="description"
                  validationState={this.props.getValidState('description')}
                >
                  <ControlLabel>Description</ControlLabel>
                  <FormControl
                    componentClass="textarea"
                    inputRef={descInputRef}
                    name="description"
                    onBlur={this.props.handleChange}
                  />
                  <FormControl.Feedback />
                </FormGroup>

                <PillsComponent
                  label="Tags"
                  ref={tagCompRef}
                />

                {(() => {
                  const errorView = props.errorMessage ?
                    (<div>
                      <Panel header="Create Pulse Error" bsStyle="danger">
                        {props.errorMessage}
                      </Panel>
                    </div>) : null;
                  return errorView;
                })()}

                <div>
                  <Button
                    id="createPulseBtn"
                    bsSize="large"
                    bsStyle="primary"
                    block
                    onClick={this.props.handleSubmit}
                  >
                    | Create
                  </Button>
                </div>
              </form>
            </Col>
          </Row>
        </Grid>
      </div>
    );
  }
};

CreatePulseView.propTypes = {
  errorMessage: PropTypes.string.isRequired,
  getValidState: PropTypes.func.isRequired,
  handleChange: PropTypes.func.isRequired,
  handleSubmit: PropTypes.func.isRequired,
};

export default CreatePulseView;
