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
package org.jhk.pulsing.giraph.computation;

import org.apache.giraph.conf.GiraphConfiguration;
import org.apache.giraph.io.formats.GiraphFileInputFormat;
import org.apache.giraph.job.GiraphJob;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.jhk.pulsing.giraph.context.FriendTextWorkerContext;
import org.jhk.pulsing.giraph.inputFormat.FriendTextVertexInputFormat;
import org.jhk.pulsing.giraph.masterCompute.FriendTextMasterCompute;
import org.jhk.pulsing.giraph.outputFormat.FriendTextVertexOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initial playing around with Giraph, work with Avro and different/proper SerDe later
 * @author Ji Kim
 */
public final class FriendTextJob implements Tool {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(FriendTextJob.class);
    
    private Configuration conf;

    @Override
    public Configuration getConf() {
        return conf;
    }

    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 3) {
            throw new IllegalArgumentException("Syntax error: Must have 3 arguments <numberOfWorkers> <inputLocation> <outputLocation>");
        }
        
        int numberOfWorkers = Integer.parseInt(args[0]);
        String inputLocation = args[1];
        String outputLocation = args[2];
        
        GiraphJob job  = new GiraphJob(getConf(), getClass().getName());
        GiraphConfiguration gConf = job.getConfiguration();
        gConf.setWorkerConfiguration(numberOfWorkers, numberOfWorkers, 100.0f);
        
        GiraphFileInputFormat.addVertexInputPath(gConf, new Path(inputLocation));
        FileOutputFormat.setOutputPath(job.getInternalJob(), new Path(outputLocation));
        
        gConf.setComputationClass(FriendTextComputation.class);
        gConf.setMasterComputeClass(FriendTextMasterCompute.class);
        gConf.setVertexInputFormatClass(FriendTextVertexInputFormat.class);
        gConf.setVertexOutputFormatClass(FriendTextVertexOutputFormat.class);
        gConf.setWorkerContextClass(FriendTextWorkerContext.class);
        
        if (job.run(true)) {
            return 0;
        } else {
            return -1;
        }
    }
    
    public static void main(String[] args) throws Exception {
        int ret = ToolRunner.run(new FriendTextJob(), args);
        if (ret == 0) {
            _LOGGER.info("Done");
        } else {
            _LOGGER.error("Failure");
        }
        System.exit(ret);
    }

}
