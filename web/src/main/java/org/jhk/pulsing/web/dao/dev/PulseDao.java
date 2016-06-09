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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.PulseId;
import org.jhk.pulsing.web.dao.IPulseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public class PulseDao implements IPulseDao {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(PulseDao.class);
    
    private static final List<Pulse> MOCK_TRENDING_PULSES = new LinkedList<>();
    private static final ConcurrentMap<PulseId, Pulse> MOCK_PULSE_MAPPER = new ConcurrentHashMap<>();
    
    static {
        
        PulseId pulseId = PulseId.newBuilder().build();
        pulseId.setId(1234L);
        
        Pulse pulse = Pulse.newBuilder().build();
        pulse.setValue("Mocked 1");
        pulse.setId(pulseId);
        
        MOCK_PULSE_MAPPER.put(pulseId, pulse);
        MOCK_TRENDING_PULSES.add(pulse);
        
        pulseId = PulseId.newBuilder().build();
        pulseId.setId(5678L);
        
        pulse = Pulse.newBuilder().build();
        pulse.setValue("Mocked 2");
        pulse.setId(pulseId);
        
        MOCK_PULSE_MAPPER.put(pulseId, pulse);
        MOCK_TRENDING_PULSES.add(pulse);
    }

    @Override
    public Pulse getPulse(PulseId pulseId) {
        _LOGGER.info("getPulse", pulseId);
        
        return MOCK_PULSE_MAPPER.get(pulseId);
    }

    @Override
    public void createPulse(Pulse pulse) {
        _LOGGER.info("createPulse", pulse);
        
        MOCK_PULSE_MAPPER.put(pulse.getId(), pulse);
    }

    @Override
    public void subscribePulse(Pulse pulse) {
        _LOGGER.info("subscribePulse", pulse);
        
    }

    @Override
    public List<Pulse> getTrendingPulse() {
        return MOCK_TRENDING_PULSES;
    }
    
}
