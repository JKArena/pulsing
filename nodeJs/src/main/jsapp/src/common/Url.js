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

const SPRING_CONTROLLER_SUFFIX = ':8083/pulsing-spring/controller/';
const SPRING_ROOT_SUFFIX = ':8083/pulsing-spring/';

const DJANGO_SUFFIX = ':9050/';

let PREFIX;

function url(suffix) {
  if(!PREFIX) {
    let location = global.location;
    PREFIX = location.protocol + '//' + location.hostname
  }
  return PREFIX + suffix;
}

export default Object.freeze(
    {
      __proto__: null,

      DEFAULT_PICTURE_PATH: '/images/defaultPicture.png',

      getPicturePath(path) {
        return path ? this.rootUrl() + path : this.DEFAULT_PICTURE_PATH;
      },
      
      rootUrl() {
        return url(SPRING_ROOT_SUFFIX);
      },
      
      controllerUrl() {
        return url(SPRING_CONTROLLER_SUFFIX);
      }
    }
);
