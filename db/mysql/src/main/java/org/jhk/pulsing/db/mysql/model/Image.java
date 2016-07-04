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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.jhk.pulsing.shared.util.PulsingConstants;

/**
 * @author Ji Kim
 */
@Entity
@Table(name="IMAGE")
public class Image implements Serializable {

    private static final long serialVersionUID = -8365376799315291584L;
    
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long _id;
    
    private String _name;
    private Blob _content;
    
    @NotEmpty
    @Column(name="NAME",
            nullable=false,
            length=100)
    public String getName() {
        return _name;
    }
    public void setName(String name) {
        _name = name;
    }
    
    @NotEmpty
    @Lob
    @Column(name="CONTENT",
            nullable=false,
            length=65536)
    public Blob getContent() {
        return _content;
    }
    public void setContent(Blob content) {
        _content = content;
    }
    
    public Long getId() {
        return _id;
    }
    public void setId(Long id) {
        _id = id;
    }
    @Override
    public int hashCode() {
        int hashCodeVal = PulsingConstants.HASH_CODE_INIT_VALUE;
        hashCodeVal = PulsingConstants.HASH_CODE_MULTIPLY_VALUE * hashCodeVal + _name.hashCode();
        return hashCodeVal;
    }
    
    @Override
    public boolean equals(Object instance) {
        if(!(instance instanceof Image)) {
            return false;
        }
        
        Image casted = (Image) instance;
        return casted._id.equals(_id) && casted._name.equals(_name) && casted._content.equals(_content);
    }
    
    @Override
    public String toString() {
        
        StringBuilder builder = new StringBuilder();
        builder.append("Image {");
        builder.append("name: " + _name);
        builder.append("}");
        
        return builder.toString();
    }

}
