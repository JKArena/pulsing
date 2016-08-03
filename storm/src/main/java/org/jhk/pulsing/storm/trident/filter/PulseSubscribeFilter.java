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
package org.jhk.pulsing.storm.trident.filter;

import org.apache.storm.trident.operation.BaseFilter;
import org.apache.storm.trident.tuple.TridentTuple;
import org.jhk.pulsing.serialization.avro.records.ACTION;

/**
 * @author Ji Kim
 */
public final class PulseSubscribeFilter extends BaseFilter {
    
    private static final long serialVersionUID = -5563080957922894127L;

    @Override
    public boolean isKeep(TridentTuple tuple) {
        return tuple.getStringByField("action").equals(ACTION.SUBSCRIBE.toString());
    }
    
}
