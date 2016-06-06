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
package org.jhk.pulsing.pail.thrift;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.StructMetaData;

/**
 * @author Ji Kim
 */
public final class PropertyStructure implements FieldStructure {
    
    private TFieldIdEnum valueId;
    private Set<Short> validIds;
    
    public PropertyStructure(Class property) {
        
        try {
            
            Map<TFieldIdEnum, FieldMetaData> pMeta = getMetadataMap(property);
            Class vClass = Class.forName(property.getName() + "Value");
            
            valueId = getIdForClass(pMeta, vClass);
            validIds = new HashSet<Short>();
            
            Map<TFieldIdEnum, FieldMetaData> vMeta = getMetadataMap(vClass);
            for(TFieldIdEnum vId : vMeta.keySet()) {
                validIds.add(vId.getThriftFieldId());
            }
            
        } catch(Exception except) {
            throw new RuntimeException(except);
        }
        
    }
    
    
    @Override
    public boolean isValidTarget(String[] dirs) {
        return false;
    }

    @Override
    public void fillTarget(List<String> ret, Object val) {
    }
    
    private Map<TFieldIdEnum, FieldMetaData> getMetadataMap(Class property) {
        try {
            Object obj= property.newInstance();
            return (Map<TFieldIdEnum, FieldMetaData>) property.getField("metaDataMap").get(obj);
        } catch(Exception except) {
            throw new RuntimeException(except);
        }
    }
    
    private TFieldIdEnum getIdForClass(Map<TFieldIdEnum, FieldMetaData> meta, Class toFind) {
        for(TFieldIdEnum key : meta.keySet()) {
            FieldValueMetaData mData = meta.get(key).valueMetaData;
            if(mData instanceof StructMetaData) {
                if(toFind.equals(((StructMetaData) mData).structClass)) {
                    return key;
                }
            }
        }
        
        throw new RuntimeException("Could not find " + toFind.toString() + " in " + meta.toString());
    }

}
