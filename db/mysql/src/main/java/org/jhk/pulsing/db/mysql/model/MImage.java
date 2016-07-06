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
package org.jhk.pulsing.db.mysql.model;

import java.io.Serializable;
import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

import org.jhk.pulsing.shared.util.PulsingConstants;

/**
 * @author Ji Kim
 */
@MappedSuperclass
@Embeddable
public class MImage implements Serializable {

    private static final long serialVersionUID = -8365376799315291584L;
    
    private String imageName;
    private Blob imageContent;
    
    @Column(name="IMAGE_NAME",
            length=100)
    public String getImageName() {
        return imageName;
    }
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    
    @Lob
    @Column(name="IMAGE_CONTENT",
            length=65536)
    public Blob getImageContent() {
        return imageContent;
    }
    public void setImageContent(Blob imageContent) {
        this.imageContent = imageContent;
    }
    
    @Override
    public int hashCode() {
        int hashCodeVal = PulsingConstants.HASH_CODE_INIT_VALUE;
        hashCodeVal = PulsingConstants.HASH_CODE_MULTIPLY_VALUE * hashCodeVal + imageName.hashCode();
        return hashCodeVal;
    }
    
    @Override
    public boolean equals(Object instance) {
        if(!(instance instanceof MImage)) {
            return false;
        }
        
        MImage casted = (MImage) instance;
        return casted.imageName.equals(imageName) && casted.imageContent.equals(imageContent);
    }
    
    @Override
    public String toString() {
        
        StringBuilder builder = new StringBuilder();
        builder.append("Image {");
        builder.append("imageName: " + imageName);
        builder.append("}");
        
        return builder.toString();
    }

}
