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
package org.jhk.pulsing.storm.common;

import java.util.List;

import static org.jhk.pulsing.serialization.thrift.edges.ACTION.*;
import static org.jhk.pulsing.storm.common.FieldConstants.*;

import org.apache.storm.tuple.ITuple;
import org.jhk.pulsing.serialization.avro.records.Picture;
import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.thrift.data.Data;
import org.jhk.pulsing.serialization.thrift.data.DataUnit;
import org.jhk.pulsing.serialization.thrift.data.Pedigree;
import org.jhk.pulsing.serialization.thrift.edges.PulseEdge;
import org.jhk.pulsing.serialization.thrift.id.PulseId;
import org.jhk.pulsing.serialization.thrift.id.UserId;
import org.jhk.pulsing.serialization.thrift.property.PicturePropertyValue;
import org.jhk.pulsing.serialization.thrift.property.PulseProperty;
import org.jhk.pulsing.serialization.thrift.property.PulsePropertyValue;
import org.jhk.pulsing.serialization.thrift.property.UserProperty;
import org.jhk.pulsing.serialization.thrift.property.UserPropertyValue;
import org.jhk.pulsing.shared.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is truly an overkill, but simply to play around w/ both avro + thrift
 * 
 * @author Ji Kim
 */
public final class ConverterCommon {
    
    private static final Logger _LOG = LoggerFactory.getLogger(ConverterCommon.class);
    
    public static Data convertPulseAvroToThrift(ITuple tuple) {
        _LOG.info("ConverterCommon.convertPulseAvroToThrift " + tuple);
        
        Pulse pulse = (Pulse) tuple.getValueByField(AVRO_PULSE);
        
        Data data = new Data();
        data.setPedigree(new Pedigree(Util.convertNanoToSeconds(pulse.getTimeStamp())));
        
        DataUnit dUnit = new DataUnit();
        data.setDataunit(dUnit);
        
        PulseProperty pProperty = new PulseProperty();
        dUnit.setPulse_property(pProperty);
        
        PulseId pId = PulseId.id(pulse.getId().getId());
        PulsePropertyValue ppValue = new PulsePropertyValue();
        pProperty.setId(pId);
        pProperty.setProperty(ppValue);
        
        ppValue.setValue(pulse.getValue().toString());
        
        List<Double> coordinates = pulse.getCoordinates();
        if(coordinates != null) {
            ppValue.setCoordinates(coordinates);
        }
        
        PulseEdge pEdge = new PulseEdge();
        dUnit.setPulse(pEdge);
        
        pEdge.setUserId(UserId.id(pulse.getUserId().getId()));
        pEdge.setPulseId(pId);
        
        String action = pulse.getAction().toString();
        
        switch(action) {
        case "CREATE": pEdge.setAction(CREATE); break;
        case "SUBSCRIBE": pEdge.setAction(SUBSCRIBE); break;
        case "UNSUBSCRIBE": pEdge.setAction(UNSUBSCRIBE); break;
        case "DELETE": pEdge.setAction(DELETE); break;
        }
        
        return data;
    }
    
    public static Data convertUserAvroToThrift(ITuple tuple) {
        _LOG.info("ConverterCommon.convertUserAvroToThrift " + tuple);
        
        User user = (User) tuple.getValueByField(AVRO_USER);
        
        Data data = new Data();
        data.setPedigree(new Pedigree(Util.convertNanoToSeconds(System.nanoTime())));
        
        DataUnit dUnit = new DataUnit();
        data.setDataunit(dUnit);
        
        UserProperty uProperty = new UserProperty();
        dUnit.setUser_property(uProperty);
        
        UserPropertyValue upValue = new UserPropertyValue();
        uProperty.setId(UserId.id(user.getId().getId()));
        uProperty.setProperty(upValue);
        
        upValue.setEmail(user.getEmail().toString());
        upValue.setPassword(user.getPassword().toString());
        upValue.setName(user.getName().toString());
        
        List<Double> coordinates = user.getCoordinates();
        if(coordinates != null) {
            upValue.setCoordinates(coordinates);
        }
        
        Picture avroPicture = user.getPicture();
        if(avroPicture != null && avroPicture.getName() != null) {
            //research how others are passing byte data over messaging. The easiest solution 
            //is to encode the byte into base 64 string, but the size increase is 2-3 times original
            upValue.setPicture(PicturePropertyValue.originalFilename(avroPicture.getName().toString()));
        }
        
        return data;
    }
    
    private ConverterCommon() {
        super();
    }
    
}
