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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import freemarker.cache.TemplateLoader;

/**
 * Will first use directory possibly passed in as a parameter; if not provided 
 * will use getResourceAsStream on the classloader
 * 
 * @author Ji Kim
 */
public final class StreamFileTemplateLoader implements TemplateLoader {
    
    private File _directory;
    private ClassLoader _cLoader;
    private Map<Object, Reader> _mapper;
    
    public StreamFileTemplateLoader(File directory, ClassLoader cLoader) {
        super();
        
        _directory = directory;
        _cLoader = cLoader;
        _mapper = new HashMap<>();
    }
    
    @Override
    public Object findTemplateSource(String name) throws IOException {
        return name;
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        
        Reader reader = _mapper.remove(templateSource);
        if(reader != null) {
            reader.close();
        }
    }

    @Override
    public long getLastModified(Object templateSource) {
        return -1;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        Reader reader = _mapper.get(templateSource);
        
        if(reader == null) {
            String tSource = (String) templateSource;
            if(_directory != null) {
                reader = Files.newBufferedReader(Paths.get(_directory.getAbsolutePath(), tSource), Charset.forName(encoding));
            }else {
                reader = new BufferedReader(new InputStreamReader(_cLoader.getResourceAsStream(tSource), encoding));
            }
            
            _mapper.put(templateSource, reader);
        }
        
        return reader;
    }

}
