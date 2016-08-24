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

import java.net.URI;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.jhk.pulsing.pail.common.PailUtil;
import org.jhk.pulsing.pail.thrift.structures.DataPailStructure;
import org.jhk.pulsing.pail.thrift.structures.SplitDataPailStructure;
import org.jhk.pulsing.shared.util.HadoopConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backtype.hadoop.pail.Pail;

/**
 * @author Ji Kim
 */
public final class PailNewDataIngestorJob {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(PailNewDataIngestorJob.class);
    
    public static void main(String[] args) {
        _LOGGER.debug("PailNewDataIngestorJob " + Arrays.toString(args));
        
        try {
            Configuration config = new Configuration();
            String fsDefault = config.get(HadoopConstants.CONFIG_FS_DEFAULT_KEY);
            URI pNewDataUri = URI.create(fsDefault + HadoopConstants.PAIL_NEW_DATA_WORKSPACE);
            
            FileSystem fs = FileSystem.get(pNewDataUri, config);
            Path pNewDataPath = new Path(pNewDataUri);
            
            Pail<SplitDataPailStructure> pMasterData = new Pail<>(fsDefault + HadoopConstants.PAIL_MASTER_WORKSPACE);
            
            //listing it out separately for now, but should go to same pail
            FileStatus[] fStatus = fs.listStatus(pNewDataPath);
            for(FileStatus status : fStatus) {
                Pail<DataPailStructure> pp = new Pail<>(status.getPath().toString());
                PailUtil.ingest(pMasterData, pp);
            }
            
        } catch (Exception exception) {
            _LOGGER.error("Crud something went wrong!!!!!!!!!!");
            exception.printStackTrace();
        }
        
    }
    
}
