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
package org.jhk.pulsing.spark.streaming

/**
 * Do not run Spark Streaming programs locally with master configured as "local" or "local[1]". This allocates only one CPU for tasks and if 
 * a receiver is running on it, there is no resource left to process the received data. Use at least "local[2]" to have more cores
 * 
 * One of the main advantage of Spark Streaming is that it provides strong fault tolerance guarantees. As long as the input data is stored 
 * reliably, Spark Streaming will always compute the correct result from it, offering "exactly once" semantics (i.e. as if all of the data 
 * was processed withut any nodes failing), even if workers or the driver fail.
 * 
 * Tolerating failures of the driver node requires a special way of creating our StreamingContext, which takes in the checkpoint directory. 
 * Instead of simply calling new StreamingContext, we need to use the StreamingContext.getOrCreate() function. Checkpointing is the main 
 * mechanism that needs to be set up for fault tolerance in Spark Streaming. It allows Spark Streaming to periodically save data about the 
 * application to a reliable storage system, such as HDFS or Amazon S3, for use in recovering. Checkpointing serves 2 purpose
 * 
 * 1) Limiting the stat that must be recomputed on failure. Spark Streaming can recompute state using the lineage graph of transformations, 
 * but checkpointing controls how far back it must go.
 * 2) Providing fault tolerance for the driver. If the driver program in a streaming application crashes, you can launch it again and tell it 
 * to recover from a checkpoint, in which case Spark Streaming will read how far the previous run of the program got in processing the data 
 * and take over from there
 * 
 * In addition to writing your initialization code using getOrCreate(), you will need to actually restart your driver program when it crashes.
 * On most cluster managers, Spark does not automatically relaunch the driver if it crashes, so you need to monitor it using a tool like monit and 
 * restart it.
 * 
 * @author Ji Kim
 */
