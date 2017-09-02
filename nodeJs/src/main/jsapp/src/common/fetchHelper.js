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

const BASIC = Symbol('basic');

const FETCH_RESPONSE_HANDLER = Object.freeze(Object.create(null, {
  json: (response, resolve, reject) => {
    this[BASIC](response, resolve, reject, 'json');
  },

  blob: (response, resolve, reject) => {
    this[BASIC](response, resolve, reject, 'blob');
  },

  [BASIC]: (response, resolve, reject, basicType) => {
    response[basicType]()
      .then(result => resolve(result))
      .catch(err => reject(err));
  },

  raw: (response, resolve) => {
    resolve(response);
  },
}));

function fetchContent(request, hOptions, responseType, logError) {
  return new Promise((resolve, reject) => {
    fetch(request, hOptions)
      .then((response) => {
        if (response.ok) {
          FETCH_RESPONSE_HANDLER[responseType](response, resolve, reject);
        } else {
          reject(response);
        }
      })
      .catch((err) => {
        console.info(`logError: ${logError}`);
        reject(err);
      });
  });
}

const fetchHelper = Object.freeze(Object.create(null,
  {
    GET_JSON: (gUrl, hOptions = Object.create(null), params = Object.create(null),
      logError = true) => {
      const DEFAULT_HEADERS = new Headers({ Accept: 'application/json' });
      const DEFAULT_OPTIONS = { method: 'GET', mode: 'cors', headers: DEFAULT_HEADERS };

      Object.keys(params).forEach((key) => {
        gUrl.searchParams.append(key, params[key]);
      });

      const request = new Request(gUrl);
      const gOptions = Object.assign(DEFAULT_OPTIONS, hOptions);

      return fetchContent(request, gOptions, 'json', logError);
    },

    POST_JSON: (pUrl, hOptions = Object.create(null), logError = true) => {
      const DEFAULT_HEADERS = new Headers({ Accept: 'application/json' });
      const DEFAULT_OPTIONS = { method: 'POST', mode: 'cors', headers: DEFAULT_HEADERS };

      const request = new Request(pUrl);
      const pOptions = Object.assign(DEFAULT_OPTIONS, hOptions);

      return fetchContent(request, pOptions, 'json', logError);
    },

    DELETE_JSON: (dUrl, hOptions = Object.create(null), logError = true) => {
      const DEFAULT_HEADERS = new Headers({ Accept: 'application/json' });
      const DEFAULT_OPTIONS = { method: 'DELETE', mode: 'cors', headers: DEFAULT_HEADERS };

      const request = new Request(dUrl);
      const dOptions = Object.assign(DEFAULT_OPTIONS, hOptions);

      return fetchContent(request, dOptions, 'json', logError);
    },

    PUT_JSON: (pUrl, hOptions = Object.create(null), logError = true) => {
      const DEFAULT_HEADERS = new Headers({ Accept: 'application/json' });
      const DEFAULT_OPTIONS = { method: 'PUT', mode: 'cors', headers: DEFAULT_HEADERS };

      const request = new Request(pUrl);
      const putOptions = Object.assign(DEFAULT_OPTIONS, hOptions);

      return fetchContent(request, putOptions, 'json', logError);
    },

    GET_RAW: (url, hOptions = Object.create(null), logError = true) => {
      const DEFAULT_OPTIONS = { method: 'GET', mode: 'cors' };

      const request = new Request(url);
      const opts = Object.assign(DEFAULT_OPTIONS, hOptions);

      return fetchContent(request, opts, 'raw', logError);
    },
  },
));

export default fetchHelper;
