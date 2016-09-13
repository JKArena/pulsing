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
package org.jhk.pulsing.cascading.hadoop.job.pail;

import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.jhk.pulsing.pail.common.PailTapUtil;
import org.jhk.pulsing.shared.util.HadoopConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backtype.cascading.tap.PailTap;
import com.twitter.maple.tap.StdoutTap;

import jcascalog.Api;
import jcascalog.Subquery;
import jcascalog.op.Count;

/**
 * @author Ji Kim
 */
public final class TagJob {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(TagJob.class);
    
    public static void main(String[] args) {
        _LOGGER.info("TagJob " + Arrays.toString(args));
        
        try {
            Configuration config = new Configuration();
            String fsDefault = config.get(HadoopConstants.CONFIG_FS_DEFAULT_KEY);
            PailTap master = PailTapUtil.splitDataTap(fsDefault + HadoopConstants.PAIL_MASTER_WORKSPACE);
            
            /*
             * Fields whose names start with a question mark (?) are non-nullable. If JCascalog encounters
             * a tuple with a null value for a non-nullable field, it's immediately filtered from the working 
             * dataset. Conversely, field names beginning with an exclamation mark (!) may contain null values.
             * 
             * Additionally field names starting with a double exclamation mark (!!) are also nullable and 
             * are needed to perform outer joins between datasets. For joins involving these kinds of field names, 
             * records that do not satisfy the join condition between datasets are still included in the result set, 
             * but with null values for these fields where data is not present.
             * 
             * The underscore informs JCascalog to ignore this field =>
             * predicate(SOMETHING, "?stuff", "_")
             */
            Api.execute(new StdoutTap(), 
                        new Subquery("?count")
                        .predicate(master, "_", "?raw")
                        .predicate(new Count(), "?count")
                        );
            
        } catch (Exception exception) {
            _LOGGER.error("Crud something went wrong!!!!!!!!!!");
            exception.printStackTrace();
        }
    }
    
}
