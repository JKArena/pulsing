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

const LOGIN_PATH = 'user/validateUser';

const LoginAction = Object.freeze(Object.create(null, {

  'loginUser': {
    get: function() {
      
      return (btnId, formId) => {
        
        let btn = document.getElementById(btnId);
        btn.setAttribute('disabled', 'disabled');
        
        let fData = new FormData(document.getElementById(formId));
        
        return new Promise(function(resolve, reject) {

          Fetch.POST_JSON(LOGIN_PATH, {body: fData})
            .then(function(result) {
              console.info('loginUser', result);
              
              if(result.code === 'SUCCESS') {
                resolve(JSON.parse(result.data));
              }else {
                reject(result.message);
              }

              btn.removeAttribute('disabled');
            })
            .catch(function(err) {
              console.error(err);
              
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

export default LoginAction;