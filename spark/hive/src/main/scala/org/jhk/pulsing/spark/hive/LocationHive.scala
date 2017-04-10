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

import org.apache.log4j.{Level, LogManager, PropertyConfigurator}
import org.apache.spark.SparkConf
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.rdd._

import org.apache.spark.sql.SparkSession

import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.Schema
import org.apache.avro.mapred.{AvroValue, AvroKey}
import org.apache.avro.mapreduce.{AvroKeyValueOutputFormat, AvroJob}

import org.jhk.pulsing.shared.util.CommonConstants._
import org.jhk.pulsing.shared.util.HadoopConstants
import org.jhk.pulsing.serialization.avro.records.Location
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper

/**
 * @author Ji Kim
 */
object LocationHive {
  
  def createSparkContext() = {
    val configuration = new SparkConf().setMaster(PROJECT_POINT).setAppName("location-hive")
    val sparkContext = new SparkContext(configuration);
    
    sparkContext
  }
  
  def main(args: Array[String]): Unit = {
    
    val sparkContext = createSparkContext
    val logger = LogManager.getRootLogger
    logger.info("Starting Location Hive...")
    
    val sparkSession = SparkSession.builder.master(SPARK_YARN_CLUSTER_MASTER)
      .appName("Location Create Hive")
      .enableHiveSupport()
      .getOrCreate()
    
  }
  
}
