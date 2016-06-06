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
package org.jhk.pulsing.storm.aggregator;

import org.apache.storm.trident.operation.CombinerAggregator;
import org.apache.storm.trident.tuple.TridentTuple;

/**
 * @author Ji Kim
 */
public final class Counter implements CombinerAggregator<Long> {

    private static final long serialVersionUID = -7930085122578857788L;

    @Override
    public Long combine(Long first, Long second) {
        return (first.longValue() + second.longValue());
    }

    @Override
    public Long init(TridentTuple tuple) {
        return 1L;
    }

    @Override
    public Long zero() {
        return 0L;
    }
    
}
