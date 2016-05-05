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
package org.jhk.interested.plugin.maven;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.avro.data.RecordBuilder;
import org.apache.avro.specific.SpecificRecord;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jhk.interested.serialization.avro.serializers.SerializationFactory;

/**
 * @author Ji Kim
 */
@Mojo(name="avrojson", defaultPhase=LifecyclePhase.COMPILE)
public final class AvroJsonMojo extends AbstractMojo {
    
    /**
     * avro classes
     */
    @Parameter
    private List<String> avroclasses;
    
    /**
     * Destination of where Javascript files will be dumped to
     */
    @Parameter
    private File destination;
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        
        for(String entry : avroclasses) {
            
            try {
                
                Class<? extends SpecificRecord> clazz = (Class<? extends SpecificRecord>) Class.forName(entry);
                Method method = clazz.getDeclaredMethod("newBuilder");
                RecordBuilder builder = (RecordBuilder) method.invoke(null);
                
                SpecificRecord record = (SpecificRecord) builder.build();
                String result = SerializationFactory.serializeAvroTypeToJSONString(record);
                System.out.println("GOT " + result);
                
            } catch (Throwable throwMe) {
                throwMe.printStackTrace();
                throw new MojoExecutionException(throwMe.getMessage());
            }
            
        }
    }
    
}
