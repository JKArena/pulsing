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
package org.jhk.pulsing.spark.graphx.friend

import org.apache.log4j.LogManager
import org.apache.spark.graphx._
import org.apache.spark.SparkConf
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.io.NullWritable
import org.apache.avro.mapred.AvroKey
import org.apache.avro.mapreduce.{AvroKeyOutputFormat, AvroJob}

import org.jhk.pulsing.spark.graphx.common.Common.{gexf, createSparkContext}
import org.jhk.pulsing.shared.util.CommonConstants._
import org.jhk.pulsing.shared.util.HadoopConstants
import org.jhk.pulsing.serialization.avro.records.edge.FriendEdge
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper

/**
 * @author Ji Kim
 */
object FriendGraph {
  
  def main(args: Array[String]): Unit = {
    scratch
  }
  
  def scratch() = {
    val sparkContext = createSparkContext("friend-scratch")
    val vertices = sparkContext.makeRDD(Array((1L, "Ann"), (2L, "Bill"), (3L, "Charles"), (4L, "Diane"), (5L, "Went to gym")))
    val edges = sparkContext.makeRDD(Array(Edge(1L, 2L, "is-friends-with"), Edge(2L, 3L, "is-friends-with"), Edge(3L, 4L, "is-friends-with"), 
        Edge(4L, 5L, "Likes-status"), Edge(3L, 5L, "Wrote-status")))
    
    val graph = Graph(vertices, edges)
    gexf(graph, "base")
    
    graph.aggregateMessages[Int](_.sendToSrc(1), _ + _).rightOuterJoin(graph.vertices)
      .map(x => (x._2._2, x._2._1.getOrElse(0))).collect
    //find distance of furthest vertex
    val pregel = Pregel(graph.mapVertices((vId, vData) => 0), 0,
        activeDirection=EdgeDirection.Out)_
    pregel((id:VertexId, vData:Int, a:Int) => math.max(vData, a),
    (eTriplet:EdgeTriplet[Int, String]) => Iterator((eTriplet.dstId, eTriplet.srcAttr+1)),
    (a:Int, b:Int) => math.max(a, b))
    
    graph.vertices.collect
  }
  
}
