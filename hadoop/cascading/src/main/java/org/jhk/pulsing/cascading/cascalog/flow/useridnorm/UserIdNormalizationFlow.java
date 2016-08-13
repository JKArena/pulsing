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
package org.jhk.pulsing.cascading.cascalog.flow.useridnorm;

import org.apache.hadoop.mapred.JobConf;

import java.io.IOException;
import java.util.Arrays;

import static org.jhk.pulsing.cascading.cascalog.flow.useridnorm.FunctionBuffer.*;

import org.jhk.pulsing.serialization.thrift.data.DataUnit;
import org.jhk.pulsing.shared.util.HadoopConstants;

import com.backtype.pail.common.PailTapUtil;

import static org.jhk.pulsing.shared.util.HadoopConstants.DIRECTORIES.*;

import cascading.flow.FlowProcess;
import cascading.flow.hadoop.HadoopFlowProcess;
import cascading.tap.Tap;
import jcascalog.Api;
import jcascalog.Fields;
import jcascalog.Option;
import jcascalog.Subquery;

/**
 * @author Ji Kim
 */
public final class UserIdNormalizationFlow {
    
    private static final String _TEMP_EQUIVS_ITERATE_DIR = HadoopConstants.getWorkingDirectory(TEMP, EQUIVS_ITERATE);
    
    private UserIdNormalizationFlow() {
        super();
    }
    
    public static void normalizeUserIds() throws IOException {
        
        initializeUserIdNormalization();
        int numberOfIterations = userIdNormalizationIterationLoop(); //temp path with this iteration is the final one to use
    }
    
    private static void initializeUserIdNormalization() {
        
        Tap equivs = PailTapUtil.attributetap(HadoopConstants.MASTER_WORKSPACE, 
                                                DataUnit._Fields.EQUIV);
        
        Api.execute(Api.hfsSeqfile(_TEMP_EQUIVS_ITERATE_DIR + "0"), 
                    new Subquery("?node1", "?node2")
                        .predicate(equivs, "_", "?data")
                        .predicate(new EdgifyEquiv(), "?node1", "?node2"));
    }
    
    private static Subquery iterationQuery(Tap source) {
        
        Subquery iterate = new Subquery("?b1", "?node1", "?node2", "?is-new")
                                        .predicate(source, "?n1","?n2")
                                        .predicate(new BidirectionalEdge(), "?n1", "?n2")
                                            .out("?b1", "?b2")
                                        .predicate(new IterateEdges(), "?b2")
                                            .out("?node1", "?node2", "?is-new");
        
        return (Subquery) Api.selectFields(iterate, new Fields("?node1", "?node2", "is-new"));
    }
    
    private static Tap userIdNormalizationIteration(int i) {
        Tap source = (Tap) Api.hfsSeqfile(_TEMP_EQUIVS_ITERATE_DIR + (i-1));
        Tap sink = (Tap) Api.hfsSeqfile(_TEMP_EQUIVS_ITERATE_DIR + i);
        
        Tap progressSink = (Tap) Api.hfsSeqfile(_TEMP_EQUIVS_ITERATE_DIR + "-new");
        
        Subquery iteration = iterationQuery(source);
        Subquery newEdgeSet = new Subquery("?node1", "?node2")
                                            .predicate(iteration, "?node1", "?node2", "_")
                                            .predicate(Option.DISTINCT, true);
        
        Subquery progressEdges = new Subquery("?node1", "?node2")
                                                .predicate(iteration, "?node1", "?node2", true);
        
        Api.execute(Arrays.asList(sink, progressSink), Arrays.asList(newEdgeSet, progressEdges));
        
        return progressSink;
    }
    
    private static int userIdNormalizationIterationLoop() throws IOException {
        int iteration = 1;
        
        while(true) {
            Tap progressEdgesSink = userIdNormalizationIteration(iteration);
            FlowProcess flowProcess = new HadoopFlowProcess(new JobConf());
            
            if(!flowProcess.openTapForRead(progressEdgesSink).hasNext()) {
                return iteration;
            }
            iteration++;
        }
        
    }
    
}
