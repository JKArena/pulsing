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

import Fetch from '../../../../common/Fetch';
import Storage from '../../../../common/Storage';
import Url from '../../../../common/Url';
import Pulse from '../../../../avro/Pulse';

const CREATE_PULSE_PATH = 'pulse/createPulse';

const CreatePulseAction = Object.freeze(
  {
    __proto__: null,

    createPulse(btnId, formId, tags) {

      let btn = document.getElementById(btnId);
      btn.setAttribute('disabled', 'disabled');

      let tagsArray = [];
      tags.forEach(val => {
        tagsArray.push(val);
      });

      let fData = new FormData();
      let pulse = new Pulse();
      let user = Storage.user;
      pulse.formMap(document.getElementById(formId));
      pulse.userId = user.id.raw;
      pulse.tags = tagsArray;
      pulse.lat = user.lat;
      pulse.lng = user.lng;
      
      fData.append('pulse', pulse.serialize());

      return new Promise(function(resolve, reject) {

        Fetch.POST_JSON(CREATE_PULSE_PATH, {body: fData}, false)
          .then(function(result) {
            console.debug('create pulse', result);

            if(result.code === 'SUCCESS') {
              let pulse = Pulse.deserialize(JSON.parse(result.data));
              Storage.subscribedPulseId = pulse.id.raw;

              resolve(pulse);
            }else {
              reject(result.message);
            }

            btn.removeAttribute('disabled');
          })
          .catch(function(err) {
            btn.removeAttribute('disabled');
            reject(err.message || err);
          });

      });
    }
  }
);

export default CreatePulseAction;
