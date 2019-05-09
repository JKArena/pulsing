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
package org.jhk.pulsing.client.pulse.internal;

import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.client.publisher.AbstractKafkaPublisher;
import org.jhk.pulsing.client.pulse.IPulseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Ji Kim
 */
@Service
public class PulseService extends AbstractKafkaPublisher 
                            implements IPulseService {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(PulseService.class);
    
    /**
     * For creation of pulse there are couple of tasks that must be done
     * 
     * 1) Add the pulse to Redis 
     * 2) Send the message to storm of the creation (need couple of different writes to Hadoop for Data + Edges for processing)
     * 3) Send the message to storm of the subscription (for trending of time interval)
     * 
     * Hmmm consider moving this to storm to prevent bad pulses from being created (i.e. filtering out bad words and etc)?
     * 
     * @param pulse
     * @return
     */
    @Override
    public void publishCreatePulse(Pulse pulse) {
        
        _LOGGER.debug("PulseService.createPulse: Sending pulse {}", pulse);
        getKafkaPublisher().produce(CommonConstants.TOPICS.PULSE_CREATE.toString(), pulse);
    }
    
    /**
     * 1) Send the message to storm of the subscription (update to redis taken care of by storm)
     * 2) Add the userLight to the pulse subscription in redis
     * 
     * @param pulse
     * @return
     */
    @Override
    public void publishPulseSubscription(Pulse pulse) {
        getKafkaPublisher().produce(CommonConstants.TOPICS.PULSE_SUBSCRIBE.toString(), pulse);
    }
    
}
