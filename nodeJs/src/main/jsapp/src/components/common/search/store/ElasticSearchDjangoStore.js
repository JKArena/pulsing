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

import {AbstractSearchStore, STORE_EVENT} from './AbstractSearchStore';
import SearchDocumentAction from '../../actions/documents/SearchDocumentAction';

class ElasticSearchDjangoStore extends AbstractSearchStore {

  constructor(index, pathPrefix) {
    super();
    
    this.index = index;
    this.pathPrefix = pathPrefix;
  }

  index(typeName, id, content) {
    console.debug('index: ', typeName, id, content);

  }

  search(typeName, query) {
    console.debug('search: ', typeName, query);
    
    SearchDocumentAction.searchDocument(this.pathPrefix + 'search', typeName, query)
      .then((searchResult) => {
        this.emit(STORE_EVENT.SEARCH, searchResult);
      });
  }

  get(typeName, id) {
    console.debug('get: ', typeName, id);

  }

  delete(typeName, id) {
    console.debug('delete: ', typeName, id);

  }

  clear() {
    console.debug('clear');

  }

}

export default ElasticSearchDjangoStore;
