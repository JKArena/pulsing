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

import Url from './Url';

const BASIC = Symbol('basic');

const FETCH_RESPONSE_HANDLER = Object.freeze(Object.create(null, {
  
  'json': {
    get: function() {
      
      return (response, resolve, reject) => {
        this[BASIC](response, resolve, reject, 'json');
      };
      
    },
    set: function() {},
    enumerable: true
  },
  
  'blob': {
    get: function() {
      
      return (response, resolve, reject) => {
        this[BASIC](response, resolve, reject, 'blob');
      };
      
    },
    set: function() {},
    enumerable: true
  },
  
  [BASIC]: {
    get: function() {
      
      return (response, resolve, reject, basic_type) => {
        
        response[basic_type]()
          .then(function(result) {
            resolve(result);
          })
          .catch(function(err) {
            console.error(`Failure in ${basic_type} `, err);
            reject(err);
          });
        
      };
      
    },
    set: function() {},
    enumerable: false
  },
  
  'raw': {
    get: function() {
      
      return (response, resolve) => {
        resolve(response);
      };
      
    },
    set: function() {},
    enumerable: true
  }
 })
);

function fetchContent(request, options, responseType='json') {
  
  return new Promise(function(resolve, reject) {

    fetch(request, options)
      .then(function(response) {

        if(response.ok) {
          
          FETCH_RESPONSE_HANDLER[responseType](response, resolve, reject);

        }else {
          console.error('Failure in getting response ', response);
          reject(response);
        }

      })
      .catch(function(err) {
        console.error('Failure in fetching ', err);
        reject(err);
      });

  });
  
}

export default Object.freeze(
    Object.create(null,
      {
        'GET_JSON' : {
          get: function() {

            return (gPath, options=Object.create(null), params=Object.create(null)) => {
              
              const DEFAULT_HEADERS = new Headers({'Accept': 'application/json'});
              const DEFAULT_OPTIONS = {method: 'GET',  mode: 'cors', headers: DEFAULT_HEADERS};
              
              let url = new URL(Url.controllerUrl() + gPath);
              Object.keys(params).forEach(key => {
                url.searchParams.append(key, params[key]);
              });
              let request = new Request(url);
              let gOptions = Object.assign(DEFAULT_OPTIONS, options);

              return fetchContent(request, gOptions, 'json');
            }
          },

          set: function() {},
          enumerable: true
        },
        
        'POST_JSON' : {
          get: function() {

            return (pPath, options=Object.create(null)) => {
              
              const DEFAULT_HEADERS = new Headers({'Accept': 'application/json'});
              const DEFAULT_OPTIONS = {method: 'POST',  mode: 'cors', headers: DEFAULT_HEADERS};

              let request = new Request(Url.controllerUrl() + pPath);
              let pOptions = Object.assign(DEFAULT_OPTIONS, options);

              return fetchContent(request, pOptions, 'json');
            }
          },

          set: function() {},
          enumerable: true
        },

        'DELETE_JSON' : {
          get: function() {

            return (dPath, options=Object.create(null)) => {
              
              const DEFAULT_HEADERS = new Headers({'Accept': 'application/json'});
              const DEFAULT_OPTIONS = {method: 'DELETE',  mode: 'cors', headers: DEFAULT_HEADERS};

              let request = new Request(Url.controllerUrl() + dPath);
              let dOptions = Object.assign(DEFAULT_OPTIONS, options);

              return fetchContent(request, dOptions, 'json');
            }
          },

          set: function() {},
          enumerable: true
        },

        'PUT_JSON' : {
          get: function() {

            return (putPath, options=Object.create(null)) => {
              
              const DEFAULT_HEADERS = new Headers({'Accept': 'application/json'});
              const DEFAULT_OPTIONS = {method: 'PUT',  mode: 'cors', headers: DEFAULT_HEADERS};

              let request = new Request(Url.controllerUrl() + putPath);
              let putOptions = Object.assign(DEFAULT_OPTIONS, options);

              return fetchContent(request, putOptions, 'json');
            }
          },

          set: function() {},
          enumerable: true
        },

        'GET_RAW' : {
          get: function() {

            return (path, options=Object.create(null)) => {
              
              const DEFAULT_OPTIONS = {method: 'GET',  mode: 'cors'};

              let request = new Request(path);
              let opts = Object.assign(DEFAULT_OPTIONS, options);

              return fetchContent(request, opts, 'raw');
            }
          },

          set: function() {},
          enumerable: true
        }
      }
    )
);
