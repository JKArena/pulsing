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

namespace java org.jhk.pulsing.serialization.thrift.data

include "../property/UserProperty.thrift"
include "../property/TagProperty.thrift"

include "../edges/EquivEdge.thrift"
include "../edges/TagEdge.thrift"

/**
 * DataUnit
 *
 * @author Ji Kim
 */
union DataUnit {
  1: UserProperty.UserProperty user_property;
  2: TagProperty.TagProperty tag_property;
  3: EquivEdge.EquivEdge equiv;
  4: TagEdge.TagEdge tag;
}
