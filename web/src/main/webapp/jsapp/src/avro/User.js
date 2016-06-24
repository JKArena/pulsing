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

import AvroJson from './avrojson';
import AbstractAvro from './AbstractAvro';

class User extends AbstractAvro {
  
  constructor() {
    super();
    
    this.skeleton = AvroJson('User');
    this.formMapper = Object.freeze([
                                     {
                                       id: 'email',
                                       unionType: 'string'
                                     },
                                     {
                                       id: 'name',
                                       unionType: 'string'
                                     },
                                     {
                                       id: 'password',
                                       unionType: 'string'
                                     }
                                     ]);
  }
  
  formMap(form) {
    super.formMap(form);
    
  }
  
  static deserialize(json) {
    console.debug('deserialize', json);
    
    let user = new User();
    //TODO map json to user class. have all setters + getters
    
    return user;
  }
  
}

export default User;
