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
import { INDICES } from '../common/searchTypes';

const STATE = {};
Object.keys(INDICES).forEach((index) => {
  const docTypes = {};
  STATE[index] = docTypes;
  INDICES[index].forEach((entry) => {
    docTypes[entry.docType] = null;
  });
});

export default function search(state = STATE, action) {
  switch (action.type) {
    case types.USER_SEARCH: {
      return { ...state, ...action.payload };
    }
    case types.PULSE_SEARCH: {
      return { ...state, ...action.payload };
    }
    default:
      return state;
  }
}
