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
package org.jhk.pulsing.storm.serializers.thrift;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.jhk.pulsing.serialization.avro.records.Picture;
import org.jhk.pulsing.serialization.thrift.data.Data;
import org.jhk.pulsing.serialization.thrift.data.DataUnit;
import org.jhk.pulsing.serialization.thrift.data.Pedigree;
import org.jhk.pulsing.serialization.thrift.id.UserId;
import org.jhk.pulsing.serialization.thrift.property.PicturePropertyValue;
import org.jhk.pulsing.serialization.thrift.property.UserProperty;
import org.jhk.pulsing.serialization.thrift.property.UserPropertyValue;
import org.jhk.pulsing.shared.util.Util;

import static org.jhk.pulsing.storm.common.FieldConstants.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is truly an overkill, but simply to play around w/ both avro + thrift
 * 
 * @author Ji Kim
 */
public final class UserSerializer extends BaseFunction {
    
    public static final Fields FIELDS = new Fields(DATA);
    
    private static final Logger _LOG = LoggerFactory.getLogger(UserSerializer.class);
    private static final long serialVersionUID = 2492968329072034376L;

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        _LOG.info("UserSerializer.execute " + tuple);
        
        Data uData = populateThriftUser(tuple);
        
        _LOG.info("Serialized to thrift " + uData);
        collector.emit(new Values(uData));
    }
    
    private Data populateThriftUser(TridentTuple tuple) {
        
        Data data = new Data();
        data.setPedigree(new Pedigree(Util.convertNanoToSeconds(System.nanoTime())));
        
        DataUnit dUnit = new DataUnit();
        data.setDataunit(dUnit);
        
        UserProperty uProperty = new UserProperty();
        dUnit.setUser_property(uProperty);
        
        UserPropertyValue upValue = new UserPropertyValue();
        uProperty.setId(UserId.id(tuple.getLongByField(ID)));
        uProperty.setProperty(upValue);
        
        upValue.setEmail(tuple.getStringByField(EMAIL));
        upValue.setPassword(tuple.getStringByField(PASSWORD));
        upValue.setName(tuple.getStringByField(NAME));
        
        @SuppressWarnings("unchecked")
        List<Double> coordinates = (List<Double>) tuple.getValueByField(COORDINATES);
        if(coordinates != null) {
            upValue.setCoordinates(coordinates);
        }
        
        Picture avroPicture = (Picture) tuple.getValueByField(PICTURE);
        if(avroPicture != null) {
            //research how others are passing byte data over messaging. The easiest solution 
            //is to encode the byte into base 64 string, but the size increase is 2-3 times original
            upValue.setPicture(PicturePropertyValue.originalFilename(avroPicture.getName().toString()));
        }
        
        return data;
    }
    
}
