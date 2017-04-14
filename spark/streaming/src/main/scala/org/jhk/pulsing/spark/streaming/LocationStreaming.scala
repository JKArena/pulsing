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
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.TaskContext

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.io.NullWritable
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.Schema
import org.apache.avro.mapred.{AvroValue, AvroKey}
import org.apache.avro.mapreduce.{AvroKeyOutputFormat, AvroKeyValueOutputFormat, AvroJob}

import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.GeocodingResult

import org.jhk.pulsing.shared.util.CommonConstants._
import org.jhk.pulsing.shared.util.HadoopConstants
import org.jhk.pulsing.serialization.avro.records.Location
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper

/**
 * @author Ji Kim
 */
object LocationStreaming {
  val SPARK_LOCATION_CREATE = HadoopConstants.HDFS_URL_PORT + HadoopConstants.SPARK_NEW_DATA_WORKSPACE + "location"
  val CHECKPOINT = HadoopConstants.getWorkingDirectory(HadoopConstants.WORKING_DIRECTORIES.SPARK_CHECK_PT_LOCATION)
  
  def createStreamingContext() = {
    val configuration = new SparkConf().setMaster(SPARK_YARN_CLUSTER_MASTER).setAppName("location-create")
    configuration.registerAvroSchemas(Location.getClassSchema)
    val streamingContext = new StreamingContext(configuration, Seconds(10))
    
    //streamingContext.checkpoint(CHECKPOINT)
    streamingContext
  }
  
  def main(args: Array[String]): Unit = {
    
    //val streamingContext = StreamingContext.getOrCreate(CHECKPOINT, createStreamingContext _) Spark 13316 Bug
    val streamingContext = createStreamingContext
    val logger = LogManager.getRootLogger
    logger.info("Starting Location Streaming...")
    
    val kafkaParameters = Map[String, Object](
      "bootstrap.servers" -> DEFAULT_BOOTSTRAP_URL,
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
    
    stream.foreachRDD{ rdd => {
      
      var job = new Job(streamingContext.sparkContext.hadoopConfiguration)
      AvroJob.setOutputKeySchema(job, Location.getClassSchema)

      rdd.mapPartitions{ partition =>
  
        val context = TaskContext.get
        val geoContext = new GeoApiContext().setApiKey(MAP_API_KEY)
        val partitionLogger = LogManager.getLogger("Location")
        partitionLogger.info(s"Location Create for partition: ${context.partitionId}")
  
        var data = partition.map({ record => 
          //perform avro deserialization + geo api call
          partitionLogger.info("Processing record - " + record)

          val deserialized = SerializationHelper.deserializeFromJSONStringToAvro(classOf[Location], Location.getClassSchema, record.value)
          val result:Array[GeocodingResult] = GeocodingApi.geocode(geoContext, deserialized.getAddress().toString()).await()

          partitionLogger.info("GeocodingResult - " + result.length)

          val geoLoc = result(0).geometry.location;
          deserialized.setLat(geoLoc.lat)
          deserialized.setLng(geoLoc.lng)

          partitionLogger.info("Deserialized - " + deserialized)
          (new AvroKey(deserialized), NullWritable.get())
        }).toList

        data.iterator
      }.saveAsNewAPIHadoopFile(SPARK_LOCATION_CREATE, 
          classOf[AvroKey[Location]], 
          classOf[NullWritable], 
          classOf[AvroKeyOutputFormat[Location]], 
          job.getConfiguration)
      }
    }
    
    streamingContext.start()
    streamingContext.awaitTermination()
  }
  
}
