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
package org.jhk.pulsing.spark.graphx.common

import org.apache.spark._
import org.apache.spark.graphx._

import org.jhk.pulsing.shared.util.CommonConstants._
import org.jhk.pulsing.shared.util.HadoopConstants

/**
 * @author Ji Kim
 */
object Common {
  
  def createSparkContext(appName: String) = {
    val configuration = new SparkConf().setMaster(PROJECT_POINT).setAppName(appName)
    val sparkContext = new SparkContext(configuration);
    
    sparkContext
  }
  
  def gexf[VD, ED](graph:Graph[VD, ED], gefxName:String) = {
    val processed = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    "<gexf xmlns=\"http://www.gexf.net/1.2draft\" version=\"1.2\">\n" + 
    "	<graph mode=\"static\" defaultedgetype=\"directed\">\n" + 
    "		<nodes>\n" + 
    graph.vertices.map(vertex => "	<node id=\"" + vertex._1 + "\" label=\"" + vertex._2 + "\" />\n").collect.mkString +
    "		</nodes>\n" + 
    "		<edges>\n" + 
    graph.edges.map(edge => "	<edge source=\"" + edge.srcId + "\" target=\"" + edge.dstId + "\" label=\"" + edge.attr + "\" />\n").collect.mkString + 
    "		</edges>\n" + 
    "	</graph>\n" + 
    "</gexf>"
    
    val pWriter = new java.io.PrintWriter(gefxName + ".gexf")
    pWriter.write(processed)
    pWriter.flush
    pWriter.close
    
  }
  
}
