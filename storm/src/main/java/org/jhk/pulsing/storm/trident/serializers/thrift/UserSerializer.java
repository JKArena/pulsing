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
package org.jhk.pulsing.storm.trident.serializers.thrift;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jhk.pulsing.serialization.thrift.data.Data;
import org.jhk.pulsing.storm.common.SerializerCommon;

/**
 * @author Ji Kim
 */
public final class UserSerializer extends BaseFunction {
    
    private static final long serialVersionUID = 2492968329072034376L;
    private static final Logger _LOG = LoggerFactory.getLogger(UserSerializer.class);
    
    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        _LOG.info("UserSerializer.execute " + tuple);
        
        Data uData = SerializerCommon.constructThriftUser(tuple);
        
        _LOG.info("Serialized to thrift " + uData);
        collector.emit(new Values(uData));
    }
    
}
