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
package org.jhk.pulsing.web.service.dev;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.PulseId;
import org.jhk.pulsing.web.common.Result;
import static org.jhk.pulsing.web.common.Result.CODE.*;
import org.jhk.pulsing.web.dao.IPulseDao;
import org.jhk.pulsing.web.dao.dev.PulseDao;
import org.jhk.pulsing.web.service.IPulseService;

/**
 * @author Ji Kim
 */
public class PulseService implements IPulseService {
    
    @Inject
    private IPulseDao pulseDao;

    @Override
    public Result<Pulse> getPulse(PulseId pulseId) {
        Optional<Pulse> pulse = pulseDao.getPulse(pulseId);
        
        return !pulse.isPresent() ? new Result<>(FAILURE, "Failed to get pulse " + pulseId) 
                : new Result<>(SUCCESS, pulse.get()); 
    }

    @Override
    public Result<Pulse> createPulse(Pulse pulse) {
        return pulseDao.createPulse(pulse);
    }

    @Override
    public Result<PulseId> subscribePulse(Pulse pulse) {
        Result<PulseId> sResult = new Result<>(SUCCESS, pulse.getId());
        return sResult;
    }

    @Override
    public Map<Long, String> getTrendingPulseSubscriptions(int numMinutes) {
        Map<Long, String> entries = new HashMap<>();
        
        PulseDao.MOCK_TRENDING_PULSE_SUBSCRIPTIONS.stream()
            .forEach(pulse -> {
                entries.put(pulse.getId().getId(), pulse.getValue().toString());
            });
        
        return entries;
    }
    
    @Override
    public List<Pulse> getMapPulseDataPoints(Double lat, Double lng) {
        List<Pulse> dataPoints = new LinkedList<>();
        for(int loop=0; loop < 10; loop++) {
            dataPoints.add(PulseDao.createMockedPulse());
        }
        return dataPoints;
    }
    
}
