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
package org.jhk.pulsing.spark.mLlib

/**
 * Although GraphX stores edges and vertices in separate tables as one might design an RDBMS schema to do, internally GraphX has 
 * special indexes to rapidly traverse the graph, and it exposes and API that makes graph querying and processing easier than trying to 
 * do the same in SQL.
 * 
 * GraphX datasets, like all Spark datasets, can't normally be shared by multiple Spark programs unless a REST server add-on like 
 * Spark JobServer is used. Until the IndexedRDD capability is added to Spark, which is effectively a mutable HashMap version of an RDD, 
 * GraphX is limited by the immutability of Spark RDDs, which is an issue for large graphs.
 * 
 * The real meat of GraphX Map/Reduce operations is called aggregateMessages (which supplants the deprecated mapReduceTriplets())
 * def aggregateMessages[Msg](
 * 	sendMsg: EdgeContext(VD, ED, Msg] => Unit,
 * 	mergeMsg: (Msg, Msg) => Msg)
 * : VertexRDD[Msg]
 * 
 * EdgeContext is a type-parameterized class similar to EdgeTriplet. EdgeContext contains the same fields as EdgeTriplet but also provides 
 * two additional methods for method sendings:
 * 1) sendToSrc - Sends a message of type Msg to the source vertex
 * 2) sendToDst - Sends a message of type Msg to the destination vertex
 * 
 * The result of applying a mergeMsg for each of the vertices is returned as a VertexRDD[Int]. VertexRDD is an RDD containing Tuple2s
 * consisting of the VertexId and the mergeMsg result for that vertex.
 * 
 * Complete algorithms can be built by repeated application of aggregateMessages. This is such a common requirement that GraphX provides 
 * a Pregel implemented API. In GraphX the implementation of Pregel is a form of Bulk Synchronous Parallel processing. The algorithm is 
 * decomposed into a series of supersteps, with each superstep being a single iteration. Within each superstep, per-vertex calculations 
 * can be performed in parallel. At the end of the superstep, each vertex generates messages for other vertices that are delivered in the 
 * next superstep. Due to the synchronization barrier, nothing from a subsequent superstep gets executed until the current superstep is 
 * fully completed.
 * 
 * def pregel[A]
 * 	(initialMsg: A,
 * 	maxIter:Int = Int.MaxValue,
 * 	activeDir: EdgeDirection = EdgeDirection.Out)
 * 	(vprog: (VertexId, VD, A) => VD,
 * 	sendMsg: EdgeTriplet[VD, ED] => Iterator[(VertexId, A)],
 * 	mergeMsg: (A, A) => A)
 * :Graph[VD, ED]
 * Partial function application (like curry). val pregel = Pregel(g, 0)_ then pregel(vprog, sendMsg, mergeMsg)
 * As Pregel still relies on the deprecated mapReduceTriplets it is using EdgeTriplet...The terminating condition for Pregel is that there are no more 
 * messages to be sent. In each iteration, if an edge's vertices did not receive messages from the previous iteration, sendMsg will not be called for that 
 * edge. The activeDirection parameter to Pregel specifies this filter.
 * 
 * Built in algorithms:
 * PageRank, Personalized PageRank, Triangle Count, Shortest Paths, Connected Components, Strongly Connected Components, Label Propagation
 * 
 * Personalized PageRank is a varitation on PageRank that gives a rank relative to a specified "source" vertex in the graph. Conceptually, the imaginary web surfer 
 * when suddenly deciding to visit another vertex, will always land on the specified soruce vertex. Within GraphX, this concept of an imaginary web surfer is implemented 
 * by enforcing a minimum PageRank only on the specified source vertex; the PageRanks of all the other vertices are allowed to fall to zero (i.e. if they have no inbound links)
 * 
 * The naive way and the way in which it was done for many years, was to assign different vertices to different computers in the cluster. 
 * But this led to computational bottlenecks because real-world graphs always seem to have some extremely high-degree vertices. The 
 * vertex degrees of real-world graphs tend to follow the Power Law. Partitioning a graph by vertices is called edge-cut because 
 * it's the edges that are getting cut. But a graph processing system that instead employs vertex-cut, which evenly distributes the 
 * edges among the machines/nodes, more evenly balances the data across the cluster.
 * 
 * @author Ji Kim
 */
