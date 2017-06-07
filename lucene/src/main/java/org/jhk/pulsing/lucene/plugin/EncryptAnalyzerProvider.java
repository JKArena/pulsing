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
package org.jhk.pulsing.lucene.plugin;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.jhk.pulsing.lucene.analyzer.EncryptAnalyzer;

/**
 * @author Ji Kim
 */
public final class EncryptAnalyzerProvider extends AbstractIndexAnalyzerProvider<EncryptAnalyzer> {
    
    public static final String NAME = "encryptor";
    
    private final EncryptAnalyzer analyzer;
    
    public EncryptAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);
        
        analyzer = new EncryptAnalyzer();
    }
    
    public static EncryptAnalyzerProvider getEncryptAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new EncryptAnalyzerProvider(indexSettings, env, name, settings);
    }

    @Override
    public EncryptAnalyzer get() {
        return analyzer;
    }

}
