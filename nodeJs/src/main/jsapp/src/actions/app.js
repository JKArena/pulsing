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

import * as types from '../common/eventTypes';

/**
 * @param {Object} error - error message
 * @param {Object} error.additional - additional detail of the error
 * @param {string} error.additional.msg - custom message
 * @param {Object[]} error.additional.args - custom message arguments
 */
export function errorMessage(error) {
  return (dispatch) => {
    console.error('Error message', error);
    dispatch({
      type: types.ERROR_MESSAGE,
      payload: { error },
    });
  };
}

/**
 * @param {Object[]} alerts - various alerts
 * @param {string} alerts[].invitationId - invitation UUID
 * @param {string} alerts[].invitationType - invitation type enum
 *  [CHAT_LOBBY_INVITE, FRIEND_REQUEST_INVITE]
 * @param {number} alerts[].fromUserId - from user Id
 * @param {number} alerts[].expiration - invitation expiration
 */
export function alertUpdate(alerts) {
  return (dispatch) => {
    console.debug(`Alerts: ${alerts}`);
    dispatch({
      type: types.ALERT_UPDATED,
      payload: { alerts },
    });
  };
}
