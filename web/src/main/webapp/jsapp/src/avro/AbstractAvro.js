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

class AbstractAvro {
  
  formMap(form) {
    
    this.formMapper
      .forEach(elementInfo => {
        let field = elementInfo.field;
        let element = this.scopedElementQuery(form, field); //so strange how they are absolute and need :scope
        let value = element[elementInfo.fieldValueAttr || 'value'];
        
        this[field] = value;
      });
  }
  
  getProperty(jsonProperty, unionType, defaultEmpty='') {
    let property = this.json[jsonProperty];
    return (property && property[unionType]) || defaultEmpty;
  }
  
  serialize() {
    let serialization = JSON.stringify(this.json);
    console.debug('serialization', serialization);
    
    return serialization;
  }
  
  scopedElementQuery(element, elementId) {
    return element.querySelector(':scope #' + elementId);
  }
}

export default AbstractAvro;
