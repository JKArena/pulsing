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
package org.jhk.pulsing.plugin.maven;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.avro.data.RecordBuilder;
import org.apache.avro.specific.SpecificRecord;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jhk.pulsing.plugin.freemarker.FreemarkerProcessor;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;


/**
 * @author Ji Kim
 */
@Mojo(name="avrojson", defaultPhase=LifecyclePhase.GENERATE_RESOURCES)
public final class AvroJsonMojo extends AbstractMojo {
    
    private static final String DEFAULT_AVROJS_FREEMARKER_TEMPLATE = "META-INF/avrojs-template.ftl";
    private static final String DEFAULT_AVROJS_FREEMARKER_TEST_TEMPLATE = "META-INF/avrojs-template-test.ftl"; 
    
    /**
     * avro classes
     */
    @Parameter(required=true)
    private List<String> avroclasses;
    
    /**
     * Destination of where Javascript files will be dumped to
     */
    @Parameter(required=true)
    private File destination;
    
    /**
     * directory that contains freemarker template
     */
    @Parameter
    private File freemarkerDirectory;
    
    /**
     * File to be used for tests
     */
    @Parameter
    private File testdestination;
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        
        FreemarkerProcessor processor = FreemarkerProcessor.getInstance(freemarkerDirectory, getClass().getClassLoader());
        
        Map<String, Object> data = new HashMap<>();
        List<ClassInfo> classinfos = new LinkedList<>();
        
        for(String entry : avroclasses) {
            
            try {
                
                Class<? extends SpecificRecord> clazz = (Class<? extends SpecificRecord>) Class.forName(entry);
                Method method = clazz.getDeclaredMethod("newBuilder");
                
                RecordBuilder builder = (RecordBuilder) method.invoke(null);
                SpecificRecord record = (SpecificRecord) builder.build();
                String result = SerializationHelper.serializeAvroTypeToJSONString(record);
                
                classinfos.add(new ClassInfo(clazz.getSimpleName(), result));
                
            } catch (Throwable throwMe) {
                throwMe.printStackTrace();
                throw new MojoExecutionException(throwMe.getMessage());
            }
            
        }
        
        data.put("classinfos", classinfos);
        
        try {
            processor.processTemplate(data, DEFAULT_AVROJS_FREEMARKER_TEMPLATE, destination);
            
            if(testdestination != null) {
                processor.processTemplate(data, DEFAULT_AVROJS_FREEMARKER_TEST_TEMPLATE, testdestination);
            }
        } catch (Exception pException) {
            pException.printStackTrace();
            throw new MojoExecutionException(pException.getMessage());
        }
    }
    
    public static class ClassInfo {
        
        private String _clazz;
        private String _skeleton;
        
        private ClassInfo(String clazz, String skeleton) {
            super();
            
            _clazz = clazz;
            _skeleton = skeleton;
        }
        
        public String getClazz() {
            return _clazz;
        }
        public void setClazz(String clazz) {
            _clazz = clazz;
        }
        
        public String getSkeleton() {
            return _skeleton;
        }
        public void setSkeleton(String skeleton) {
            _skeleton = skeleton;
        }
        
    }
    
}
