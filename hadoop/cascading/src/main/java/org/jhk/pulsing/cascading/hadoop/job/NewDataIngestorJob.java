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
package org.jhk.pulsing.cascading.hadoop.job;

import static org.jhk.pulsing.shared.util.HadoopConstants.DIRECTORIES.SHREDDED;
import static org.jhk.pulsing.shared.util.HadoopConstants.DIRECTORIES.TEMP;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.serializer.WritableSerialization;
import org.jhk.pulsing.cascading.hadoop.config.ThriftSerialization;
import org.jhk.pulsing.shared.util.HadoopConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backtype.pail.common.PailTapUtil;

import jcascalog.Api;

/**
 * @author Ji Kim
 */
public final class NewDataIngestorJob {
    
    private static final Logger _LOG = LoggerFactory.getLogger(NewDataIngestorJob.class);
    
    public static void main(String[] args) {
        _LOG.info("NewDataIngestorJob " + Arrays.toString(args));
        
        try {
            
            setApplicationConfig();
            
            Configuration config = new Configuration();
            System.out.println("CHECK " + config.get("fs.defaultFS"));
            URI uri = URI.create(config.get("fs.defaultFS") + "/data/newdata");
            System.out.println("URI is " + uri);
            FileSystem fs = FileSystem.get(uri, config);
            
            System.out.println("FS " + fs.getName() + " : " + fs.getWorkingDirectory().getName() + ", " + fs.getWorkingDirectory() + " * " + 
            fs.getHomeDirectory().getName() + ", " + fs.getHomeDirectory());
            //FS hdfs://0.0.0.0 : jikim, hdfs://0.0.0.0/user/jikim * jikim, hdfs://0.0.0.0/user/jikim
            //PailTapUtil.shred(HadoopConstants.HDFS_URL_PORT + HadoopConstants.NEW_DATA_WORKSPACE, HadoopConstants.getWorkingDirectory(TEMP, SHREDDED));
            
        } catch (Exception exception) {
            _LOG.error("Crud something went wrong!!!!!!!!!!");
            exception.printStackTrace();
        }
        
    }
    
    public static void setApplicationConfig() {
        Map<String, String> config = new HashMap<>();
        
        config.put("io.serializations", ThriftSerialization.class.getName() + "," + WritableSerialization.class.getName());
        Api.setApplicationConf(config);
    }
    
}
