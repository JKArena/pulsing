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
package org.jhk.pulsing.giraph.inputFormat;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.giraph.edge.DefaultEdge;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import static org.jhk.pulsing.giraph.common.Constants.FRIENDSHIP_TEXT_FRIEND_DELIM;

/**
 * {userId/vertexId}|[friendId:level,friendId:level,...]|{comma-separated direct friendId}
 * 
 * @author Ji Kim
 */
public final class FriendTextVertexInputFormat extends TextVertexInputFormat<LongWritable, Text, LongWritable> {

    @Override
    public TextVertexInputFormat<LongWritable, Text, LongWritable>.TextVertexReader createVertexReader(
            InputSplit split, TaskAttemptContext context) throws IOException {
        return new FriendTextReader();
    }
    
    public final class FriendTextReader extends org.apache.giraph.io.formats.TextVertexInputFormat.TextVertexReader {

        @Override
        public Vertex getCurrentVertex() throws IOException, InterruptedException {
            Text line = (Text) getRecordReader().getCurrentValue();
            String[] split = line.toString().split("\\|");
            LongWritable userId = new LongWritable(Long.parseLong(split[0]));
            Text value = new Text(split[1]);
            
            List<Edge<LongWritable, LongWritable>> friendIdList = new LinkedList<>();
            for (String friendId : split[2].split(FRIENDSHIP_TEXT_FRIEND_DELIM)) {
                DefaultEdge<LongWritable, LongWritable> friendEdge = new DefaultEdge<>();
                LongWritable friendEdgeId = new LongWritable(Long.parseLong(friendId));
                
                friendEdge.setTargetVertexId(friendEdgeId);
                friendEdge.setValue(friendEdgeId); //TODO: dummy value
                friendIdList.add(friendEdge);
            }
            
            Vertex<LongWritable, Text, LongWritable> vertex = getConf().createVertex();
            vertex.initialize(userId, value, friendIdList);
            
            return vertex;
        }

        @Override
        public boolean nextVertex() throws IOException, InterruptedException {
            return getRecordReader().nextKeyValue();
        }
        
    }

}
