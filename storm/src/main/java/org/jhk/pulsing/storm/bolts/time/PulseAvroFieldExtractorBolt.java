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
package org.jhk.pulsing.storm.bolts.time;

import org.jhk.pulsing.storm.common.FieldConstants;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class PulseAvroFieldExtractorBolt extends BaseBasicBolt {

    private static final long serialVersionUID = -8825186311978632181L;
    private static final Logger _LOGGER = LoggerFactory.getLogger(PulseAvroFieldExtractorBolt.class);
    
    private EnumSet<EXTRACT_FIELD> _extractFields;
    
    public PulseAvroFieldExtractorBolt() {
        super();
        
        _extractFields = EnumSet.noneOf(EXTRACT_FIELD.class);
    }
    
    public PulseAvroFieldExtractorBolt(EnumSet<EXTRACT_FIELD> extractFields) {
        super();
        
        _extractFields = extractFields;
    }
    
    public enum EXTRACT_FIELD {
        TIMESTAMP(FieldConstants.TIMESTAMP) {
            @Override
            Object getValue(Pulse pulse) {
                return pulse.getTimeStamp();
            }
        }, 
        
        ID(FieldConstants.ID) {
            @Override
            Object getValue(Pulse pulse) {
                return pulse.getId().getId();
            }
        },
        
        USER_ID(FieldConstants.USER_ID) {
            @Override
            Object getValue(Pulse pulse) {
                return pulse.getUserId().getId();
            }
        },
        
        COORDINATES(FieldConstants.COORDINATES) {
            @Override
            Object getValue(Pulse pulse) {
                return pulse.getCoordinates();
            }
        },
        
        VALUE(FieldConstants.VALUE) {
            @Override
            Object getValue(Pulse pulse) {
                return pulse.getValue().toString();
            }
        },
        
        DESCRIPTION(FieldConstants.DESCRIPTION) {
            @Override
            Object getValue(Pulse pulse) {
                return pulse.getDescription().toString();
            }
        },
        
        ACTION(FieldConstants.ACTION) {
            @Override
            Object getValue(Pulse pulse) {
                return pulse.getAction().toString();
            }
        };
        
        private String _field;
        
        private EXTRACT_FIELD(String field) {
            _field = field;
        }
        
        abstract Object getValue(Pulse pulse);
        
        private String getField() {
            return _field;
        }
    };
    
    @Override
    public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
        _LOGGER.info("PulseAvroFieldExtractorBolt.execute: " + tuple);
        
        Pulse pulse = (Pulse) tuple.getValueByField(FieldConstants.AVRO_PULSE);
        
        Values values = new Values();
        
        _extractFields.forEach(field -> {
            values.add(field.getValue(pulse));
        });
        
        _LOGGER.info("PulseAvroFieldExtractorBolt.execute extracted values: " + values);
        outputCollector.emit(values);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer fieldsDeclarer) {
        
        List<String> fields = new LinkedList<>();
        
        _extractFields.forEach(field -> {
           fields.add(field.getField()); 
        });
        
        _LOGGER.info("PulseAvroFieldExtractorBolt.declareOutputFields: " + fields);
        
        fieldsDeclarer.declare(new Fields(fields));
    }

}
