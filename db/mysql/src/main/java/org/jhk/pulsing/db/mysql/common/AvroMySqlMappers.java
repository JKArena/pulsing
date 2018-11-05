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
package org.jhk.pulsing.db.mysql.common;

import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import org.jhk.pulsing.db.mysql.model.MImage;
import org.jhk.pulsing.db.mysql.model.MUser;
import org.jhk.pulsing.serialization.avro.records.Picture;
import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;

/**
 * Helper class to map Avro <=> MySql entities
 * 
 * Do I want to use reflection or annotation here?  So hard to find usage for the new reflection api though...
 * 
 * Is there a better way to do this, it doesn't look nice ToT
 * 
 * @author Ji Kim
 */
public final class AvroMySqlMappers {
    
    private AvroMySqlMappers() {
        super();
    }
    
    public static MUser avroToMysql(User user) {
        
        MUser mUser = new MUser();
        
        UserId userId = user.getId();
        mUser.setId(userId.getId());
        
        mUser.setEmail(user.getEmail().toString());
        mUser.setName(user.getName().toString());
        mUser.setPassword(user.getPassword().toString());
        
        //is below even needed?
        Picture picture = user.getPicture();
        if(picture != null && picture.getName() != null) {
            
            ByteBuffer buffer = picture.getContent();
            
            if(buffer.array() != null && buffer.array().length > 0) {
                try {
                    MImage mImage = new MImage();
                    mImage.setImageName(picture.getName().toString());
                    mImage.setImageContent(new SerialBlob(buffer.array()));
                    mUser.setPicture(mImage);
                }catch(Exception except) {
                    except.printStackTrace();
                }
            }
        }
        
        return mUser;
    }
    
    public static User mySqlToAvro(MUser mUser) {
        
        User user = User.newBuilder().build();
        UserId userId = UserId.newBuilder().build();
        userId.setId(mUser.getId());
        
        user.setName(mUser.getName());
        user.setId(userId);
        user.setEmail(mUser.getEmail());
        user.setPassword(mUser.getPassword());
        
        MImage mImage = mUser.getPicture();
        if(mImage != null && mImage.getImageName() != null) {
            
            Blob blob = mImage.getImageContent();
            
            try {
                int length = (int) blob.length();
                
                if(length > 0) {
                    Picture picture = Picture.newBuilder().build();
                    
                    //make sure below is cool since a wrap
                    picture.setContent(ByteBuffer.wrap(blob.getBytes(1L, length)));
                    picture.setName(mImage.getImageName());
                    user.setPicture(picture);
                }
            } catch (SQLException sException) {
                sException.printStackTrace();
            }
        }
        
        return user;
    }
    
}
