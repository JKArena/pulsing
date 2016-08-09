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
package org.jhk.pulsing.hadoop.job;

import static org.jhk.pulsing.shared.util.HadoopConstants.DIRECTORIES.SHREDDED;
import static org.jhk.pulsing.shared.util.HadoopConstants.DIRECTORIES.TEMP;

import java.io.IOException;
import java.util.Arrays;

import org.jhk.pulsing.pail.common.PailTap;
import org.jhk.pulsing.pail.common.PailTapUtil;
import org.jhk.pulsing.shared.util.HadoopConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class NewDataIngestorJob {
    
    private static final Logger _LOG = LoggerFactory.getLogger(NewDataIngestorJob.class);
    
    public static void main(String[] args) {
        _LOG.info("NewDataIngestorJob " + Arrays.toString(args));
        
        try {
            PailTap masterPT = new PailTap(HadoopConstants.HDFS_URL_PORT + HadoopConstants.MASTER_WORKSPACE);
            PailTap newDataPT = new PailTap(HadoopConstants.HDFS_URL_PORT + HadoopConstants.NEW_DATA_WORKSPACE);
            
            PailTapUtil.shred(HadoopConstants.HDFS_URL_PORT + HadoopConstants.NEW_DATA_WORKSPACE, HadoopConstants.getWorkingDirectory(TEMP, SHREDDED));
            
        } catch (IOException ioException) {
            _LOG.error("Crud something went wrong!!!!!!!!!!");
            ioException.printStackTrace();
        }
        
    }
    
}
