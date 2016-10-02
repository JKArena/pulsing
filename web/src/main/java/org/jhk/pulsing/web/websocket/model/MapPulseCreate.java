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
package org.jhk.pulsing.web.websocket.model;

import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.web.pojo.light.UserLight;

/**
 * @author Ji Kim
 */
public class MapPulseCreate {
    
    private UserLight _userLight;
    private String _pulse;
    
    public MapPulseCreate() {
        super();
    }
    
    public MapPulseCreate(UserLight userLight, String pulse) {
        super();
        
        _userLight = userLight;
        _pulse = pulse;
    }
    
    public UserLight getUserLight() {
        return _userLight;
    }
    public void setUserLight(UserLight userLight) {
        _userLight = userLight;
    }

    public String getPulse() {
        return _pulse;
    }
    public void setPulse(String pulse) {
        _pulse = pulse;
    }

    @Override
    public int hashCode() {
        int hashCodeVal = CommonConstants.HASH_CODE_INIT_VALUE;
        hashCodeVal = CommonConstants.HASH_CODE_MULTIPLY_VALUE * hashCodeVal + _userLight.hashCode();
        hashCodeVal = CommonConstants.HASH_CODE_MULTIPLY_VALUE * hashCodeVal + _pulse.hashCode();
        return hashCodeVal;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof MapPulseCreate)) {
            return false;
        }
        
        MapPulseCreate casted = (MapPulseCreate) obj;
        return casted._pulse.equals(_pulse) && casted._userLight.equals(_userLight);
    }
    
}
