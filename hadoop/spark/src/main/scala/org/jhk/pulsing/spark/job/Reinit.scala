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
package org.jhk.pulsing.spark.job

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path

import org.jhk.pulsing.shared.util.HadoopConstants
import org.jhk.pulsing.shared.util.HadoopConstants.DIRECTORIES._

/**
 * Simple reinit like hadoop/cascading as the cleanUp.sh script clears out the data for disk usage
 * 
 * @author Ji Kim
 */
class Reinit {
  println("Testing");
  val fSystem = FileSystem.get(new Configuration());
  
  fSystem.mkdirs(new Path(HadoopConstants.SPARK_MASTER_WORKSPACE))
  fSystem.mkdirs(new Path(HadoopConstants.SPARK_NEW_DATA_WORKSPACE))
  
}
