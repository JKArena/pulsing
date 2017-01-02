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

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.PulseId;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;
import org.jhk.pulsing.web.common.Result;
import org.jhk.pulsing.web.pojo.light.MapPulseCreate;
import org.jhk.pulsing.web.pojo.light.UserLight;
import org.jhk.pulsing.web.service.IPulseService;
import org.jhk.pulsing.web.service.IUserService;
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
    
    private ObjectMapper _objectMapper = new ObjectMapper();
    
    @Inject
    private IPulseService pulseService;
    
    @Inject
    private IUserService userService;
    
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
        
        Result<Pulse> result = pulseService.createPulse(pulse);
        
        if(result.getCode() == Result.CODE.SUCCESS) {
            pulse = result.getData();
            
            Optional<UserLight> oUserLight = userService.getUserLight(pulse.getUserId().getId());
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
    
    @RequestMapping(value="/getTrendingPulseSubscriptions", method=RequestMethod.GET)
    public @ResponseBody Map<Long, String> getTrendingPulseSubscriptions() {
        _LOGGER.debug("PulseController.getTrendingPulseSubscriptions");
        
        return pulseService.getTrendingPulseSubscriptions(10);
    }
    
    @RequestMapping(value="/getMapPulseDataPoints", method=RequestMethod.GET)
    public @ResponseBody Map<String, Set<UserLight>> getMapPulseDataPoints(@RequestParam double lat, @RequestParam double lng) {
        _LOGGER.debug("PulseController.getMapPulseDataPoints: " + lat + " / " + lng);
        
        return pulseService.getMapPulseDataPoints(lat, lng);
    }
    
    @RequestMapping(value="/subscribePulse/{pulseId}/{userId}", method=RequestMethod.PUT)
    public @ResponseBody Result<PulseId> subscribePulse(@PathVariable PulseId pulseId, @PathVariable UserId userId) {
        _LOGGER.debug("PulseController.subscribePulse: " + pulseId + " - " + userId);
        
        Result<Pulse> result = pulseService.getPulse(pulseId);
        if(result.getCode() == Result.CODE.FAILURE) {
            return new Result<PulseId>(Result.CODE.FAILURE, pulseId, result.getMessage());
        }
        
        return pulseService.subscribePulse(result.getData(), userId);
    }
    
    @RequestMapping(value="/unSubscribePulse/{pulseId}/{userId}", method=RequestMethod.PUT)
    public @ResponseBody Result<String> unSubscribePulse(@PathVariable PulseId pulseId, @PathVariable UserId userId) {
        _LOGGER.debug("PulseController.unSubscribePulse: " + pulseId + " - " + userId);
        
        Result<Pulse> result = pulseService.getPulse(pulseId);
        if(result.getCode() == Result.CODE.FAILURE) {
            return new Result<String>(Result.CODE.FAILURE, null, result.getMessage());
        }
        
        return pulseService.unSubscribePulse(result.getData(), userId);
    }
    
}
