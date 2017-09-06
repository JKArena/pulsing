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
import { connect } from 'react-redux';
import { Grid, Row, Col } from 'react-bootstrap';

import NavContainer from '../containers/NavContainer';
import ChatContainer from '../containers/ChatContainer';

import User from '../avro/User';

require('normalize.css/normalize.css');
require('bootstrap/dist/css/bootstrap.css');

const AppView = props =>
  (<div>
    <NavContainer />

    <Grid>
      <Row>
        <Col>
          {props.children}
        </Col>
      </Row>
      {(() => {
        const chat = props.user ?
          (<Row>
            <Col>
              <ChatContainer user={props.user} />
            </Col>
          </Row>) : null;
        return chat;
      })()}
    </Grid>
  </div>);

AppView.propTypes = {
  user: React.PropTypes.objectOf(User).isRequired,
  children: React.PropTypes.element.isRequired,
};

export function mapStateToProps(state) {
  return {
    user: state.auth.user,
  };
}

export default connect(mapStateToProps)(AppView);