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

include "../property/PulseProperty.thrift"
include "../property/UserProperty.thrift"

include "../edges/EquivEdge.thrift"
include "../edges/FriendEdge.thrift"
include "../edges/PulseEdge.thrift"

/**
 * Data content
 *
 * @author Ji Kim
 */
struct Data {
  1: required DataUnit dataunit;
  2: required Pedigree pedigree;
}

struct Pedigree {
  1: required i32 true_as_of_secs;
}

union DataUnit {
  1: UserProperty.UserProperty user_property;
  2: PulseProperty.PulseProperty pulse_property;
  3: EquivEdge.EquivEdge equiv;
  4: FriendEdge.FriendEdge friends;
  5: PulseEdge.PulseEdge pulse;
}