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
import java.util.Map;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.jhk.pulsing.serialization.avro.records.ACTION;
import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.PulseId;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.shared.util.Util;
import org.jhk.pulsing.web.common.Result;
import static org.jhk.pulsing.web.common.Result.CODE.*;
import org.jhk.pulsing.web.dao.prod.db.redis.RedisPulseDao;
import org.jhk.pulsing.web.dao.prod.db.redis.RedisUserDao;
import org.jhk.pulsing.web.pojo.light.UserLight;
import org.jhk.pulsing.web.service.IPulseService;
import org.jhk.pulsing.web.service.prod.helper.PulseServiceUtil;
import org.jhk.pulsing.web.websocket.model.MapPulseCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Ji Kim
 */
@Service
public class PulseService extends AbstractStormPublisher 
                            implements IPulseService {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(PulseService.class);
    
    private ObjectMapper _objectMapper = new ObjectMapper();
    
    @Inject
    @Named("redisPulseDao")
    private RedisPulseDao redisPulseDao;
    
    @Inject
    @Named("redisUserDao")
    private RedisUserDao redisUserDao;
    
    @Inject
    private SimpMessagingTemplate template;
    
    @Override
    public Result<Pulse> getPulse(PulseId pulseId) {
        Optional<Pulse> optPulse = redisPulseDao.getPulse(pulseId);
        
        return optPulse.isPresent() ? new Result<>(SUCCESS, optPulse.get()) : new Result<>(FAILURE, null, "Unabled to find " + pulseId);
    }
    
    
    /**
     * For creation of pulse there are couple of tasks that must be done
     * 1) Add the pulse to Redis 
     * 2) Send the message to storm of the creation (need couple of different writes to Hadoop for Data + Edges for processing)
     * 3) Send the message to storm of the subscription (for trending of time interval)
     * 4) Send the websocket topic to clients (namely MapComponent) that a new pulse has been created (to either map or not)
     * 
     * @param pulse
     * @return
     */
    @Override
    public Result<Pulse> createPulse(Pulse pulse) {
        
        PulseId pId = PulseId.newBuilder().build();
        pId.setId(Util.uniqueId());
        pulse.setAction(ACTION.CREATE);
        pulse.setId(pId);
        pulse.setTimeStamp(Instant.now().getEpochSecond());
        
        Result<Pulse> cPulse = redisPulseDao.createPulse(pulse);
        
        if(cPulse.getCode() == SUCCESS) {
            getStormPublisher().produce(CommonConstants.TOPICS.PULSE_CREATE.toString(), cPulse.getData());
            subscribePulse(pulse, pulse.getUserId());
            
            Optional<UserLight> oUserLight = redisUserDao.getUserLight(pulse.getUserId().getId());
            if(oUserLight.isPresent()) {
                try {
                    
                    template.convertAndSend("/topics/pulseCreated", 
                                            _objectMapper.writeValueAsString(new MapPulseCreate(oUserLight.get(), 
                                                                                SerializationHelper.serializeAvroTypeToJSONString(cPulse.getData()))));
                } catch (Exception except) {
                    _LOGGER.error("Error while converting pulse ", except);
                    except.printStackTrace();
                }
            }
        }
        
        return cPulse;
    }
    
    /**
     * 1) Send the message to storm of the subscription (update to redis taken care of by storm)
     * 2) Add the userLight to the pulse subscription in redis
     * 
     * @param pulse
     * @return
     */
    @Override
    public Result<String> subscribePulse(Pulse pulse, UserId userId) {
        pulse.setUserId(userId);
        pulse.setAction(ACTION.SUBSCRIBE);
        pulse.setTimeStamp(Instant.now().getEpochSecond());
        
        Optional<UserLight> oUserLight = redisUserDao.getUserLight(userId.getId());
        Result<String> result = new Result<>(FAILURE, "Failed in subscription");
        
        if(oUserLight.isPresent()) {
            UserLight userLight = oUserLight.get();
            userLight.setSubscribedPulseId(pulse.getId().getId());
            
            redisPulseDao.subscribePulse(pulse, userLight);
            redisUserDao.storeUserLight(userLight); //need to update it with the new subscribed pulse id
            getStormPublisher().produce(CommonConstants.TOPICS.PULSE_SUBSCRIBE.toString(), pulse);
            result = new Result<>(SUCCESS, "Subscribed");
        }
        
        return result;
    }
    
    /**
     * Hmmm should work in DRPC from storm+trident to get notified of new batch and then send 
     * the message to client component for new set? Look into it since familiar only w/ trident
     * 
     * @param numMinutes
     * @return
     */
    @Override
    public Map<Long, String> getTrendingPulseSubscriptions(int numMinutes) {
        
        Instant current = Instant.now();
        Instant beforeRange = current.minus(numMinutes, ChronoUnit.MINUTES);
        
        Optional<Set<String>> optTps = redisPulseDao.getTrendingPulseSubscriptions(beforeRange.getEpochSecond(), current.getEpochSecond());
        
        @SuppressWarnings("unchecked")
        Map<Long, String> tpSubscriptions = Collections.EMPTY_MAP;
        
        if(optTps.isPresent()) {
            tpSubscriptions = PulseServiceUtil.processTrendingPulseSubscribe(optTps.get());
        };
        
        return tpSubscriptions;
    }
    
    @Override
    public Map<Pulse, Set<UserLight>> getMapPulseDataPoints(Double lat, Double lng) {
        
        return redisPulseDao.getMapPulseDataPoints(lat, lng);
    }
    
}
