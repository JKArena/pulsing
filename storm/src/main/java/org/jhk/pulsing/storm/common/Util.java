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
    
    private static final Logger _LOG = LoggerFactory.getLogger(Util.class);
    
    public static byte[] serializeThriftData(Data tData) {
        _LOG.info("Util.serializeThriftData: " + tData);
        
        TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
        byte[] bytes = new byte[0];
        
        try {
            bytes = serializer.serialize(tData);
        } catch (TException tException) {
            tException.printStackTrace();
        }
        
        return bytes;
    }
    
    private Util() {
        super();
    }
    
}
