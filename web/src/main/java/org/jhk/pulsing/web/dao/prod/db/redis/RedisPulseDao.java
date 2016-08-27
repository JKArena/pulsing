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
package org.jhk.pulsing.web.dao.prod.db.redis;

import java.util.Optional;
import java.util.Set;

import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.PulseId;
import org.jhk.pulsing.shared.util.RedisConstants;
import org.jhk.pulsing.web.common.Result;
import static org.jhk.pulsing.web.common.Result.CODE.*;
import org.jhk.pulsing.web.dao.IPulseDao;
import org.jhk.pulsing.web.dao.prod.db.AbstractRedisDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * @author Ji Kim
 */
@Repository
public class RedisPulseDao extends AbstractRedisDao
                            implements IPulseDao {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(RedisPulseDao.class);
    private static final int _LIMIT = 100;
    
    @Override
    public Optional<Pulse> getPulse(PulseId pulseId) {
        _LOGGER.debug("RedisPulseDao.getPulse: " + pulseId);
        
        return Optional.empty();
    }

    @Override
    public Result<Pulse> createPulse(Pulse pulse) {
        _LOGGER.debug("RedisPulseDao.createPulse: " + pulse);
        
        return new Result<>(FAILURE, "dummy");
    }
    
    public Optional<Set<String>> getTrendingPulseSubscriptions(long brEpoch, long cEpoch) {
        _LOGGER.debug("RedisPulseDao.getTrendingPulseSubscriptions: " + brEpoch + " - " + cEpoch);
        
        Set<String> result = getJedis().zrangeByScore(RedisConstants.REDIS_KEY.SUBSCRIBE_PULSE_.toString(), brEpoch, cEpoch, 0, _LIMIT);
        _LOGGER.debug("RedisPulseDao.getTrendingPulseSubscriptions.queryResult: " + result.size());
        return Optional.ofNullable(result);
    }
    
}
