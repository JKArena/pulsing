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
import PulseId from './PulseId';
import UserId from './UserId';

const FORM_MAPPER = Symbol('FORM_MAPPER');

class Pulse extends AbstractAvro {
  
  constructor(json) {
    super();
    
    this.json = json || AvroJson('Pulse');
    this.formMapper = Pulse[FORM_MAPPER];
  }
  
  get id() {
    return new PulseId(this.json['id']);
  }

  get userId() {
    return new UserId(this.json['userId']);
  }

  get timeStamp() {
    return this.getProperty('timeStamp', 'long');
  }
  
  set coordinates(coords) {
    this.json.coordinates = {'array': coords};
  }
  
  get coordinates() {
    return this.getProperty('coordinates', 'array');
  }

  get tags() {
    return this.getProperty('tags', 'array');
  }
  
  set value(val) {
    this.json.value = {'string' : val};
  }
  
  get value() {
    return this.getProperty('value', 'string');
  }
  
  set action(val) {
    this.json.action = {'string' : val};
  }
  
  get action() {
    return this.getProperty('action', 'string');
  }
  
  static get [FORM_MAPPER]() {
    
    return Object.freeze([
                          {
                            field: 'value'
                          }
                         ]);
  }
  
  static deserialize(json) {
    console.debug('deserialize', json);
    
    return new Pulse(json);
  }
  
}

export default Pulse;
