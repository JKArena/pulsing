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
package org.jhk.pulsing.plugin.freemarker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * @author Ji Kim
 */
public final class FreemarkerProcessor {
    
    private Configuration _config;
    
    private FreemarkerProcessor(File sDirectory, ClassLoader cLoader) {
        super();
        
        _config = new Configuration(Configuration.VERSION_2_3_23);
        _config.setTemplateLoader(new StreamFileTemplateLoader(sDirectory, cLoader));
    }
    
    public static synchronized FreemarkerProcessor getInstance(File sDirectory, ClassLoader cLoader) {
        return new FreemarkerProcessor(sDirectory, cLoader);
    }
    
    public void processTemplate(Map<String, Object> data, String readFileName, File destination) throws IOException, TemplateException {
    	File parentFile = destination.getParentFile();
    	if(!parentFile.exists()) {
    		parentFile.mkdirs();
    	}
    	
        BufferedWriter bWriter = Files.newBufferedWriter(destination.toPath(), Charset.forName("UTF-8"));
        _config.getTemplate(readFileName).process(data, bWriter);
        bWriter.flush();
    }
    
}
