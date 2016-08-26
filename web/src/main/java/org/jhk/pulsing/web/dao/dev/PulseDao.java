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
package org.jhk.pulsing.web.dao.dev;

import static org.jhk.pulsing.web.common.Result.CODE.SUCCESS;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.PulseId;
import org.jhk.pulsing.web.common.Result;
import org.jhk.pulsing.web.dao.IPulseDao;

/**
 * @author Ji Kim
 */
public class PulseDao implements IPulseDao {
    
    public static final List<Pulse> MOCK_TRENDING_PULSE_SUBSCRIPTIONS = new LinkedList<>();
    
    private static final ConcurrentMap<PulseId, Pulse> MOCK_PULSE_MAPPER = new ConcurrentHashMap<>();
    
    static {
        
        PulseId pulseId = PulseId.newBuilder().build();
        pulseId.setId(1234L);
        
        Pulse pulse = Pulse.newBuilder().build();
        pulse.setValue("Mocked 1");
        pulse.setId(pulseId);
        
        MOCK_PULSE_MAPPER.put(pulseId, pulse);
        MOCK_TRENDING_PULSE_SUBSCRIPTIONS.add(pulse);
        
        pulseId = PulseId.newBuilder().build();
        pulseId.setId(5678L);
        
        pulse = Pulse.newBuilder().build();
        pulse.setValue("Mocked 2");
        pulse.setId(pulseId);
        
        MOCK_PULSE_MAPPER.put(pulseId, pulse);
        MOCK_TRENDING_PULSE_SUBSCRIPTIONS.add(pulse);
    }

    @Override
    public Optional<Pulse> getPulse(PulseId pulseId) {
        Pulse pulse = MOCK_PULSE_MAPPER.get(pulseId);
        
        return Optional.ofNullable(pulse);
    }

    @Override
    public Result<Pulse> createPulse(Pulse pulse) {
        MOCK_PULSE_MAPPER.put(pulse.getId(), pulse);
        
        return new Result<>(SUCCESS, (pulse));
    }
    
}
