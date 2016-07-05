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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Ji Kim
 */
@Entity()
@DynamicInsert
@DynamicUpdate
@Table(name="USER")
public class MUser implements Serializable {
    
    private static final long serialVersionUID = 3496761818540000213L;

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @NotEmpty
    @Column(nullable=false, name="EMAIL")
    private String email;
    
    @NotEmpty
    @Column(nullable=false, name="NAME")
    private String name;
    
    @NotEmpty
    @Column(nullable=false, name="PASSWORD")
    private String password;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="LAST_MODIFIED",
            updatable=false,
            insertable=false)
    @org.hibernate.annotations.Generated(
            org.hibernate.annotations.GenerationTime.ALWAYS)
    private Date _signedUp;
    
    @Embedded
    private MImage _picture;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Date getSignedup() {
        return _signedUp;
    }
    
    public MImage getPicture() {
        return _picture;
    }
    public void setPicture(MImage picture) {
        _picture = picture;
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public boolean equals(Object instance) {
        if(!(instance instanceof MUser)) {
            return false;
        }
        MUser casted = (MUser) instance;
        return casted.id.equals(id);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("User {");
        builder.append("id: " + id + ", ");
        builder.append("email: " + email + ", ");
        builder.append("name: " + name + ", ");
        builder.append("password: " + password);
        builder.append("}");
        
        return builder.toString();
    }
    
}
