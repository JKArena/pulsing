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
package org.jhk.pulsing.storm.bolts.pulse;

import java.io.IOException;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class PulseDeserializerBolt extends BaseBasicBolt {
    
    public static final Fields FIELDS = new Fields("action", "id", "userId", "timeStamp", "value");
    
    private static final Logger _LOG = LoggerFactory.getLogger(PulseDeserializerBolt.class);
    private static final long serialVersionUID = 9003236874311323612L;
    
    @Override
    public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
        _LOG.debug("PulseDeserializerBolt.execute: " + tuple);
        
        String pulseString = tuple.getString(0);
        
        try {
            
            Pulse pulse = SerializationHelper.deserializeFromJSONStringToAvro(Pulse.class, Pulse.getClassSchema(), pulseString);
            outputCollector.emit(getPulseValues(pulse));
            
        } catch (IOException decodeException) {
            outputCollector.reportError(decodeException);
        }
        
    }
    
    private Values getPulseValues(Pulse pulse) {
        return new Values(pulse.getAction().toString(), pulse.getId().getId(), pulse.getUserId().getId(), 
                pulse.getTimeStamp(), pulse.getValue());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer fieldsDeclarer) {
        fieldsDeclarer.declare(FIELDS);
    }

}
