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
import * as appActions from './app';

import fetchHelper from '../common/fetchHelper';
import urls from '../common/urls';

const URL = require('url').URL;

const ELASTIC_SEARCH_QUERY_URL = new URL([urls.djangoRootUrl(), 'search/query'].join(''));

export default function search(eventType, index, docType, value,
  queryGenerator = searchValue => `{'term': {'name': {'value': ${searchValue} }}}`) {
  const query = queryGenerator(value);

  return (dispatch) => {
    const params = {
      __proto__: null,
      index,
      doc_type: docType,
      search: query,
    };

    fetchHelper.GET_JSON(ELASTIC_SEARCH_QUERY_URL, {}, params)
      .then((result) => {
        console.debug('searchDocument result', result);

        if (result.code === 'SUCCESS') {
          const searchResult = JSON.parse(result.data);

          dispatch({
            type: types[eventType],
            payload: { [index]: { [docType]: searchResult } },
          });
        }
      })
      .catch((error) => {
        appActions.errorMessage(error)(dispatch);
      });
  };
}
