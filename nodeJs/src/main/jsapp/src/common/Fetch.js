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
import {TOPICS, API} from './PubSub';

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

function fetchContent(request, hOptions, responseType, logError) {
  
  return new Promise(function(resolve, reject) {

    fetch(request, hOptions)
      .then(function(response) {

        if(response.ok) {
          
          FETCH_RESPONSE_HANDLER[responseType](response, resolve, reject);

        }else {
          if(logError) {
            API.publish(TOPICS.ERROR_MESSAGE, {error: new Error('Failure in fetch'), additional: {
              msg: `Failure in ${responseType} `,
              args: [request, hOptions]
            }});
          }
          
          reject(response);
        }

      })
      .catch(function(err) {
        if(logError) {
          API.publish(TOPICS.ERROR_MESSAGE, {error: err, additional: {
            msg: `Failure in ${responseType} `,
            args: [request, hOptions]
          }});
        }

        reject(err);
      });

  });
  
}

function generatePath(path) {
  return path.startsWith('http') ? path : Url.controllerUrl() + path;
}

export default Object.freeze(
    Object.create(null,
      {
        'GET_JSON' : {
          get: function() {


            return (gPath, hOptions=Object.create(null), params=Object.create(null), logError=true) => {
              
              const DEFAULT_HEADERS = new Headers({'Accept': 'application/json'});
              const DEFAULT_OPTIONS = {method: 'GET',  mode: 'cors', headers: DEFAULT_HEADERS};
              
              
              let url = new URL(generatePath(gPath));
              Object.keys(params).forEach(key => {
                url.searchParams.append(key, params[key]);
              });
              let request = new Request(url);
              let gOptions = Object.assign(DEFAULT_OPTIONS, hOptions);

              return fetchContent(request, gOptions, 'json', logError);
            }
          },

          set: function() {},
          enumerable: true
        },
        
        'POST_JSON' : {
          get: function() {

            return (pPath, hOptions=Object.create(null), logError=true) => {
              
              const DEFAULT_HEADERS = new Headers({'Accept': 'application/json'});
              const DEFAULT_OPTIONS = {method: 'POST',  mode: 'cors', headers: DEFAULT_HEADERS};

              let request = new Request(generatePath(pPath));
              let pOptions = Object.assign(DEFAULT_OPTIONS, hOptions);

              return fetchContent(request, pOptions, 'json', logError);
            }
          },

          set: function() {},
          enumerable: true
        },

        'DELETE_JSON' : {
          get: function() {

            return (dPath, hOptions=Object.create(null), logError=true) => {
              
              const DEFAULT_HEADERS = new Headers({'Accept': 'application/json'});
              const DEFAULT_OPTIONS = {method: 'DELETE',  mode: 'cors', headers: DEFAULT_HEADERS};

              let request = new Request(generatePath(dPath));
              let dOptions = Object.assign(DEFAULT_OPTIONS, hOptions);

              return fetchContent(request, dOptions, 'json', logError);
            }
          },

          set: function() {},
          enumerable: true
        },

        'PUT_JSON' : {
          get: function() {

            return (putURL, hOptions=Object.create(null), logError=true) => {

              const DEFAULT_HEADERS = new Headers({'Accept': 'application/json'});
              const DEFAULT_OPTIONS = {method: 'PUT',  mode: 'cors', headers: DEFAULT_HEADERS};

              let request = new Request(putURL);
              let putOptions = Object.assign(DEFAULT_OPTIONS, hOptions);

              return fetchContent(request, putOptions, 'json', logError);
            }
          },

          set: function() {},
          enumerable: true
        },

        'GET_RAW' : {
          get: function() {

            return (path, hOptions=Object.create(null), logError=true) => {
              
              const DEFAULT_OPTIONS = {method: 'GET',  mode: 'cors'};

              let request = new Request(path);
              let opts = Object.assign(DEFAULT_OPTIONS, hOptions);

              return fetchContent(request, opts, 'raw', logError);
            }
          },

          set: function() {},
          enumerable: true
        }
      }
    )
);
