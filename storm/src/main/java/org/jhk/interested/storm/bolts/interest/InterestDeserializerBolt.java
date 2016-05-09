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
package org.jhk.interested.storm.bolts.interest;

import java.io.IOException;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.jhk.interested.serialization.avro.records.Interest;
import org.jhk.interested.serialization.avro.serializers.SerializationFactory;

/**
 * @author Ji Kim
 */
public final class InterestDeserializerBolt extends BaseBasicBolt {
    
    private static final long serialVersionUID = 9003236874311323612L;
    
    public static Fields FIELDS = new Fields("action", "id", "userId", "timeStamp", "value");
    
    @Override
    public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
        String interestString = tuple.getString(0);
        
        try {
            
            Interest interest = SerializationFactory.deserializeFromJSONStringToAvro(Interest.class, Interest.getClassSchema(), interestString);
            outputCollector.emit(getInterestValues(interest));
            
        } catch (IOException decodeException) {
            outputCollector.reportError(decodeException);
        }
        
    }
    
    private Values getInterestValues(Interest interest) {
        return new Values(interest.getAction().toString(), interest.getId().getId(), interest.getUserId().getId(), 
                interest.getTimeStamp(), interest.getValue());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer fieldsDeclarer) {
        fieldsDeclarer.declare(FIELDS);
    }

}
