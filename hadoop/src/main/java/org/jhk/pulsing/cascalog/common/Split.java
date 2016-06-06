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
package org.jhk.pulsing.cascalog.common;

import cascading.flow.FlowProcess;
import cascading.operation.FunctionCall;
import cascading.tuple.Tuple;
import cascalog.CascalogFunction;

/**
 * @author Ji Kim
 */
public final class Split extends CascalogFunction {

    private static final long serialVersionUID = 1710613680664690784L;
    
    private String _delim;
    
    public Split(String delim) {
        super();
        
        _delim = delim;
    }

    @Override
    public void operate(FlowProcess process, FunctionCall call) {
        
        String sentence = call.getArguments().getString(0);
        for(String word : sentence.split(_delim)) {
            call.getOutputCollector().add(new Tuple(word));
        }
        
    }

}
