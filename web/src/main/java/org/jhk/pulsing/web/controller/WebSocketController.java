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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Ji Kim
 */
@Controller
public class WebSocketController {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(WebSocketController.class);
    
    @MessageMapping("/pulseSubscribeSocketJS")
    @SendTo("/pulsingTopic/pulseSubscribe")
    public long pulseSubscribe(@RequestParam("pulseId") long pulseId, @RequestParam("userId") long userId) {
        _LOGGER.info("pulseSubscribe: " + pulseId);
        
        //notify new user subscribed to the pulse
        //should have the subscription time out (as a config?)
        return userId;
    }
    
}
