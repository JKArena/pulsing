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

import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

import com.google.maps.GeoApiContext

import org.jhk.pulsing.shared.util.CommonConstants._
import org.jhk.pulsing.shared.util.HadoopConstants

/**
 * @author Ji Kim
 */
class Location {
  val CHECKPOINT = HadoopConstants.getWorkingDirectory(HadoopConstants.DIRECTORIES.SPARK_STREAM_LOCATION_CREATE)
  
  def createStreamingContext() = {
    val configuration = new SparkConf().setMaster(PROJECT_POINT).setAppName("location-create")
    val streamingContext = new StreamingContext(configuration, Seconds(10))
    
    streamingContext.checkpoint(CHECKPOINT)
    streamingContext
  }
  
  def main(args: Array[String]): Unit = {
    
    val streamingContext = StreamingContext.getOrCreate(CHECKPOINT, createStreamingContext _)
    
    val kafkaParameters = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      //The cache is keyed by topicpartition and group.id, so use a separate group.id
      //for each call to createDirectStream.
      "group.id" -> "location_create_stream", 
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    
    val topics = List(TOPICS.LOCATION_CREATE.toString()).toSet
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext, 
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParameters)
    )
    
    stream.mapPartitions{
      records =>
      val geoContext = new GeoApiContext().setApiKey(MAP_API_KEY)
      
      records.map { record =>
        //perform avro deserialization
        
      }.map { case (avro) =>
        //perform geo call
        
      }
    }
    
    streamingContext.start()
    streamingContext.awaitTermination()
  }
  
}
