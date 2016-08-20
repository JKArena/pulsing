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
import org.jhk.pulsing.pail.thrift.structures.DataPailStructure;
import org.jhk.pulsing.pail.thrift.structures.SplitDataPailStructure;
import org.jhk.pulsing.shared.util.HadoopConstants;
import org.jhk.pulsing.shared.util.HadoopConstants.PAIL_NEW_DATA_PATH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backtype.hadoop.pail.Pail;

/**
 * Since have been deleting and restarting from fresh for all db and etc, 
 * just a convenience job for creating new setup.
 * 
 * @author Ji Kim
 */
public final class PailReinitJob {
    
    private static final Logger _LOG = LoggerFactory.getLogger(PailReinitJob.class);
    
    public static void main(String[] args) {
        _LOG.info("PailReinitJob " + Arrays.toString(args));
        
        try {
            Configuration config = new Configuration();
            String fsDefault = config.get(HadoopConstants.CONFIG_FS_DEFAULT_KEY);
            
            for(PAIL_NEW_DATA_PATH pNewDataPath : HadoopConstants.PAIL_NEW_DATA_PATH.values()) {
                Pail.create(fsDefault + HadoopConstants.PAIL_NEW_DATA_WORKSPACE + pNewDataPath.toString(), new DataPailStructure());
            }
            
            Pail.create(fsDefault + HadoopConstants.PAIL_MASTER_WORKSPACE, new SplitDataPailStructure());
            
        } catch (Exception exception) {
            _LOG.error("Crud something went wrong!!!!!!!!!!");
            exception.printStackTrace();
        }
        
    }
    
}
