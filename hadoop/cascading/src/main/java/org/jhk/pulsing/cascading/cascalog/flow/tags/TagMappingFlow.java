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
package org.jhk.pulsing.cascading.cascalog.flow.tags;

import static org.jhk.pulsing.shared.util.HadoopConstants.DIRECTORIES.*;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.jhk.pulsing.cascading.cascalog.function.EmitDataUnitFieldFunction;
import org.jhk.pulsing.cascading.cascalog.function.EmitDataUnitFieldFunction.EMIT_DATA_UNIT_FIELD;
import org.jhk.pulsing.pail.common.PailTapUtil;
import org.jhk.pulsing.pail.thrift.structures.SplitDataPailStructure;
import org.jhk.pulsing.serialization.thrift.data.DataUnit;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.shared.util.HadoopConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backtype.cascading.tap.PailTap;
import com.twitter.maple.tap.StdoutTap;

import cascading.flow.FlowProcess;
import cascading.operation.FunctionCall;
import cascading.tuple.Tuple;
import cascalog.CascalogFunction;
import jcascalog.Api;
import jcascalog.Subquery;

/**
 * @author Ji Kim
 */
public final class TagMappingFlow {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(TagMappingFlow.class);
    
    private static final String _TEMP_TAG_GROUP_MAPPING_DIR = HadoopConstants.getWorkingDirectory(TEMP, TAG_GROUP_MAPPING);
    
    /**
     * Map similar tagGroups together. Meaning if two tagGroup's share a threshold number of tags together, then consider them to 
     * overlap and link them together. Then create a link for userId => [set(tagGroupIds...tags)] using the coordinates so to suggest to 
     * user in the mapcomponent and etc when a pulse with similar tag gets posted.
     * 
     * @throws IOException
     */
    public static void mapTagGroups() throws IOException {
        _LOGGER.info("TagMappingFlow.mapTagGroups");
        
        Configuration config = new Configuration();
        String fsDefault = config.get(HadoopConstants.CONFIG_FS_DEFAULT_KEY);
        FileSystem fSystem = FileSystem.get(config);
        
        fSystem.delete(new Path(_TEMP_TAG_GROUP_MAPPING_DIR), true);
        fSystem.mkdirs(new Path(_TEMP_TAG_GROUP_MAPPING_DIR));
        
        PailTap tagProperties = PailTapUtil.attributetap(fsDefault + HadoopConstants.PAIL_MASTER_WORKSPACE, 
                new SplitDataPailStructure(),
                DataUnit._Fields.TAGGROUP_PROPERTY);
        
        Api.execute(//Api.hfsSeqfile(_TEMP_TAG_GROUP_MAPPING_DIR + "First"), 
                new StdoutTap(),
                new Subquery("?tagGrouped", "?tagGroupIdGroup", "?tagGroupId2Group")
                .predicate(tagProperties, "_", "?data")
                .predicate(tagProperties, "_", "?data2")
                .predicate(new EmitDataUnitFieldFunction(EMIT_DATA_UNIT_FIELD.TAG_GROUP_PROPERTY), "?data")
                    .out("?tagGroupId", "?lat", "?lng", "?tag")
                .predicate(new EmitDataUnitFieldFunction(EMIT_DATA_UNIT_FIELD.TAG_GROUP_PROPERTY), "?data2")
                    .out("?tagGroupId2", "?lat2", "?lng2", "?tag")
                .predicate(new EqualTagProcessor(), "?tag", "?tagGroupId", "?lat", "?lng", "?tagGroupId2", "?lat2", "?lng2")
                    .out("?tagGrouped", "?tagGroupIdGroup", "?tagGroupId2Group"));
        
    }
    
    static final class EqualTagProcessor extends CascalogFunction {
        
        private static final long serialVersionUID = -2155839044472884960L;

        @Override
        public void operate(FlowProcess fProcess, FunctionCall fCall) {
            _LOGGER.info("EqualTagProcessor.operate");
            
            String tag = fCall.getArguments().getString("?tag");
            
            _LOGGER.info("EqualTagProcessor.operate " + tag);
            
            long tagGroupId = fCall.getArguments().getLong("?tagGroupId");
            long tagGroupId2 = fCall.getArguments().getLong("?tagGroupId2");
            
            _LOGGER.info("EqualTagProcessor.operate " + tagGroupId + "/" + tagGroupId2);
            
            double lat = fCall.getArguments().getDouble("?lat");
            double lng = fCall.getArguments().getDouble("?lng");
            double lat2 = fCall.getArguments().getDouble("?lat2");
            double lng2 = fCall.getArguments().getDouble("?lng2");
            
            double latR = lat-lat2;
            double lngR = lng-lng2;
            double distance = Math.sqrt((latR*latR) + (lngR*lngR));
            
            if(tagGroupId != tagGroupId2 && distance < CommonConstants.DEFAULT_PULSE_RADIUS) {
                _LOGGER.info("EqualTagProcessor.operate emitting - " + tag + ":" + tagGroupId + "/" + tagGroupId2);
                fCall.getOutputCollector().add(new Tuple(tag, tagGroupId, tagGroupId2));
            }
        }
        
    }
    
    private TagMappingFlow() {
        super();
    }
    
}
