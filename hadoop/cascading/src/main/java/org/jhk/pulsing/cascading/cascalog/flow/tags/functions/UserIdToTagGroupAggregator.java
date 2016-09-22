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
package org.jhk.pulsing.cascading.cascalog.flow.tags.functions;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.flow.FlowProcess;
import cascading.operation.AggregatorCall;
import cascading.tuple.Tuple;
import cascalog.CascalogAggregator;

/**
 * @author Ji Kim
 */
public final class UserIdToTagGroupAggregator extends CascalogAggregator {
    
    private static final long serialVersionUID = -6855977867969481301L;
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(UserIdToTagGroupAggregator.class);
    
    @Override
    public void start(FlowProcess fProcess, AggregatorCall aCall) {
        _LOGGER.info("UserIdToTagGroupAggregator.start");
        
        aCall.setContext(new HashSet<Long>());
    }
    
    @Override
    public void aggregate(FlowProcess fProcess, AggregatorCall aCall) {
        long tagGroupId = aCall.getArguments().getLong("?tagGroupId");
        long userId = aCall.getArguments().getLong("?userId");
        
        _LOGGER.info("UserIdToTagGroupAggregator.aggregate " + tagGroupId + "/" + userId);
        
        Set<Long> context = (Set<Long>) aCall.getContext();
        context.add(tagGroupId);
    }

    @Override
    public void complete(FlowProcess fProcess, AggregatorCall aCall) {
        _LOGGER.info("UserIdToTagGroupAggregator.complete!!!!!!!!!!!!!!!!!! " + aCall.getContext());
        
        Set<Long> context = (Set<Long>) aCall.getContext();
        aCall.getOutputCollector().add(new Tuple(context));
    }

}
