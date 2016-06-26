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

const FIELD_MAPPER = Symbol('FIELD_MAPPER');

class User extends AbstractAvro {
  
  constructor(json) {
    super();
    
    this.json = json || AvroJson('User');
    this.fieldMapper = User[FIELD_MAPPER];
  }
  
  get email() {
    return this.getProperty('email', 'string');
  }
  
  set email(val) {
    this.json.email = {'string' : val};
  }
  
  get name() {
    return this.getProperty('name', 'string');
  }
  
  set name(val) {
    this.json.name = {'string' : val};
  }
  
  get password() {
    return this.getProperty('password', 'string');
  }
  
  set password(val) {
    this.json.password = {'string' : val};
  }
  
  static get [FIELD_MAPPER]() {
    
    return Object.freeze([
                          {
                            field: 'email',
                            formField: true
                          },
                          {
                            field: 'name',
                            formField: true
                          },
                          {
                            field: 'password',
                            formField: true
                          }
                         ]);
  }
  
  static deserialize(json) {
    console.debug('deserialize', json);
    
    return new User(json);
  }
  
}

export default User;
