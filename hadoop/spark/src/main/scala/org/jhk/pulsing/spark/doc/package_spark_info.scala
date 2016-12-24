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
package org.jhk.pulsing.spark.doc

/**
 * An RDD in Spark is simply an immutable distributed collection of objects. Each RDD is split into multiple partitions, which may be computed ond different nodes of the cluster.
 * 
 * Transformations construct a new RDD from a previous one. Actions compute a result based on an RDD and either return it to the driver program or save it to an external storage system.
 * 
 * Although you can define new RDDs any time, Spark computes them only in a lazy fashion - that is, the first time they are used in an action. Finally, Spark's RDDs are by default 
 * recomputed each time you run an action on them. If you would like to reuse an RDD in multiple actions, you can ask Spark to persist it using RDD.persist(). When we ask 
 * Spark to persist an RDD, the nodes that compute the RDD store their partitions. If a node that has data persisted on it fails, Spark will recompute the lost partitions of the 
 * data when needed. We can also replicate our data on multiple nodes if we want to be able to handle node failure without slowdown.
 * 
 * import org.apache.spakr.storage.StorageLevel
 * val result = input.map(x => x*x)
 * result.persist(StorageLevel.DISK_ONLY)
 * println(result.count())
 * println(result.collect().mkString(","))
 * 
 * Other options are MEMORY_ONLY, MEMORYONLY_SR, MEMORY_AND_DISK, MEMORY_AND_DISK_SER
 * 
 * As you derive new RDDs from each other using transformations, Spark keeps track of the set of dependencies between different RDDs, called the lineage graph. It uses this 
 * information to compute each RDD on demand and to recover lost data if part of a persistent RDD is lost.
 * 
 * rdd.redueByKey(func) produces the same RDD as rdd.groupByKey().mapValues(value => value.reduce(func)) but is more efficient as it avoids the step of creating a list of values for 
 * each key.
 * 
 * Spark's partitioning is available on all RDDs of key/value pairs, and causes the system to group elements based on a function of each key. Although Spark does not give 
 * explicit control of which worker node each key goes to (partly because the system is designed to work even if specific node fail), it lets the program ensure that a set of 
 * keys will appear together on some node. For example, you might choose to hash partition an RDD into 100 partitions so that keys that have the same hash value modulo 
 * 100 appear on the same node. Or you might range-partition the RDD into sorted range of keys so that elements with keys in the same range appear on the same node.
 * 
 * val sc = new SparkContext(...)
 * val userData = sc.sequenceFile[UserId, UserInfo]("hdfs://...")
 * 									.partitionBy(new org.apache.spark.HashPartitioner(100)) //creates 100 partitions
 * 									.persist()
 * val events = sc.sequenceFile[UsedId, LinkInfo](logFileName)
 * val joined = userData.join(events)
 * 
 * val offTopicVisits = joined.filter {
 * 	case (userId, (userInfo, linkInfo)) => !userInfo.topics.contains(linkInfo.topic)
 * }.count()
 * 
 * The calls to join() will take advantage of the fact userData is partitioned, Spark will shuffle only the events RDD, sending events with each particular UserId to the machine 
 * that contains the corresponding hash partition of userData. The result is that a lot less data is communicated over the network and the program runs significantly faster.
 * 
 * sortByKey() and groupByKey() will result in range-partitioned and hash-partitioned RDDs, respectively. On the other hand, operations like map() cause the new RDD to forget the 
 * parent's partitioning information because such operations could theoretically modify the key of each record. 
 * 
 * As of Spark 1.0, the operations that benefit from partitioning are cogroup(), groupWith(), join(), leftOuterJoin(), rightOuterJoin(), groupByKey(), reduceByKey(), 
 * combineByKey(), and lookup(). For operations that act on a single RDD such as reduceByKey(), 
 * running on a prepartitioned RDD will cause all the values for each key to be computed locally on a single machine, requiring only the final, locally reduced value to be sent 
 * from each worker node back to the master. For binary operations, such as cogroup() and join(), pre-partitioning will cause at least one of the RDDs (the one with the known 
 * partitioner) to not be shuffled. If both RDDs have the same partitioner, and if they are cached on the same machines (e.g. one was created using mapValues on the other, 
 * which preserves keys and partitioning) or if one of them has not yet been computed, then no shuffling across the network will occur.
 * 
 * For transformations that cannot be guaranteed to produce a known partitioning, the output RDD will not have a partitioner set. For example, if you call map() on a hash-partitioned RDD
 * of key/value pairs, the function passed to map() can in theory change the key of each element, so the result will not have a partitioner. Spark does not analyze your functions to 
 * check whether they retain the key. Instead it provides two other operations, mapValues() and flatMapValues(), which guarantee that each tuple's key remains the same.
 * 
 * All that said, here are all the operations that result in a partitioner being set on the outputRDD: cogroup(), groupWith(), join(), leftOuterJoin(), rightOuterJoin(), 
 * groupByKey(), reduceByKey(), combineByKey(), partitionBy(), sort(), mapValues() (if the parent RDD has a partitioner), flatMapValues() (if parent has a partitioner), and filter() 
 * (if parent has a partitioner). All other operations will produce a result with no partitioner.
 * 
 * To maximize the potential of partitioning-related optimizations, you should use mapValues() or flatMapValues() whenever you are not changing an element's key. 
 * 
 * Hadoop sequence file
 * val data = sc.sequenceFile(inFile, classOf[Text], classOf[IntWritable]).map{case (x, y) => (x.toString, y.get())}
 * data.saveAsSequenceFile(outputFile)
 * 
 * To read in a file using the new Hadoop API we need to tell Spark a few things. The newAPIHadoopFile takes a path, and three classes...Twitter's Elephant Bird package supports a 
 * large number of data formats, including JSON, Lucene, Protocol Buffer-related formats, and others. The package also works with both the new and old Hadoop file APIs. To illustrate 
 * how to work with the new style Hadoop APIs from Spark, we'll look at loading LZO-compressed JSON data with Lzo JsonInputFormat.
 * 
 * val input = sc.newAPIHadoopFile(inputFile, classOf[LzoJsonInputFormat], classOf[LongWritable], classOf[MapWritable], conf)
 * 
 * LZO support requires you to install the hadoop-lzo package and point Spark to its native libraries. If you install the Debian package, adding --driver-library-path 
 * /usr/lib/hadoop/lib/native/ --driver-class-path /usr/lib/hadoop/lib to your spark-submit invocation should do the trick.
 * 
 * Choosing an output compressiong codec can have a big impact on future users of data. With distributed systems such as Spark, we normally try to read our data in from multiple 
 * different machines. To make this possible, each worker needs to be able to find the start of a new record. Some compression formats make this impossible, which requires a single 
 * node to read in all of the data and thus can easily lead to a bottleneck. Formats that can be easily read from multiple machines are called "split-table".
 * 
 * Spark's second type of shared variable, broadcast variables, allows the program to efficiently send a large, read-only value to all the worker nodes for use in one or more 
 * Spark operations. They come in handy, for example, if your application needs to send a large, read-only lookup table to all the nodes, or even a large feature vector in a machine 
 * learning algorithm.
 * 
 * Recall that Spark automatically sends all variables referenced in your closures to the worker nodes. While this is convenient, it can also be inefficient because (1) the 
 * default task launching mechanism is optimized for small task sizes, and (2) you might, in fact, use the same variable in multiple parallel operations, but Spark will send it 
 * separately for each operation.
 * 
 * val signPrefixes = sc.broadcast(loadCallSignTable())
 * 
 * val countryContactCounts = contactCounts.map{case (sign, count) =>
 * 	val country = lookupInArray(sign, signPrefixes.value)
 * 	(country, count)
 * }.reduceByKey((x, y) => x+y)
 * 
 * Working with data on a per-partition basis allows us to avoid redoing setup work for each data item. Operations like opening a database connection or creating a random number 
 * generator are examples of setup steps that we wish to avoid doing for each element. Spark has per-partition versions of map and foreach to help reduce the cost of these 
 * operations by leeting you run code only once for each partition of an RDD...we use mapPartitions() function, which gives us an iterator of the elements in each partition of 
 * the input RDD and expects us to return an iterator of our results.
 * 
 * val contactsContactLists = validSigns.distinct().mapPartitions {
 * 	signs =>
 * 	val mapper = createMapper()
 * 	val client = new HttpClient
 * 	client.start()
 * 	
 * 	signs.map {sign =>
 * 		createExchangeForSign(sign)
 * 	}.map { case(sign, exchange) =>
 * 		(sign, readExchangeCallLog(mapper, exchange))
 * 	}.filter(x => x._2 != null)
 * }
 * 
 * @author Ji Kim
 */
