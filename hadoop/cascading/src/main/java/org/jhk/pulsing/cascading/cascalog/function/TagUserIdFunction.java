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
package org.jhk.pulsing.cascading.cascalog.function;

import org.jhk.pulsing.serialization.thrift.data.Data;
import org.jhk.pulsing.serialization.thrift.edges.TagEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.flow.FlowProcess;
import cascading.operation.FunctionCall;
import cascading.tuple.Tuple;
import cascalog.CascalogFunction;

/**
 * @author Ji Kim
 */
public final class TagUserIdFunction extends CascalogFunction {
    
    private static final long serialVersionUID = -4684623393961746785L;
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(TagUserIdFunction.class);

    @Override
    public void operate(FlowProcess fProcess, FunctionCall fCall) {
        _LOGGER.info("TagUserIdFunction.operate");
        
        Data data = (Data) fCall.getArguments().getObject(0);
        TagEdge tEdge = data.getDataunit().getTag();
        
        _LOGGER.info("TagUserIdFunction.operate tag/userId " + tEdge.getTagId().getTag() + "/" + tEdge.getUserId().getId());
        fCall.getOutputCollector().add(new Tuple(tEdge.getTagId().getTag(), tEdge.getUserId().getId()));
    }

}
