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

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.web.common.Result;
import org.jhk.pulsing.web.service.IPulseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Ji Kim
 */
@Controller
@RequestMapping("/pulse")
public class PulseController extends AbstractController {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(PulseController.class);
    
    @Inject
    private IPulseService pulseService;
    
    @RequestMapping(value="/createPulse", method=RequestMethod.POST, consumes={MediaType.MULTIPART_FORM_DATA_VALUE})
    public @ResponseBody Result<Pulse> createPulse(@RequestParam Pulse pulse) {
        _LOGGER.debug("PulseController.createPulse: " + pulse);
        
        return pulseService.createPulse(pulse);
    }
    
    @RequestMapping(value="/getTrendingPulseSubscriptions", method=RequestMethod.GET)
    public @ResponseBody Map<Long, String> getTrendingPulseSubscriptions() {
        _LOGGER.debug("PulseController.getTrendingPulseSubscriptions");
        
        return pulseService.getTrendingPulseSubscriptions(10);
    }
    
    @RequestMapping(value="/getMapPulseDataPoints", method=RequestMethod.GET)
    public @ResponseBody List<Pulse> getMapPulseDataPoints(@RequestParam Double lat, @RequestParam Double lng) {
        _LOGGER.debug("PulseController.getMapPulseDataPoints: " + lat + " - " + lng);
        
        return pulseService.getMapPulseDataPoints(lat, lng);
    }
    
}
