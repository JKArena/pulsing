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
package org.jhk.pulsing.storm.kryo.serializer;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.jhk.pulsing.serialization.thrift.data.Data;
import org.jhk.pulsing.storm.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * @author Ji Kim
 */
public final class ThriftDataSerializer extends Serializer<Data> {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(ThriftDataSerializer.class);

    @Override
    public Data read(Kryo kryo, Input input, Class<Data> type) {
        _LOGGER.debug("ThriftDataSerializer.read " + type.getName() + " - " + input.getBuffer().length);
        
        TDeserializer dSerializer = new TDeserializer(new TBinaryProtocol.Factory());
        Data data = new Data();
        
        try {
            //TODO make sure that it comes in read (meaning the buffer), at the moment 
            //since single machine won't be serializing and deserializing
            dSerializer.deserialize(data, input.getBuffer()); 
        } catch (TException tException) {
            tException.printStackTrace();
        }
        
        return data;
    }

    @Override
    public void write(Kryo kryo, Output output, Data tData) {
        _LOGGER.debug("ThriftDataSerializer.write " + tData);
        
        output.write(Util.serializeThriftData(tData));
        output.flush();
        output.close();
    }

}
