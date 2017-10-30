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
package org.jhk.pulsing.giraph.computation;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import static org.jhk.pulsing.giraph.common.Constants.FRIENDSHIP_AGGREGATE;
import static org.jhk.pulsing.giraph.common.Constants.FRIENDSHIP_TEXT_FRIEND_DELIM;
import static org.jhk.pulsing.giraph.common.Constants.FRIENDSHIP_TEXT_FRIEND_LEVEL_DELIM;

/**
 * @author Ji Kim
 */
public final class FriendTextComputation extends BasicComputation<LongWritable, Text, LongWritable, LongWritable> {
    
    private Text vertexText = new Text();
    private LongWritable longIncrement = new LongWritable();

    @Override
    public void compute(Vertex<LongWritable, Text, LongWritable> vertex, Iterable<LongWritable> messages) throws IOException {
        long superstep = getSuperstep();
        
        Map<Long, Long> friendships = new HashMap<>();
        Arrays.asList(vertex.getValue().toString().split(FRIENDSHIP_TEXT_FRIEND_DELIM)).stream()
            .forEach(entry -> {
                String[] split = entry.split(FRIENDSHIP_TEXT_FRIEND_LEVEL_DELIM);
                friendships.put(Long.valueOf(split[0]), Long.valueOf(split[1]));
            });
        
        long count = 0L;
        for (LongWritable friend : messages) {
            long friendId = friend.get();
            if (!friendships.containsKey(friendId)) {
                friendships.put(friendId, superstep);
                count++;
            }
        }
        
        for (Edge<LongWritable, LongWritable> edge : vertex.getEdges()) {
            sendMessage(edge.getTargetVertexId(), vertex.getId());
        }
        
        if (count > 0) {
            StringJoiner joiner = new StringJoiner(FRIENDSHIP_TEXT_FRIEND_DELIM);
            friendships.keySet().forEach(friendId -> {
                joiner.add(friendId + FRIENDSHIP_TEXT_FRIEND_LEVEL_DELIM + friendships.get(friendId));
            });
            vertexText.set(joiner.toString());
            vertex.setValue(vertexText);
        }
        
        longIncrement.set(count);
        aggregate(FRIENDSHIP_AGGREGATE, longIncrement);
        
        vertex.voteToHalt();
    }

}
