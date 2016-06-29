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

import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.PulseId;
import org.jhk.pulsing.web.common.Result;
import org.jhk.pulsing.web.dao.IPulseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Ji Kim
 */
@Controller
@RequestMapping("/pulse")
public final class PulseController extends AbstractController {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(PulseController.class);
    
    @Autowired
    private IPulseDao pulseDao;
    
    @RequestMapping(value="/createPulse", method=RequestMethod.POST)
    public @ResponseBody Result<PulseId> createPulse(@RequestBody Pulse pulse) {
        _LOGGER.info("createPulse: " + pulse);
        
        return pulseDao.createPulse(pulse);
    }
    
    @RequestMapping(value="/getTrendingPulse", method=RequestMethod.GET)
    public @ResponseBody List<Pulse> getTrendingPulse() {
        _LOGGER.info("getTrendingPulse");
        
        return pulseDao.getTrendingPulse();
    }
    
}
