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
package org.jhk.pulsing.web.controller;

import static org.jhk.pulsing.client.payload.Result.CODE.FAILURE;
import static org.jhk.pulsing.client.payload.Result.CODE.SUCCESS;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jhk.pulsing.serialization.avro.records.ACTION;
import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.PulseId;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;
import org.jhk.pulsing.shared.processor.PulseProcessor;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.shared.util.Util;
import org.jhk.pulsing.client.payload.Result;
import org.jhk.pulsing.web.dao.prod.db.redis.RedisPulseDao;
import org.jhk.pulsing.web.dao.prod.db.redis.RedisUserDao;
import org.jhk.pulsing.web.pojo.light.MapPulseCreate;
import org.jhk.pulsing.client.payload.light.UserLight;
import org.jhk.pulsing.client.pulse.IPulseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Ji Kim
 */
@CrossOrigin(origins="*")
@Controller
@RequestMapping("/pulse")
public class PulseController {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(PulseController.class);
    private static final int TRENDING_PULSE_PRIOR_MINUTE = 10;
    
    private ObjectMapper _objectMapper = new ObjectMapper();
    
    @Inject
    private IPulseService pulseService;
    
    @Inject
    private RedisPulseDao redisPulseDao;
    
    @Inject
    private RedisUserDao redisUserDao;
    
    @Inject
    private SimpMessagingTemplate template;
    
    /**
     * 1) Use the pulseService to create the pulse
     * 2) If successful send the websocket topic to clients (namely MapComponent) that a new pulse has been created (to either map or not)
     * 
     * @param pulse
     * @return
     */
    @RequestMapping(value="/createPulse", method=RequestMethod.POST, consumes={MediaType.MULTIPART_FORM_DATA_VALUE})
    public @ResponseBody Result<Pulse> createPulse(@RequestParam Pulse pulse) {
        _LOGGER.debug("PulseController.createPulse: " + pulse);
        PulseId pId = PulseId.newBuilder().build();
        pId.setId(Util.uniqueId());
        pulse.setAction(ACTION.CREATE);
        pulse.setId(pId);
        pulse.setTimeStamp(Instant.now().getEpochSecond());
        
        Result<Pulse> result = redisPulseDao.createPulse(pulse);
        if(result.getCode() == Result.CODE.SUCCESS) {
            pulse = result.getData();
            pulseService.publishCreatePulse(pulse);
            subscribePulse(pulse.getId(), pulse.getUserId());
            Optional<UserLight> oUserLight = redisUserDao.getUserLight(pulse.getUserId().getId());
            if(oUserLight.isPresent()) {
                try {
                    template.convertAndSend("/topics/pulseCreated", 
                                            _objectMapper.writeValueAsString(new MapPulseCreate(oUserLight.get(), 
                                                                                SerializationHelper.serializeAvroTypeToJSONString(pulse))));
                } catch (Exception except) {
                    _LOGGER.error("Error while converting pulse ", except);
                    except.printStackTrace();
                }
            }
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
    @RequestMapping(value="/getTrendingPulseSubscriptions", method=RequestMethod.GET)
    public @ResponseBody Map<Long, String> getTrendingPulseSubscriptions() {
        _LOGGER.debug("PulseController.getTrendingPulseSubscriptions");

        Instant current = Instant.now();
        Instant beforeRange = current.minus(TRENDING_PULSE_PRIOR_MINUTE, ChronoUnit.MINUTES);
        
        Optional<Set<String>> optTpSubscribe = redisPulseDao.getTrendingPulseSubscriptions(beforeRange.getEpochSecond(), current.getEpochSecond());
        
        @SuppressWarnings("unchecked")
        Map<Long, String> tpSubscriptions = Collections.EMPTY_MAP;
        
        if(optTpSubscribe.isPresent()) {
            
            Map<String, Integer> count = PulseProcessor.countTrendingPulseSubscribe(optTpSubscribe.get());
            
            if(count.size() > 0) {
                tpSubscriptions = count.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(Collectors.toMap(
                                entry -> Long.parseLong(entry.getKey().split(CommonConstants.TIME_INTERVAL_ID_VALUE_DELIM)[0]),
                                entry -> entry.getKey().split(CommonConstants.TIME_INTERVAL_ID_VALUE_DELIM)[1],
                                (x, y) -> {throw new AssertionError();},
                                LinkedHashMap::new
                                ));
            }
            
        };
        
        return tpSubscriptions;
    }
    
    @RequestMapping(value="/getMapPulseDataPoints", method=RequestMethod.GET)
    public @ResponseBody Map<String, Set<UserLight>> getMapPulseDataPoints(@RequestParam double lat, @RequestParam double lng) {
        _LOGGER.debug("PulseController.getMapPulseDataPoints: lat {}, lng {}", lat, lng);
        
        return redisPulseDao.getMapPulseDataPoints(lat, lng);
    }
    
    @RequestMapping(value="/subscribePulse/{pulseId}/{userId}", method=RequestMethod.PUT)
    public @ResponseBody Result<PulseId> subscribePulse(@PathVariable PulseId pulseId, @PathVariable UserId userId) {
        _LOGGER.debug("PulseController.subscribePulse: " + pulseId + " - " + userId);
        
        Result<Pulse> gPulse = getPulse(pulseId);
        if(gPulse.getCode() == Result.CODE.FAILURE) {
            return new Result<PulseId>(Result.CODE.FAILURE, pulseId, gPulse.getMessage());
        }

        Pulse pulse = gPulse.getData();
        pulse.setUserId(userId);
        pulse.setAction(ACTION.SUBSCRIBE);
        pulse.setTimeStamp(Instant.now().getEpochSecond());
        
        Optional<UserLight> oUserLight = redisUserDao.getUserLight(userId.getId());
        Result<PulseId> result = new Result<>(FAILURE, pulse.getId(), "Failed in subscription");

        if(oUserLight.isPresent()) {
            UserLight userLight = oUserLight.get();
            userLight.setSubscribedPulseId(pulse.getId().getId());
            
            redisPulseDao.subscribePulse(pulse, userLight);
            redisUserDao.storeUserLight(userLight); //need to update it with the new subscribed pulse id
            pulseService.publishPulseSubscription(pulse);
            result = new Result<>(SUCCESS, pulse.getId(), "Subscribed");
        }

        return result;
    }
    
    @RequestMapping(value="/unSubscribePulse/{pulseId}/{userId}", method=RequestMethod.PUT)
    public @ResponseBody Result<String> unSubscribePulse(@PathVariable PulseId pulseId, @PathVariable UserId userId) {
        _LOGGER.debug("PulseController.unSubscribePulse: " + pulseId + " - " + userId);
        
        Result<Pulse> gPulse = getPulse(pulseId);
        if(gPulse.getCode() == Result.CODE.FAILURE) {
            return new Result<String>(Result.CODE.FAILURE, null, gPulse.getMessage());
        }
        
        Optional<UserLight> oUserLight = redisUserDao.getUserLight(userId.getId());
        Result<String> result = new Result<>(FAILURE, "Failed in unsubscribe");
        
        if(oUserLight.isPresent()) {
            UserLight userLight = oUserLight.get();
            redisPulseDao.unSubscribePulse(userLight);
            
            userLight.setSubscribedPulseId(0L);
            redisUserDao.storeUserLight(userLight);
            result = new Result<>(SUCCESS, "Success in unsubscribe");
        }
        
        return result;
    }
    
    private Result<Pulse> getPulse(PulseId pulseId) {
        Optional<Pulse> optPulse = redisPulseDao.getPulse(pulseId);
        
        return optPulse.isPresent() ? new Result<>(SUCCESS, optPulse.get()) : new Result<>(FAILURE, null, "Unabled to find " + pulseId);
    }
    
}
