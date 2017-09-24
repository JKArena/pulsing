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
package org.jhk.pulsing.spark.ml.job

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.classification.LogisticRegressionWithSGD

import org.jhk.pulsing.spark.ml.common.Common
import org.jhk.pulsing.shared.util.CommonConstants._
import org.jhk.pulsing.shared.util.HadoopConstants

/**
 * @author Ji Kim
 */
object SpamBot {
  
  val SPAM_WORDS_PATH = HadoopConstants.HDFS_URL_PORT + HadoopConstants.TRAINING_DATA_WORKSPACE + "spamWords.txt";
  val NORMAL_WORDS_PATH = HadoopConstants.HDFS_URL_PORT + HadoopConstants.TRAINING_DATA_WORKSPACE + "normalWords.txt";
  
  def main(args: Array[String]): Unit = {
    startup
  }
  
  def startup(): Unit = {
    
    val sparkContext = Common.createSparkContext("spam-bot")
    val spamWords = sparkContext.textFile(SPAM_WORDS_PATH)
    val normalWords = sparkContext.textFile(NORMAL_WORDS_PATH)
    
    //Map message text to vectors of 10000 features
    val transform = new HashingTF(numFeatures = 10000)
    
    //Each message is split into words, each word is mapped to one feature
    val spamFeatures = spamWords.map(message => transform.transform(message.split(" ")))
    val nonSpamFeatures = normalWords.map(message => transform.transform(message.split(" ")))
    
    //Create LabeledPoints for spam + nonSpam examples
    val spamExamples = spamFeatures.map(features => LabeledPoint(1, features))
    val nonSpamExamples = nonSpamFeatures.map(features => LabeledPoint(0, features))
    
    val trainingData = spamExamples.union(nonSpamExamples)
    trainingData.cache // Cache since Logistic Regression is an iterative algorithm
    
    //run logistic regression using the SGD algorithm
    val model = new LogisticRegressionWithSGD().run(trainingData)
    val spamTest = transform.transform("Earn free money".split(" "))
    val nonSpamTest = transform.transform("Hey people, let's go to the event".split(" "))
    
    println("Spam Test: " + model.predict(spamTest))
    println("Non Spam Test: " + model.predict(nonSpamTest))
    
    model.save(sparkContext, HadoopConstants.getWorkingDirectory(HadoopConstants.WORKING_DIRECTORIES.SPARK_SPAM_MODEL))
  }
  
}
