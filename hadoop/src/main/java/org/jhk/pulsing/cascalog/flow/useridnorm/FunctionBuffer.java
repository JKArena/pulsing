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
package org.jhk.pulsing.cascalog.flow.useridnorm;

import java.util.Iterator;
import java.util.TreeSet;

import org.jhk.pulsing.serialization.thrift.data.Data;
import org.jhk.pulsing.serialization.thrift.edges.EquivEdge;
import org.jhk.pulsing.serialization.thrift.id.UserId;

import cascading.flow.FlowProcess;
import cascading.operation.BufferCall;
import cascading.operation.FunctionCall;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;
import cascalog.CascalogBuffer;
import cascalog.CascalogFunction;

/**
 * @author Ji Kim
 */
final class FunctionBuffer {
    
    private FunctionBuffer() {
        super();
    }
    
    static final class EdgifyEquiv extends CascalogFunction {

        private static final long serialVersionUID = 4912878873484962976L;

        @Override
        public void operate(FlowProcess process, FunctionCall call) {
            Data data = (Data) call.getArguments().getObject(0);
            EquivEdge equiv = data.getDataunit().getEquiv();
            call.getOutputCollector().add(new Tuple(equiv.getFirstId(), equiv.getSecondId()));
        }

    }
    
    static final class BidirectionalEdge extends CascalogFunction {
        
        private static final long serialVersionUID = -2145839044472884960L;

        @Override
        public void operate(FlowProcess process, FunctionCall call) {
            Object node1 = call.getArguments().getObject(0);
            Object node2 = call.getArguments().getObject(1);
            
            if(!node1.equals(node2)) {
                call.getOutputCollector().add(new Tuple(node1, node2));
                call.getOutputCollector().add(new Tuple(node2, node1));
            }
            
        }
        
    }
    
    static final class IterateEdges extends CascalogBuffer {
        
        private static final long serialVersionUID = -4864578917622894948L;

        @Override
        public void operate(FlowProcess process, BufferCall call) {
            
            UserId grouped = (UserId) call.getGroup().getObject(0);
            TreeSet<UserId> allIds = new TreeSet<UserId>();
            allIds.add(grouped);
            
            Iterator<TupleEntry> iterate = call.getArgumentsIterator();
            
            while(iterate.hasNext()) {
                allIds.add((UserId) iterate.next().getObject(0));
            }
            
            Iterator<UserId> allIdsIterate = allIds.iterator();
            UserId smallest = allIdsIterate.next();
            
            boolean progress = allIds.size() > 2 && !grouped.equals(smallest);
            
            while(allIdsIterate.hasNext()) {
                UserId id = allIdsIterate.next();
                call.getOutputCollector().add(new Tuple(smallest, id, progress));
            }
        }
        
    }
    
}
