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
package org.jhk.interested.web.dao.dev;

import java.util.List;

import org.jhk.interested.serialization.avro.records.Interest;
import org.jhk.interested.serialization.avro.records.InterestId;
import org.jhk.interested.web.dao.IInterestDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Ji Kim
 */
@Component
public class InterestDao implements IInterestDao {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(InterestDao.class);

    @Override
    public Interest getInterest(InterestId interestId) {
        _LOGGER.info("getInterest", interestId);
        
        return null;
    }

    @Override
    public void createInterest(Interest interest) {
        _LOGGER.info("createInterest", interest);
        
    }

    @Override
    public void subscribeInterest(Interest interest) {
        _LOGGER.info("subscribeInterest", interest);
        
    }

    @Override
    public List<Interest> getTrendingInterests() {
        return null;
    }
    
}
