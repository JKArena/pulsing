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

package org.jhk.pulsing.sandbox.timeline;

/**
 * ####Design
 * 1) Mimic of Kafka broker with main thread/client publishing the Messages to TransferQueue and KafkaMimic processing it (can add new tweet entry by the command line)
 * 2) Mimic of stream processing (i.e. Storm) of messages by using CompletableFuture to mimic topology of the consumer. Note since all we want is to get the latest 
 * tweets and tweets were not ordered by timestamp used PriorityQueue as minHeap rather than initially thought CircularFifoQueue (i.e. remove min timestamp tweet value 
 * when size == 10 and new tweet has timestamp greater than it)  
 * 3) Mimic of Fanout by having UserId -> Tweets (mimic of Redis cache) of tweets.
 * 
 * ####Assumption
 * 1) Biggest assumption I made is that number of followers for a tweeter is in acceptable range for Fanout design. Meaning if the tweeter is a very popular 
 * person (i.e. celebrity), probably best to have the follower query the DB of whether the tweeter has any new messages rather than fanning out to very large 
 * number of followers.
 * 2) Also KafkaMimic uses TransferQueue whereas Kafka stores the messages in log files w/ offsets. This "Mimic" doesn't work out since I will have to store everything  
 * in the memory (for that reason I just used TransferQueue which allows for drop in messages rather than continual growing where the consumer doesn't reduce  
 * the queue of the message published).
 * 3) Assumed the input files to be valid otherwise just ignore
 * 4) Assumed that # of users * 10 (max number of tweets to fit in memory)
 * 
 * ####Commands - Used Commons CLI
 * Start the commandline
 * java -jar commandCli.jar
 * 
 * 1) timeline -help
 *      provides command listing
 * 2) timeline -getTimeline <userId>
 *      ex. timeline -getTimeline 989489610
 * 3) timeline -tweet <tweet> 
 * 4) timeline -getUser <userId>
 *      ex. timeline -getUser 989489610
 * 5) timeline -getFollowers <userId>
 *      ex. timeline -getFollowers 989489610
 *
 * @author Ji Kim
 */