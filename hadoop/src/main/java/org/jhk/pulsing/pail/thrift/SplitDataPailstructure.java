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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import org.jhk.pulsing.serialization.thrift.data.Data;
import org.jhk.pulsing.serialization.thrift.data.DataUnit;

/**
 * @author Ji Kim
 */
public final class SplitDataPailstructure extends DataPailStructure {
    
    private static final long serialVersionUID = 2970071032803343849L;
    
    private static Map<Short, FieldStructure> validFieldMap = new HashMap<>();
    
    static {
        
        for(DataUnit._Fields key: DataUnit.metaDataMap.keySet()) {
            FieldValueMetaData mData = DataUnit.metaDataMap.get(key).valueMetaData;
            FieldStructure fstruct;
            
            if(mData instanceof StructMetaData && ((StructMetaData) mData).structClass.getName().endsWith("Property")) {
                fstruct = new PropertyStructure(((StructMetaData) mData).structClass);
            } else {
                fstruct = new EdgeStructure();  
            }
            
            validFieldMap.put(key.getThriftFieldId(), fstruct);
        }
        
    }
    
    @Override
    public List<String> getTarget(Data object) {
        List<String> target = new ArrayList<>();
        DataUnit dUnit = object.getDataunit();
        short id = dUnit.getSetField().getThriftFieldId();
        target.add(""+id);
        validFieldMap.get(id).fillTarget(target,  dUnit.getFieldValue());
        return target;
    }
    
    @Override
    public boolean isValidTarget(String... dirs) {
        if(dirs.length == 0) {
            return false;
        }
        
        try {
            short id = Short.parseShort(dirs[0]);
            FieldStructure fStructure = validFieldMap.get(id);
            
            if(fStructure == null) {
                return false;
            } else {
                return fStructure.isValidTarget(dirs);
            }
        } catch(NumberFormatException nFormatException) {
            return false;
        }
        
    }
    
}
