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
package org.jhk.pulsing.client.payload.light;

import java.io.Serializable;

import org.jhk.pulsing.serialization.avro.records.Picture;
import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.shared.util.CommonConstants;

/**
 * Light user class for MapComponent and etc
 * 
 * @author Ji Kim
 */
public class UserLight implements Serializable {
    
    private static final long serialVersionUID = -3192467748390587499L;
    
    private String name;
    private long id;
    private String picturePath;
    private long subscribedPulseId;
    
    public UserLight() {
        super();
    }
    
    public UserLight(User user) {
        super();
        
        name = user.getName().toString();
        id = user.getId().getId();
        
        Picture picture = user.getPicture();
        
        if(picture != null && picture.getUrl() != null) {
            picturePath = picture.getUrl().toString();
        } else {
            picturePath = "";
        }
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getPicturePath() {
        return picturePath;
    }
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
    
    public long getSubscribedPulseId() {
        return subscribedPulseId;
    }
    public void setSubscribedPulseId(long subscribedPulseId) {
        this.subscribedPulseId = subscribedPulseId;
    }

    @Override
    public int hashCode() {
        int hashCodeVal = CommonConstants.HASH_CODE_INIT_VALUE;
        hashCodeVal = CommonConstants.HASH_CODE_MULTIPLY_VALUE * hashCodeVal + name.hashCode();
        hashCodeVal = CommonConstants.HASH_CODE_MULTIPLY_VALUE * hashCodeVal + ((int) id);
        hashCodeVal = CommonConstants.HASH_CODE_MULTIPLY_VALUE * hashCodeVal + picturePath.hashCode();
        return hashCodeVal;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof UserLight)) {
            return false;
        }
        
        UserLight casted = (UserLight) obj;
        return casted.id == id;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("UserLight {");
        builder.append("id: " + id + ", ");
        builder.append("name: " + name + ", ");
        builder.append("subscribedPulseId: " + subscribedPulseId + ", ");
        builder.append("picturePath: " + picturePath);
        builder.append("}");
        
        return builder.toString();
    }
    
}
