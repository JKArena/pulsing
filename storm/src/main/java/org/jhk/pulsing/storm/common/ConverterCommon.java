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

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.jhk.pulsing.storm.common.FieldConstants.*;

import org.apache.storm.tuple.ITuple;
import org.jhk.pulsing.serialization.avro.records.Picture;
import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.thrift.data.Data;
import org.jhk.pulsing.serialization.thrift.data.DataUnit;
import org.jhk.pulsing.serialization.thrift.data.Pedigree;
import org.jhk.pulsing.serialization.thrift.edges.TagEdge;
import org.jhk.pulsing.serialization.thrift.id.TagId;
import org.jhk.pulsing.serialization.thrift.id.UserId;
import org.jhk.pulsing.serialization.thrift.property.PicturePropertyValue;
import org.jhk.pulsing.serialization.thrift.property.TagProperty;
import org.jhk.pulsing.serialization.thrift.property.TagPropertyValue;
import org.jhk.pulsing.serialization.thrift.property.UserProperty;
import org.jhk.pulsing.serialization.thrift.property.UserPropertyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is truly an overkill, but simply to play around w/ both avro + thrift
 * 
 * @author Ji Kim
 */
public final class ConverterCommon {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(ConverterCommon.class);
    
    /**
     * Create multiple Data objects for the tags as well as the pulse name to create 
     * edges between the tag + userId nodes.
     * 
     * Possibly consider splitting the tag+pulse name by space and adding them as well.
     * 
     * @param tuple
     * @return
     */
    public static List<Data> convertPulseAvroToThriftDataList(ITuple tuple) {
        _LOGGER.debug("ConverterCommon.convertPulseAvroToThriftDataList " + tuple);
        
        Pulse pulse = (Pulse) tuple.getValueByField(AVRO_PULSE);
        
        UserId uId = UserId.id(pulse.getUserId().getId());
        List<Double> coordinates = pulse.getCoordinates();
        long tStamp = pulse.getTimeStamp();
        
        List<String> tags = pulse.getTags().parallelStream()
                .map(cSequence -> {
                   return cSequence.toString(); 
                })
                .collect(Collectors.toList());
        tags.add(pulse.getValue().toString());
        
        List<Data> tDatas = tags.parallelStream()
            .map(tag -> {
                Data data = new Data();
                data.setPedigree(new Pedigree(tStamp));
                
                DataUnit dUnit = new DataUnit();
                data.setDataunit(dUnit);
                
                TagId tId = TagId.tag(tag);
                
                TagProperty tProperty = new TagProperty();
                dUnit.setTag_property(tProperty);
                
                TagPropertyValue tpValue = new TagPropertyValue();
                tpValue.setCoordinates(coordinates);
                tProperty.setProperty(tpValue);
                tProperty.setId(tId);
                
                TagEdge tEdge = new TagEdge();
                dUnit.setTag(tEdge);
                tEdge.setTagId(tId);
                tEdge.setUserId(uId);
                
                return data;
            })
            .collect(Collectors.toList());
        
        return tDatas;
    }
    
    public static Data convertUserAvroToThriftData(ITuple tuple) {
        _LOGGER.debug("ConverterCommon.convertUserAvroToThriftData " + tuple);
        
        User user = (User) tuple.getValueByField(AVRO_USER);
        
        Data data = new Data();
        data.setPedigree(new Pedigree(Instant.now().getEpochSecond()));
        
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
