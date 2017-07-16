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

import Fetch from '../common/Fetch';
import Url from '../common/Url';
import Storage from '../common/Storage';
import * as types from '../common/storageTypes';

const LOGIN_URL = new URL(Url.controllerUrl() + 'user/validateUser');

 export function login(btn, formId) {
  
  return (dispatch) => {
    btn.setAttribute('disabled', 'disabled');
    const fData = new FormData(document.getElementById(formId));

    Fetch.POST_JSON(LOGIN_URL, {body: fData}, false)
      .then(function(result) {
        console.debug('loginUser', result);
        
        if(result.code === 'SUCCESS') {
          Storage.user = JSON.parse(result.data); //store as json, but get as Avros (and store in Map as Avros)
          
          dispatch({
            type: types.AUTH_CHANGED,
            payload: { Storage.user },
          });
        }

        btn.removeAttribute('disabled');
      })
      .catch(function(err) {
        btn.removeAttribute('disabled');
      });
    }
  }
 }
 