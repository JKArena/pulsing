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
package org.jhk.pulsing.giraph.outputFormat;

import java.io.IOException;
import java.util.StringJoiner;

import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import static org.jhk.pulsing.giraph.common.Constants.FRIENDSHIP_TEXT_FRIEND_DELIM;

/**
 * @author Ji Kim
 */
public final class FriendTextVertexOutputFormat extends TextVertexOutputFormat<LongWritable, Text, LongWritable> {

    @Override
    public TextVertexOutputFormat<LongWritable, Text, LongWritable>.TextVertexWriter createVertexWriter(
            TaskAttemptContext context) throws IOException, InterruptedException {
        return new FriendRecordTextWriter();
    }
    
    public class FriendRecordTextWriter extends TextVertexWriter {
        
        private Text newKey = new Text();
        private Text newValue = new Text();

        @Override
        public void writeVertex(Vertex<LongWritable, Text, LongWritable> vertex)
                throws IOException, InterruptedException {
            Iterable<Edge<LongWritable, LongWritable>> edges = vertex.getEdges();
            StringJoiner joiner = new StringJoiner(FRIENDSHIP_TEXT_FRIEND_DELIM);
            
            for (Edge<LongWritable, LongWritable> edge : edges) {
                joiner.add(edge.getValue().toString());
            }
            newKey.set(vertex.getId().get() + "|" + vertex.getValue() + "|" + joiner.toString());
            getRecordWriter().write(newKey, newValue);
        }
        
    }

}
