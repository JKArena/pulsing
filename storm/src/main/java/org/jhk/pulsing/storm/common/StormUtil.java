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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.jhk.pulsing.serialization.thrift.data.Data;
import org.jhk.pulsing.shared.util.HadoopConstants;
import org.jhk.pulsing.shared.util.HadoopConstants.PAIL_NEW_DATA_PATH;
import org.jhk.pulsing.shared.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class StormUtil {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(StormUtil.class);
    
    private static final String HADOOP_PAIL_NEW_DATA_PATH_DELIM = "-";
    private static final String HADOOP_PAIL_NEW_DATA_PATH = HadoopConstants.HDFS_URL_PORT + HadoopConstants.PAIL_NEW_DATA_WORKSPACE;
    
    public static String generateNewPailPath(PAIL_NEW_DATA_PATH newDataPath) {
        return HADOOP_PAIL_NEW_DATA_PATH + newDataPath.toString() + File.separator + 
                newDataPath.toString() + HADOOP_PAIL_NEW_DATA_PATH_DELIM + Util.uniqueId();
    }
    
    public static byte[] serializeAvro(SpecificRecord record) {
        _LOGGER.debug("Util.serializeAvro: " + record);
        
        byte[] bytes = new byte[0];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().directBinaryEncoder(out, null);
        SpecificDatumWriter<SpecificRecord> writer = new SpecificDatumWriter<>(record.getSchema());
        
        //rip can't use java 7 since datumwriter doesn't support Autocloseable
        try {
            writer.write(record, encoder);
            encoder.flush();
            bytes = out.toByteArray();
        } catch (IOException serializeException) {
            _LOGGER.error("Util.serializeAvro error during serialization!!!!!!", serializeException);
            serializeException.printStackTrace();
        } finally {
            try {
                if(out != null) {
                    out.close();
                }
            } catch(IOException closeException){
            }
        }
        
        return bytes;
    }
    
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
    
    private StormUtil() {
        super();
    }
    
}
