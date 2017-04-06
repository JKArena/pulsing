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

const GET_FRIENDS_PATH = 'friend/queryFriends/';

const GetFriendsAction = Object.freeze(
  {

    queryFriends(userId) {

      let params = {__proto__: null,
                    'userId': userId.serialize()};
      return new Promise(function(resolve, reject) {

        Fetch.GET_JSON(GET_FRIENDS_PATH, {}, params)
          .then(function(result) {
            console.debug('queryFriends result', result);
            
            if(result.code === 'SUCCESS') {
              resolve(result.data);
            } else {
              reject(result.message);
            }
          })
          .catch(function(err) {
            reject(err);
          });

      });

    }

  }
);

export default GetFriendsAction;
