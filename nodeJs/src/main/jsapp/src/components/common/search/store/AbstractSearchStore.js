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

import {EventEmitter} from 'events';

class AbstractSearchStore extends EventEmitter {

  constructor(indexName) {
    super();

    this.indexName = indexName;
  }
  
  index(typeName, id, content) {
    throw new Error('AbstractSearchStore should not be used standalone ' + typeName + "/" + id + " - " + content);
  }

  search(typeName, query) {
    throw new Error('AbstractSearchStore should not be used standalone ' + typeName + " - " + query);
  }

  get(typeName, id) {
    throw new Error('AbstractSearchStore should not be used standalone ' + typeName + " - " + id);
  }

  delete(typeName, id) {
    throw new Error('AbstractSearchStore should not be used standalone ' + typeName + " - " + id);
  }

  clear() {
    throw new Error('AbstractSearchStore should not be used standalone');
  }
  
}

export default AbstractSearchStore;