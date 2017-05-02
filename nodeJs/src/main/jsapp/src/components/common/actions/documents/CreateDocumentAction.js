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

const CreateDocumentAction = Object.freeze(
  {

    createDocument(path, index, doc_type, data) {

      const fData = new FormData();
      for(let key of Object.keys(data)) {
        fData.append(key, data[key]);
      }
      fData.append('index', index);
      fData.append('doc_type', doc_type);

      return new Promise(function(resolve, reject) {

        Fetch.POST_JSON(new URL(path), {body: fData}, false)
          .then(function(result) {
            console.debug('createDocument', result);

            if(result.code === 'SUCCESS') {
              resolve();
            } else {
              reject(result.message);
            }

          });

      });

    }

  }
);

export default CreateDocumentAction;
