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

import AvroJson from './avrojson';
import AbstractAvro from './AbstractAvro';

class LocationId extends AbstractAvro {
  
  constructor(json) {
    super(json);
    
    this.json = json || AvroJson('LocationId');
  }
  
  get id() {
    return this.json['id']['long'];
  }
  
  static deserialize(json) {
    console.debug('LocationId.deserialize', json);
    
    return new LocationId(json);
  }
  
}

export default LocationId;
