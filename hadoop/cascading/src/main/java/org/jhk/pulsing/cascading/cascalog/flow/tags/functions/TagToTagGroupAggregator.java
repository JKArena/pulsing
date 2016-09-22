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

import org.jhk.pulsing.shared.util.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.flow.FlowProcess;
import cascading.operation.AggregatorCall;
import cascading.tuple.Tuple;
import cascalog.CascalogAggregator;

/**
 * @author Ji Kim
 */
public final class TagToTagGroupAggregator extends CascalogAggregator {
    
    private static final long serialVersionUID = 7987092184714949979L;
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(TagToTagGroupAggregator.class);

    @Override
    public void start(FlowProcess fProcess, AggregatorCall aCall) {
        _LOGGER.info("TagToTagGroupAggregator.start");
        
        aCall.setContext(new HashSet<Long>());
    }
    
    @Override
    public void aggregate(FlowProcess fProcess, AggregatorCall aCall) {
        String tag = aCall.getArguments().getString("?tag");
        
        _LOGGER.info("TagToTagGroupAggregator.aggregate " + tag);
        
        long tagGroupId = aCall.getArguments().getLong("?tagGroupId");
        long tagGroupId2 = aCall.getArguments().getLong("?tagGroupId2");
        
        double lat = aCall.getArguments().getDouble("?lat");
        double lng = aCall.getArguments().getDouble("?lng");
        double lat2 = aCall.getArguments().getDouble("?lat2");
        double lng2 = aCall.getArguments().getDouble("?lng2");
        
        double latR = lat-lat2;
        double lngR = lng-lng2;
        double distance = Math.sqrt((latR*latR) + (lngR*lngR));
        
        if(distance < CommonConstants.DEFAULT_PULSE_RADIUS) {
            _LOGGER.info("TagToTagGroupAggregator.aggregate adding - " + tag + ":" + tagGroupId + "/" + tagGroupId2);
            
            Set<Long> context = (Set<Long>) aCall.getContext();
            context.add(tagGroupId);
            context.add(tagGroupId2);
        }
    }

    @Override
    public void complete(FlowProcess fProcess, AggregatorCall aCall) {
        _LOGGER.info("TagToTagGroupAggregator.complete!!!!!!!!!!!!!!!!!! " + aCall.getContext());
        
        Set<Long> context = (Set<Long>) aCall.getContext();
        aCall.getOutputCollector().add(new Tuple(context));
    }
    
}
