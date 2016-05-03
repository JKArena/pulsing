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
package org.jhk.interested.storm.deserializers.avro;

import java.io.IOException;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.jhk.interested.serialization.avro.records.Interest;
import org.jhk.interested.serialization.avro.serializers.SerializationFactory;

/**
 * @author Ji Kim
 */
public class InterestDeserializer extends BaseFunction {

    private static final long serialVersionUID = 4863013986214675297L;
    
    public static Fields FIELDS = new Fields("action", "id", "userId", "timeStamp", "value");
    
    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        
        String interestString = tuple.getString(0);
        
        try {
            
            Interest interest = SerializationFactory.deserializeFromJSONStringToAvro(Interest.class, Interest.getClassSchema(), interestString);
            collector.emit(getInterestValues(interest));
            
        } catch (IOException decodeException) {
            collector.reportError(decodeException);
        }
        
    }
    
    private Values getInterestValues(Interest interest) {
        return new Values(interest.getAction().toString(), interest.getId(), interest.getUserId(), 
                interest.getTimeStamp(), interest.getValue());
    }

}
