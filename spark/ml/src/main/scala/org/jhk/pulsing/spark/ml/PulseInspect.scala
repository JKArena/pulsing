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
package org.jhk.pulsing.spark.ml

import java.time.LocalDate;

import org.apache.spark._
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import org.apache.spark.sql._
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.clustering.KMeans

import com.databricks.spark.avro._

import org.jhk.pulsing.shared.util.CommonConstants._
import org.jhk.pulsing.shared.util.HadoopConstants

/**
 * @author Ji Kim
 */
object PulseInspect {
  
  def createSparkContext() = {
    val configuration = new SparkConf().setMaster(PROJECT_POINT).setAppName("spam-bot")
    val sparkContext = new SparkContext(configuration);
    
    sparkContext
  }
  
  def main(args: Array[String]): Unit = {
    
    val sparkContext = createSparkContext
    val sqlContext = new SQLContext(sparkContext)
    import sqlContext.implicits._
    import sqlContext._
    
    sqlContext.setConf("spark.sql.avro.compression.codec", "deflate")
    sqlContext.setConf("spark.sql.avro.deflate.level", "5")
    
    val dframe = sqlContext.read.avro(HadoopConstants.SPARK_NEW_DATA_WORKSPACE + "/pulse/" + LocalDate.now().getYear() + "/Pulse")
    dframe.printSchema
    
    dframe.cache
    dframe.show
    
    val featureCols = Array("lat", "lng")
    val assembler = new VectorAssembler().setInputCols(featureCols)
    
    val dfTransformed = assembler.transform(dframe)
    dfTransformed.show
    
    val Array(trainingData, testData) = dfTransformed.randomSplit(Array(0.7, 0.3), 5043)
    val kMeans = new KMeans().setK(9).setFeaturesCol("features").setPredictionCol("prediction")
    val model = kMeans.fit(dfTransformed)
    
    println("Processed...")
    model.clusterCenters.foreach(println)
    
    val categories = model.transform(testData)
    categories.show
    
    categories.select(hour(from_unixtime($"timeStamp")).alias("hour"), $"prediction")
      .groupBy("hour", "prediction").agg(count("prediction").alias("count")).orderBy(desc( "count" )).show
    categories.groupBy("prediction").count().show()
  }
  
}
