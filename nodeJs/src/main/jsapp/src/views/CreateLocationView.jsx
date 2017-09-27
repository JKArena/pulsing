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

import { Grid, Row, Col, FormGroup, ControlLabel, FormControl, Button, Panel } from 'react-bootstrap';
import PillsComponent from '../components/pills/PillsComponent';

class CreateLocationView extends Component {

  constructor(props) {
    super(props);

    this.valueInputNode = null;
    this.descInputNode = null;
    this.tagComp = null;
  }

  onSubmitClick(evt) {
    this.props.handleSubmit(evt, this.tagComp.getData());
  }
  render() {
    const tagCompRef = (ele) => {
      this.tagComp = ele;
    };

    return (
      <div className="createlocation-component">
        <Grid>
          <Row>
            <Col sm={12}>
              <h1>Create Location</h1>
            </Col>
            <Col sm={12}>
              <form className="form" id="createLocationForm" action="">
                <FormGroup
                  controlId="name"
                  validationState={this.props.getValidState('name')}
                >
                  <ControlLabel>Name</ControlLabel>
                  <FormControl
                    type="text"
                    name="name"
                    onBlur={this.props.handleChange}
                    placeholder="Location Name"
                  />
                  <FormControl.Feedback />
                </FormGroup>

                <FormGroup
                  controlId="address"
                  validationState={this.props.getValidState('address')}
                >
                  <ControlLabel>Address</ControlLabel>
                  <FormControl
                    type="text"
                    name="address"
                    onBlur={this.props.handleChange}
                  />
                  <FormControl.Feedback />
                </FormGroup>

                <FormGroup controlId="description">
                  <ControlLabel>Description</ControlLabel>
                  <FormControl
                    componentClass="textarea"
                    name="description"
                  />
                  <FormControl.Feedback />
                </FormGroup>

                <PillsComponent
                  label="Tags"
                  ref={tagCompRef}
                />

                {(() => {
                  const errorView = this.props.errorMessage ?
                    (<div>
                      <Panel
                        header="Create Location Error"
                        bsStyle="danger"
                      >
                        {this.props.errorMessage}
                      </Panel>
                    </div>) : null;
                  return errorView;
                })()}

                <div>
                  <Button
                    id="createLocationBtn"
                    bsSize="large"
                    bsStyle="primary"
                    block
                    onClick={this.props.handleSubmit}
                  >
                    Create
                  </Button>
                </div>
              </form>
            </Col>
          </Row>
        </Grid>
      </div>
    );
  }
}

CreateLocationView.propTypes = {
  errorMessage: PropTypes.string.isRequired,
  getValidState: PropTypes.func.isRequired,
  handleChange: PropTypes.func.isRequired,
  handleSubmit: PropTypes.func.isRequired,
};

export default CreateLocationView;
