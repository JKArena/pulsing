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

import Fetch from '../../../common/Fetch';

const SUBSCRIBE_PULSE_PATH = 'pulse/subscribePulse';

const SubscribePulseAction = Object.freeze(
  {

    subscribePulse(pulseId, userId) {
      
      let fData = new FormData();
      fData.append('pulseId', pulseId.serialize());
      fData.append('userId', userId.serialize());

      return new Promise(function(resolve, reject) {

        Fetch.PUT_JSON(SUBSCRIBE_PULSE_PATH, {body: fData})
          .then(function(result) {
            console.debug('subscribePulse', result);

            if(result.code === 'SUCCESS') {
              Storage.subscribedPulseId = JSON.parse(result.data);
              resolve(Storage.subscribedPulseId);
            }else {
              reject(result.message);
            }

          });

      });

    }

  }
);

export default SubscribePulseAction;
