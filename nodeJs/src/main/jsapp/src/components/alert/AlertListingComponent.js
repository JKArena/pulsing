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

require('./AlertListing.scss');

import React, {Component} from 'react';
import {Table} from 'react-bootstrap';

import Storage from '../../common/Storage';
import GetAlertListAction from './actions/GetAlertListAction';

class AlertListingComponent extends Component {
  
  constructor(props) {
    super(props);

    this.state = {};
    this.alertEntries = [];
  }

  componentDidMount() {

    GetAlertListAction.getAlertList(Storage.user.id)
      .then((alertEntries) => {
        this.alertEntries = alertEntries || [];
        this.setState(this.state);
      });
  }

  render() {
    let rows = [];

    this.alertEntries.forEach((value, key) => {
      
      rows.push(<tr data-user-id={value.fromUserId} data-invitation-id={value.invitationId}>
        <td>{key}</td>
        <td>{value.invitationType}</td>
        <td>{new Date(value.expiration).toLocaleString()}</td>
        </tr>);
    });

    return (
      <div className='alertlisting-component'>
        <Table responsive>
          <thead>
            <tr>
              <th>#</th>
              <th>Invitation Type</th>
              <th>Expiration</th>
            </tr>
          </thead>
          <tbody>
            {rows}
          </tbody>
        </Table>
      </div>
    );
  }
}

AlertListingComponent.displayName = 'AlertListingComponent';

export default AlertListingComponent;

