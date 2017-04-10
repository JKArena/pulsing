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
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.TaskContext

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.sql.SparkSession

import org.jhk.pulsing.shared.util.CommonConstants._
import org.jhk.pulsing.shared.util.HadoopConstants
import org.jhk.pulsing.serialization.avro.records.edge.FriendEdge
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper

/**
 * @author Ji Kim
 */
object FriendStreaming {
  val CHECKPOINT = HadoopConstants.getWorkingDirectory(HadoopConstants.WORKING_DIRECTORIES.SPARK_CHECK_PT_FRIEND)
  
  def createStreamingContext() = {
    val configuration = new SparkConf().setMaster(SPARK_YARN_CLUSTER_MASTER).setAppName("friend")
    val streamingContext = new StreamingContext(configuration, Seconds(10))
    
    streamingContext
  }
  
  def main(args: Array[String]): Unit = {
    
    val streamingContext = createStreamingContext
    val logger = LogManager.getRootLogger
    logger.info("Starting Friend Streaming...")
    
    val kafkaParameters = Map[String, Object](
      "bootstrap.servers" -> DEFAULT_BOOTSTRAP_URL,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "friend_stream", 
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    
    val topics = List(TOPICS.FRIEND.toString()).toSet
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext, 
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParameters)
    )
    
    stream.foreachRDD{ rdd =>
      rdd.foreachPartition { records =>
        
        val context = TaskContext.get
        
        val sparkSession = SparkSession.builder.master(SPARK_YARN_CLUSTER_MASTER)
          .appName("Friend Hive")
          .enableHiveSupport()
          .getOrCreate()

        val partitionLogger = LogManager.getLogger("Friend")
        partitionLogger.info(s"Friend for partition: ${context.partitionId}")
        
        records.foreach(record => {
          //perform avro deserialization + geo api call
          
          partitionLogger.info("Processing record - " + record)
          
          val deserialized = SerializationHelper.deserializeFromJSONStringToAvro(classOf[FriendEdge], FriendEdge.getClassSchema(), record.value)
          partitionLogger.info("Deserialized - " + deserialized)
          
        })
      
      }
    }
    
    streamingContext.start()
    streamingContext.awaitTermination()
  }
  
}
