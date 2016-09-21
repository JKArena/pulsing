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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.PulseId;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.shared.util.RedisConstants;
import org.jhk.pulsing.web.common.Result;

import static org.jhk.pulsing.shared.util.RedisConstants.REDIS_KEY.*;
import static org.jhk.pulsing.web.common.Result.CODE.*;
import org.jhk.pulsing.web.dao.IPulseDao;
import org.jhk.pulsing.web.dao.prod.db.AbstractRedisDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;

/**
 * @author Ji Kim
 */
@Repository
public class RedisPulseDao extends AbstractRedisDao
                            implements IPulseDao {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(RedisPulseDao.class);
    
    @Override
    public Optional<Pulse> getPulse(PulseId pulseId) {
        _LOGGER.debug("RedisPulseDao.getPulse: " + pulseId);
        
        String pulseJson = getJedis().get(PULSE_.toString() + pulseId.getId());
        Optional<Pulse> pulse = Optional.empty();
        
        if(pulseJson != null) {
            try {
                pulse = Optional.of(SerializationHelper.deserializeFromJSONStringToAvro(Pulse.class, Pulse.getClassSchema(), pulseJson));
            } catch (IOException dException) {
                dException.printStackTrace();
            }
        }
        
        return pulse;
    }
    
    /**
     * TODO: Do not push pulse if <value> and the <geolocation> area exists
     * 
     * 1) Put the pulse by id w/ default expire
     * 2) Put the pulse relation to GEO by lng + lat
     * 3) Put the pulse relation to tags values submitted
     * 
     * @param pulse
     * @return
     */
    @Override
    public Result<Pulse> createPulse(Pulse pulse) {
        _LOGGER.debug("RedisPulseDao.createPulse: " + pulse);
        
        Result<Pulse> result;
        
        try {
            double lat = pulse.getLat();
            double lng = pulse.getLng();
            
            /*
             * Hmmm...add denormalized content for easier fetch since the pulse itself can't be modified after creation 
             * (other than tags) and don't want to perform so many queries or should just go for memory optimization?
             * Decisions decisions o.O
             */
            String pulseJson = SerializationHelper.serializeAvroTypeToJSONString(pulse);
            getJedis().setex(PULSE_.toString() + pulse.getId().getId(), RedisConstants.CACHE_EXPIRE_DAY, pulseJson);
            getJedis().geoadd(PULSE_GEO_.toString(), lng, lat, pulseJson);
            
            for(CharSequence tag : pulse.getTags()) {
                getJedis().sadd(PULSE_TAGS_.toString() + tag, pulseJson);
            }
            
            result = new Result<>(SUCCESS, pulse);
        } catch (IOException sException) {
            result = new Result<>(FAILURE, sException.getMessage());
            sException.printStackTrace();
        }
        
        return result;
    }
    
    public List<Pulse> getMapPulseDataPoints(double lat, double lng) {
        
        List<Pulse> pDataPoints = new LinkedList<>();
        
        List<GeoRadiusResponse> response = getJedis().georadius(PULSE_GEO_.toString(), lng, lat, CommonConstants.DEFAULT_PULSE_RADIUS, GeoUnit.M);
        response.parallelStream().forEach(grResponse -> {
            
            try {
                pDataPoints.add(SerializationHelper.deserializeFromJSONStringToAvro(Pulse.class, Pulse.getClassSchema(), grResponse.getMemberByString()));
            } catch (IOException ioException) {
                _LOGGER.warn("Failure in parsing of georadiusresponse: " + grResponse);
                ioException.printStackTrace();
            }
        });
        
        return pDataPoints;
    }
    
    /**
     * Entries are held by time ranges so query the range
     * 
     * @param brEpoch before/start time range
     * @param cEpoch current/end time range
     * @return
     */
    public Optional<Set<String>> getTrendingPulseSubscriptions(long brEpoch, long cEpoch) {
        _LOGGER.debug("RedisPulseDao.getTrendingPulseSubscriptions: " + brEpoch + " - " + cEpoch);
        
        final int _LIMIT = 100;
        
        Set<String> result = getJedis().zrangeByScore(PULSE_SUBSCRIBE_.toString(), brEpoch, cEpoch, 0, _LIMIT);
        _LOGGER.debug("RedisPulseDao.getTrendingPulseSubscriptions.queryResult: " + result.size());
        return Optional.ofNullable(result);
    }
    
}
