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
package org.jhk.interested.plugin.freemarker;

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
public final class AvroJsonProcessor {
    
    private Configuration _config;
    private File _destination;
    
    private AvroJsonProcessor(File sDirectory, ClassLoader cLoader, File destination) {
        super();
        
        _config = new Configuration(Configuration.VERSION_2_3_23);
        _config.setTemplateLoader(new StreamFileTemplateLoader(sDirectory, cLoader));
        _destination = destination;
    }
    
    public static synchronized AvroJsonProcessor getInstance(File sDirectory, ClassLoader cLoader, File destination) {
        return new AvroJsonProcessor(sDirectory, cLoader, destination);
    }
    
    public void processTemplate(Map<String, Object> data, String readFileName) throws IOException, TemplateException {
        BufferedWriter bWriter = Files.newBufferedWriter(_destination.toPath(), Charset.forName("UTF-8"));
        _config.getTemplate(readFileName).process(data, bWriter);
        bWriter.flush();
    }
    
}
