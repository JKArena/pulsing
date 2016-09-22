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
import java.util.Optional;
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
public final class UserIdToTagAggregator extends CascalogAggregator {
    
    private static final long serialVersionUID = -5482009255295278242L;
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(UserIdToTagAggregator.class);
    
    @Override
    public void start(FlowProcess fProcess, AggregatorCall aCall) {
        _LOGGER.info("UserIdToTagAggregator.start");
        
        aCall.setContext(new HashSet<String>());
    }
    
    @Override
    public void aggregate(FlowProcess fProcess, AggregatorCall aCall) {
        
        //"?userId", "?uTagGroupIdGroups", "?tag", "?tTagGroupIdGroups"
        String tag = aCall.getArguments().getString("?tag");
        long userId = aCall.getArguments().getLong("?userId");
        
        _LOGGER.info("UserIdToTagAggregator.aggregate " + tag + "/" + userId);
        
        Set<Long> uTagGroupIdGroups = (Set<Long>) aCall.getArguments().getObject("?uTagGroupIdGroups");
        Set<Long> tTagGroupIdGroups = (Set<Long>) aCall.getArguments().getObject("?tTagGroupIdGroups");
        
        Optional<Long> query = uTagGroupIdGroups.stream()
            .filter(id -> {
                return tTagGroupIdGroups.contains(id);
            })
            .findAny();
        
        if(query.isPresent()) {
            Set<String> context = (Set<String>) aCall.getContext();
            context.add(tag);
        }
    }

    @Override
    public void complete(FlowProcess fProcess, AggregatorCall aCall) {
        _LOGGER.info("UserIdToTagAggregator.complete!!!!!!!!!!!!!!!!!! " + aCall.getContext());
        
        Set<String> context = (Set<String>) aCall.getContext();
        aCall.getOutputCollector().add(new Tuple(context));
    }
    
}
