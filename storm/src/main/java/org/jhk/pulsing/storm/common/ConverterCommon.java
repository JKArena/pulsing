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
import java.util.Set;
import java.util.stream.Collectors;

import static org.jhk.pulsing.storm.common.FieldConstants.*;

import org.apache.storm.tuple.ITuple;
import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.thrift.data.Data;
import org.jhk.pulsing.serialization.thrift.data.DataUnit;
import org.jhk.pulsing.serialization.thrift.data.Pedigree;
import org.jhk.pulsing.serialization.thrift.edges.TagGroupUserEdge;
import org.jhk.pulsing.serialization.thrift.id.TagGroupId;
import org.jhk.pulsing.serialization.thrift.id.UserId;
import org.jhk.pulsing.serialization.thrift.property.TagGroupProperty;
import org.jhk.pulsing.serialization.thrift.property.TagGroupPropertyValue;
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
        _LOGGER.info("ConverterCommon.convertPulseAvroToThriftDataList " + tuple);
        
        Pulse pulse = (Pulse) tuple.getValueByField(AVRO);
        
        UserId uId = UserId.id(pulse.getUserId().getId());
        double lat = pulse.getLat();
        double lng = pulse.getLng();
        long tStamp = pulse.getTimeStamp();
        
        Set<String> tags = pulse.getTags().parallelStream()
                .map(cSequence -> {
                   return cSequence.toString(); 
                })
                .collect(Collectors.toSet());
        
        //1) create a TagGroup for these tags to group them using pulseId and coordinates
        //   (so to group additional tags if user modifies the pulse)
        //2) create an edge between user and tagGroup by creating a TagGroupUserEdge
        //3) create multiple TagGroupProperty linking the tag name with the TagGroupId
        //
        //in hadoop 
        //1) create a relation graph of the associated TagGroups by grouping by tag name including 
        //   tagGroupId + coordinates
        //2) then take the result and gather tagGroupIds together using geohash w/ coordinates
        //3) create a cassandra entry of the userId -> set(tagGroupIds -> {tag})
        
        TagGroupId tgId = new TagGroupId(pulse.getId().getId(), lat, lng);
        
        List<Data> tDatas = tags.parallelStream()
            .map(tag -> {
                Data data = new Data();
                data.setPedigree(new Pedigree(tStamp));
                
                DataUnit dUnit = new DataUnit();
                data.setDataunit(dUnit);
                
                TagGroupProperty tgProperty = new TagGroupProperty();
                dUnit.setTaggroup_property(tgProperty);
                
                TagGroupPropertyValue tgpValue = new TagGroupPropertyValue();
                tgpValue.setTag(tag);
                
                tgProperty.setProperty(tgpValue);
                tgProperty.setId(tgId);
                
                return data;
            })
            .collect(Collectors.toList());
        
        Data data = new Data();
        data.setPedigree(new Pedigree(tStamp));
        
        DataUnit dUnit = new DataUnit();
        data.setDataunit(dUnit);
        
        TagGroupUserEdge tguEdge = new TagGroupUserEdge();
        tguEdge.setUserId(uId);
        tguEdge.setTagGroupId(tgId);
        dUnit.setTaggroupuser_edge(tguEdge);
        
        tDatas.add(data);
        
        return tDatas;
    }
    
    public static Data convertUserAvroToThriftData(ITuple tuple) {
        _LOGGER.info("ConverterCommon.convertUserAvroToThriftData " + tuple);
        
        User user = (User) tuple.getValueByField(AVRO);
        
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
        upValue.setName(user.getName().toString());
        
        Double lat = user.getLat();
        Double lng = user.getLng();
        if(lat != null && lng != null) {
            upValue.setLat(lat);
            upValue.setLng(lng);
        }
        
        return data;
    }
    
    private ConverterCommon() {
        super();
    }
    
}
