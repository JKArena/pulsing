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
import UserId from './UserId';
import {TOPICS, API} from '../common/PubSub';

const FORM_MAPPER = Symbol('FORM_MAPPER');
const U_GEOLOCATION_OPTS = {'timeout': 30000, 'maximumAge': 30000};

class User extends AbstractAvro {
  
  constructor(json) {
    super();
    
    this.json = json || AvroJson('User');
    this.formMapper = User[FORM_MAPPER];
    this._watchId = global.navigator.geolocation.watchPosition(this._onPosition.bind(this),
                      (err) => {console.error('Error in geolocation', err);}, U_GEOLOCATION_OPTS);
  }

  clearGeoWatch() {
    global.geolocation.clearWatch(this._watchId);
  }
  
  _onPosition(position) {
    console.debug('position ', position);
    
    let coords = position.coords;
    this.coordinates = [coords.latitude, coords.longitude];
    API.publish(TOPICS.USER_GEO_CHANGE, [coords.latitude, coords.longitude]); //so can't modify the value accidentally
  }
  
  get id() {
    return new UserId(this.json['id']);
  }

  get coordinates() {
    return this.getProperty('coordinates', 'double');
  }
  
  set coordinates(coords) {
    this.json.coordinates = {'double': coords};
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
  
  get pictureUrl() {
    let picture = this.json['picture'];
    
    return (picture && picture.url && picture.url.string) || '';
  }
  
  get password() {
    return this.getProperty('password', 'string');
  }
  
  set password(val) {
    this.json.password = {'string' : val};
  }
  
  static get [FORM_MAPPER]() {
    
    return Object.freeze([
                          {
                            field: 'email'
                          },
                          {
                            field: 'name'
                          },
                          {
                            field: 'password'
                          }
                         ]);
  }
  
  static deserialize(json) {
    console.debug('deserialize', json);
    
    return new User(json);
  }
  
}

export default User;
