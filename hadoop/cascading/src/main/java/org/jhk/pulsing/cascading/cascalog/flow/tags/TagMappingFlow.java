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

import static org.jhk.pulsing.shared.util.HadoopConstants.WORKING_DIRECTORIES.*;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.jhk.pulsing.cascading.cascalog.flow.tags.functions.TagToTagGroupAggregator;
import org.jhk.pulsing.cascading.cascalog.flow.tags.functions.UserIdToTagAggregator;
import org.jhk.pulsing.cascading.cascalog.flow.tags.functions.UserIdToTagGroupAggregator;
import org.jhk.pulsing.cascading.cascalog.function.EmitDataUnitFieldFunction;
import org.jhk.pulsing.cascading.cascalog.function.EmitDataUnitFieldFunction.EMIT_DATA_UNIT_FIELD;
import org.jhk.pulsing.pail.common.PailTapUtil;
import org.jhk.pulsing.pail.thrift.structures.SplitDataPailStructure;
import org.jhk.pulsing.serialization.thrift.data.DataUnit;
import org.jhk.pulsing.shared.util.HadoopConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backtype.cascading.tap.PailTap;

import cascading.tap.Tap;
import jcascalog.Api;
import jcascalog.Subquery;

/**
 * @author Ji Kim
 */
public final class TagMappingFlow {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(TagMappingFlow.class);
    
    private static final String _TEMP_TAG_GROUP_MAPPING_DIR = HadoopConstants.getWorkingDirectory(TEMP, TAG_GROUP_MAPPING);
    private static final String _TAG_TO_TAG_GROUP = "TagToTagGroup";
    private static final String _USERID_TO_TAG_GROUP = "UserIdToTagGroup";
    private static final String _USERID_TO_TAG = "UserIdToTag";
    
    /**
     * From previous steps:
     * 1) tag => set(tagGroupIds...)
     * 2) userId => set(tagGroupIds...)
     * 
     * Now generate 
     * userId => set(tag...)
     * 
     * @throws IOException
     */
    public static void mapUserToTagSet() throws IOException {
        
        Configuration config = new Configuration();
        String fsDefault = config.get(HadoopConstants.CONFIG_FS_DEFAULT_KEY);
        
        Map userIdToTaggroupMap = (Map) Api.hfsSeqfile(_TEMP_TAG_GROUP_MAPPING_DIR + _USERID_TO_TAG_GROUP);
        Map tagToTaggroupMap = (Map) Api.hfsSeqfile(_TEMP_TAG_GROUP_MAPPING_DIR + _TAG_TO_TAG_GROUP);
        
        Tap userIdToTaggroup = (Tap) userIdToTaggroupMap.get(HadoopConstants.CASCALOG_HFS_SEQFILE_SOURCE_KEY);
        Tap tagToTaggroup = (Tap) tagToTaggroupMap.get(HadoopConstants.CASCALOG_HFS_SEQFILE_SINK_KEY);
        
        Api.execute(Api.hfsSeqfile(_TEMP_TAG_GROUP_MAPPING_DIR + _USERID_TO_TAG), 
                    new Subquery("?userId", "?tags")
                    .predicate(userIdToTaggroup, "?userId", "?uTagGroupIdGroups")
                    .predicate(tagToTaggroup, "?tag", "?tTagGroupIdGroups")
                    .predicate(new UserIdToTagAggregator(), "?userId", "?uTagGroupIdGroups", "?tag", "?tTagGroupIdGroups")
                        .out("?tags"));
    }
    
    /**
     * For all TagGroupUserEdge group by userId and aggregate the tagGroups that the user belongs in
     * 
     * userId => set(tagGroupIds...)
     * 
     * Next step is mapUserToTagSet
     * 
     * @throws IOException
     */
    public static void mapUserToTagGroupSet() throws IOException {
        _LOGGER.info("TagMappingFlow.mapUserToTagGroupSet");
        
        Configuration config = new Configuration();
        String fsDefault = config.get(HadoopConstants.CONFIG_FS_DEFAULT_KEY);
        
        PailTap tagGroupUserEdge = PailTapUtil.attributetap(fsDefault + HadoopConstants.PAIL_MASTER_WORKSPACE, 
                new SplitDataPailStructure(),
                DataUnit._Fields.TAGGROUPUSER_EDGE);
        
        Api.execute(Api.hfsSeqfile(_TEMP_TAG_GROUP_MAPPING_DIR + _USERID_TO_TAG_GROUP), 
                new Subquery("?userId", "?tagGroupIdGroups")
                .predicate(tagGroupUserEdge, "_", "?data")
                .predicate(new EmitDataUnitFieldFunction(EMIT_DATA_UNIT_FIELD.TAG_GROUP_USER_EDGE), "?data")
                    .out("?tagGroupId", "_", "_", "?userId")
                .predicate(new UserIdToTagGroupAggregator(), "?userId", "?tagGroupId")
                    .out("?tagGroupIdGroups"));
    }
    
    /**
     * Map similar tagGroups together. Meaning for now if tag is shared between two different tagGroup and location is within the threshold, 
     * then consider them to overlap and link them together. 
     * 
     * This will result in tag => set(tagGroupIds...)
     * 
     * Next step is the mapUserToTagGroupSet
     * 
     * CHANGE. Rather than tag => set(tagGroupIds...) want tagGroupIds => set(tag...) reason is how to group by userId => tagGroupIds
     * 
     * Ideally want tagGroupIds => set(tag...) so 
     * for the final FUNCTION, emit tagGroupId1 -> tagGroupId2 and vice versa (if the lat + lng are in the proximity)
     * tagGroupId1 -> tagGroupId2  (meaning have tagGroupId1 have all of its tags + also of tagGroupId2's)/
     * tagGroupId2 -> tagGroupId1  
     * 
     * Then have a grouping of tagGroupIds
     * tagGroupId1 -> tagGroupId2
     *             -> tagGroupId3
     * 
     * Then have an another query of tagGroupId -> tags and aggregate all together
     * 
     * @throws IOException
     */
    public static void mapTagToTagGroupSet() throws IOException {
        _LOGGER.info("TagMappingFlow.mapTagToTagGroupSet");
        
        Configuration config = new Configuration();
        String fsDefault = config.get(HadoopConstants.CONFIG_FS_DEFAULT_KEY);
        FileSystem fSystem = FileSystem.get(config);
        
        fSystem.delete(new Path(_TEMP_TAG_GROUP_MAPPING_DIR), true);
        fSystem.mkdirs(new Path(_TEMP_TAG_GROUP_MAPPING_DIR));
        
        PailTap tagProperties = PailTapUtil.attributetap(fsDefault + HadoopConstants.PAIL_MASTER_WORKSPACE, 
                new SplitDataPailStructure(),
                DataUnit._Fields.TAGGROUP_PROPERTY);
        
        Api.execute(Api.hfsSeqfile(_TEMP_TAG_GROUP_MAPPING_DIR + _TAG_TO_TAG_GROUP), 
                new Subquery("?tag", "?tagGroupIdGroups")
                .predicate(tagProperties, "_", "?data")
                .predicate(tagProperties, "_", "?data2")
                .predicate(new EmitDataUnitFieldFunction(EMIT_DATA_UNIT_FIELD.TAG_GROUP_PROPERTY), "?data")
                    .out("?tagGroupId", "?lat", "?lng", "?tag")
                .predicate(new EmitDataUnitFieldFunction(EMIT_DATA_UNIT_FIELD.TAG_GROUP_PROPERTY), "?data2")
                    .out("?tagGroupId2", "?lat2", "?lng2", "?tag")
                .predicate(new TagToTagGroupAggregator(), "?tag", "?tagGroupId", "?lat", "?lng", "?tagGroupId2", "?lat2", "?lng2")
                    .out("?tagGroupIdGroups"));
        
    }
    
    private TagMappingFlow() {
        super();
    }
    
}
