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
package org.jhk.pulsing.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.jhk.pulsing.lucene.tokenizer.EncryptTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class EncryptAnalyzer extends Analyzer {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(EncryptAnalyzer.class);

    @Override
    protected TokenStreamComponents createComponents(String field) {
        _LOGGER.info("EncryptAnalyzer.createComponents: {}", field);
        
        Tokenizer source = new EncryptTokenizer();
        TokenStream filter = new StandardFilter(source);
        return new TokenStreamComponents(source, filter);
    }

}
