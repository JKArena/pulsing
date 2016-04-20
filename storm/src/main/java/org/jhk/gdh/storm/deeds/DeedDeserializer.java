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
package org.jhk.gdh.storm.deeds;

import java.io.IOException;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.jhk.gdh.serialization.SerializationFactory;
import org.jhk.gdh.serialization.pojo.IDeed;

/**
 * @author Ji Kim
 */
public final class DeedDeserializer extends BaseFunction {
    
    private static final long serialVersionUID = -5222249102945206582L;
    
    public static Fields FIELDS = new Fields("id", "deedTime", "description", "deederId", "pluses", "title");
    
    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        
        String deedString = tuple.getString(0);
        
        try {
            
            IDeed deed = SerializationFactory.decodeFromJSONStringToType(IDeed.class, deedString);
            collector.emit(new Values(deed.getId(), deed.getDeedTime(), deed.getDescription(),
                                        deed.getDeederId(), deed.getPluses(), deed.getTitle()));
            
        } catch (IOException decodeException) {
            collector.reportError(decodeException);
        }
        
        
    }

}
