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
package org.jhk.pulsing.giraph.masterCompute;

import org.apache.giraph.aggregators.LongSumAggregator;
import org.apache.giraph.master.DefaultMasterCompute;
import org.apache.hadoop.io.LongWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jhk.pulsing.giraph.common.Constants.FRIENDSHIP_AGGREGATE;

/**
 * @author Ji Kim
 */
public final class FriendTextMasterCompute extends DefaultMasterCompute {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(FriendTextMasterCompute.class);

    @Override
    public void compute() {
        LongWritable friendship = getAggregatedValue(FRIENDSHIP_AGGREGATE);
        String superstep = String.valueOf(getSuperstep());

        _LOGGER.info("Superstep: " + superstep + "; friendship: " + friendship);
        _LOGGER.info("Superstep: " + superstep + "; edges: " + getTotalNumEdges());
        _LOGGER.info("Superstep: " + superstep + "; vertices: " + getTotalNumVertices());
    }
    
    @Override
    public void initialize() throws InstantiationException, IllegalAccessException {
        registerAggregator(FRIENDSHIP_AGGREGATE, LongSumAggregator.class);
    }
    
}
