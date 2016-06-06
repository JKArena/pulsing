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
package org.jhk.pulsing.pail.common;

import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import com.backtype.hadoop.pail.PailStructure;

/**
 * @author Ji Kim
 */
public abstract class ThriftPailStructure<T extends Comparable<T>> 
                                            implements PailStructure<T> {

    private static final long serialVersionUID = 8824535385349855885L;
    
    private transient TSerializer serializer;
    private transient TDeserializer deserializer;
    
    private TSerializer getSerializer() {
        if(serializer == null) {
            serializer = new TSerializer();
        }
        return serializer;
    }
    
    private TDeserializer getDeserializer() {
        if(deserializer == null) {
            deserializer = new TDeserializer();
        }
        return deserializer;
    }
    
    @Override
    public T deserialize(byte[] serialized) {
        T thriftObject = createThriftObject();
        
        try {
            getDeserializer().deserialize((TBase) thriftObject, serialized);
        } catch (TException dsException) {
            throw new RuntimeException(dsException);
        }
        
        return thriftObject;
    }

    @Override
    public byte[] serialize(T object) {
        try {
            return getSerializer().serialize((TBase) object);
        } catch (TException sException) {
            throw new RuntimeException(sException);
        }
    }

    public abstract T createThriftObject();
    
}
