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

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.classification.LogisticRegressionWithSGD

import org.jhk.pulsing.shared.util.CommonConstants._
import org.jhk.pulsing.shared.util.HadoopConstants

/**
 * @author Ji Kim
 */
class ForbiddenPulseWords {
  
  val FORBIDDEN_WORDS_PATH = HadoopConstants.HDFS_URL_PORT + HadoopConstants.TRAINING_DATA_WORKSPACE + "forbiddenPulseWords.txt";
  
  def createSparkContext() = {
    val configuration = new SparkConf().setMaster(PROJECT_POINT).setAppName("forbidden-pulse-words")
    val sparkContext = new SparkContext(configuration);
    
    sparkContext
  }
  
  def main(args: Array[String]): Unit = {
    
    val sparkContext = createSparkContext
    val forbiddenWords = sparkContext.textFile(FORBIDDEN_WORDS_PATH)
    
  }
  
}
