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

import User from '../../../avro/User';
import Fetch from '../../../common/Fetch';
import Storage from '../../../common/Storage';
import Url from '../../../common/Url';

const LOGIN_URL = new URL(Url.controllerUrl() + 'user/createUser');

const SignupAction = Object.freeze(Object.create(null, {

  'signup': {
    get: function() {
      
      return (btnId, formId, pictureId) => {
        
        const btn = document.getElementById(btnId);
        btn.setAttribute('disabled', 'disabled');
        
        const fData = new FormData();
        const picture = document.getElementById(pictureId).file;
        
        if(picture) {
          fData.append('picture', picture);
        }
        
        const user = new User();
        user.formMap(document.getElementById(formId));
        
        fData.append('user', user.serialize());
        
        return new Promise(function(resolve, reject) {

          Fetch.POST_JSON(LOGIN_URL, {body: fData}, false)
            .then(function(result) {
              console.debug('signup', result);
              
              if(result.code === 'SUCCESS') {
                
                Storage.user = JSON.parse(result.data);
                resolve(Storage.user);
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
    },
    set: function() {},
    enumerable: true
  }

}));

export default SignupAction;
