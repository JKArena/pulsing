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

import { Grid, Row, Col } from 'react-bootstrap';

import NavContainer from '../containers/NavContainer';
import ChatContainer from '../containers/ChatContainer';

class AppView extends React.Component {
  render() {
    return (
      <div>
        <NavContainer user={this.props.user} onCreateUser={this.props.onCreateUser} onLogOut={this.props.onLogOut}
          onLogIn={this.props.onLogIn} />

        <Grid>
          <Row>
            <Col>
              {this.props.children}
            </Col>
          </Row>
          <Row>
            <Col>
              <ChatContainer user={this.props.user} />
            </Col>
          </Row>
        </Grid>
      </div>
    );
  }
}

AppView.propTypes = {
  user: React.PropTypes.object,
  onCreateUser: React.PropTypes.func,
  onLogIn: React.PropTypes.func,
  onLogOut: React.PropTypes.func,
};

export default AppView;
