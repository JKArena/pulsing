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
package org.jhk.pulsing.web.service.prod;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.PulseId;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.web.common.Result;
import static org.jhk.pulsing.web.common.Result.CODE.*;
import org.jhk.pulsing.web.dao.prod.db.redis.RedisPulseDao;
import org.jhk.pulsing.web.service.IPulseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Ji Kim
 */
@Transactional
@Service
public class PulseService extends AbstractStormPublisher 
                            implements IPulseService {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(PulseService.class);
    
    private static final TypeReference<HashMap<String, Integer>> _TRENDING_PULSE_SUBSCRIPTION_TYPE_REF = 
            new TypeReference<HashMap<String, Integer>>(){};
    
    @Inject
    private RedisPulseDao redisPulseDao;
    
    private ObjectMapper _objectMapper = new ObjectMapper();

    @Override
    public Result<Pulse> getPulse(PulseId pulseId) {
        Optional<Pulse> optPulse = redisPulseDao.getPulse(pulseId);
        
        return optPulse.isPresent() ? new Result<>(SUCCESS, optPulse.get()) : new Result<>(FAILURE, "Unabled to find " + pulseId);
    }

    @Override
    public Result<Pulse> createPulse(Pulse pulse) {
        Result<Pulse> cPulse = redisPulseDao.createPulse(pulse);
        
        if(cPulse.getCode() == SUCCESS) {
            getStormPublisher().produce(CommonConstants.TOPICS.USER_CREATE.toString(), cPulse.getData());
        }
        
        return cPulse;
    }

    @Override
    public Result<PulseId> subscribePulse(Pulse pulse) {
        
        getStormPublisher().produce(CommonConstants.TOPICS.PULSE_SUBSCRIBE.toString(), pulse);
        return new Result<>(SUCCESS, pulse.getId());
    }

    @Override
    public Map<Long, String> getTrendingPulseSubscriptions(int numMinutes) {
        
        Instant current = Instant.now();
        Instant beforeRange = current.minus(numMinutes, ChronoUnit.MINUTES);
        
        Optional<Set<String>> entries = redisPulseDao.getTrendingPulse(beforeRange.toEpochMilli(), current.toEpochMilli());
        
        Map<Long, String> tpSubscriptions = new HashMap<>();
        
        entries.ifPresent(entry -> {
            
            entry.stream().forEach(tpsIdValueCounts -> {
                
                try {
                    _LOGGER.debug("PulseService.getTrendingPulseSubscriptions: trying to convert " + tpsIdValueCounts);
                    
                    Map<String, Integer> converted = _objectMapper.readValue(tpsIdValueCounts, _TRENDING_PULSE_SUBSCRIPTION_TYPE_REF);
                    
                    _LOGGER.debug("PulseService.getTrendingPulseSubscriptions: sucessfully converted " + converted.size());
                } catch (Exception cException) {
                    cException.printStackTrace();
                }
            });
            
        });
        
        return tpSubscriptions;
    }
    
}
