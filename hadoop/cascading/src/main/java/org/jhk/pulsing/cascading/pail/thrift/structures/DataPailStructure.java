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
package org.jhk.pulsing.cascading.pail.thrift.structures;

import java.util.Collections;
import java.util.List;

import org.jhk.pulsing.cascading.pail.thrift.AbstractThriftPailStructure;
import org.jhk.pulsing.serialization.thrift.data.Data;

/**
 * @author Ji Kim
 */
public class DataPailStructure extends AbstractThriftPailStructure<Data> {

    private static final long serialVersionUID = 2750979512797745877L;

    @Override
    public boolean isValidTarget(String... dirs) {
        return true;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<String> getTarget(Data object) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Class<Data> getType() {
        return Data.class;
    }

    @Override
    public Data createThriftObject() {
        return new Data();
    }

}
