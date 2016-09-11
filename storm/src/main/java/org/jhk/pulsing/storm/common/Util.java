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

import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.jhk.pulsing.serialization.thrift.data.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class Util {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(Util.class);
    
    public static byte[] serializeThriftData(Data tData) {
        _LOGGER.debug("Util.serializeThriftData: " + tData);
        
        TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
        byte[] bytes = new byte[0];
        
        try {
            bytes = serializer.serialize(tData);
        } catch (TException tException) {
            _LOGGER.error("Util.serializeThriftData error during serialization!!!!!!", tException);
            tException.printStackTrace();
        }
        
        return bytes;
    }
    
    public static byte[] serializeThriftDataList(List<Data> tDatas) {
        _LOGGER.debug("Util.serializeThriftDatas: " + tDatas);
        
        TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
        
        int length = 0;
        List<byte[]> datas = new LinkedList<>();
        for(Data data : tDatas) {
            byte[] bData = new byte[0];
            
            try {
                bData = serializer.serialize(data);
                length += bData.length;
                datas.add(bData);
            } catch (TException tException) {
                _LOGGER.error("Util.serializeThriftDatas error during serialization!!!!!!", tException);
                tException.printStackTrace();
            }
        };
        
        int destPos = 0;
        byte[] bytes = new byte[length];
        for(byte[] byt : datas) {
            int cLength = byt.length;
            System.arraycopy(byt, 0, bytes, destPos, cLength);
            destPos += cLength;
        }
        
        return bytes;
    }
    
    private Util() {
        super();
    }
    
}
