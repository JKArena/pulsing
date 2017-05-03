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
import Url from '../../../../common/Url';

const FRIEND_REQUEST_URL = new URL(Url.djangoRootUrl() + 'friend/friendRequest/');

const FriendRequestAction = Object.freeze(
  {

    friendRequest(userId, friendId) {

      const fData = new FormData();
      fData.append('userId', userId);
      fData.append('friendId', friendId);

      return new Promise(function(resolve, reject) {

        Fetch.POST_JSON(FRIEND_REQUEST_URL, {body: fData}, false)
          .then(function(result) {
            console.debug('friendRequest', result);

            if(result.code === 'SUCCESS') {
              resolve(result.data);
            } else {
              reject(result.message);
            }

          });

      });

    }

  }
);

export default FriendRequestAction;
