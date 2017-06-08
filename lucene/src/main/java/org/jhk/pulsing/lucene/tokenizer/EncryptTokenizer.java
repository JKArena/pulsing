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
package org.jhk.pulsing.lucene.tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.jhk.pulsing.shared.util.AesCipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class EncryptTokenizer extends Tokenizer {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(EncryptTokenizer.class);
    
    private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
    
    private AesCipher _aCipher = new AesCipher();
    private boolean _decrypted;
    private int _position;
    private List<String> _values;
    
    @Override
    public boolean incrementToken() throws IOException {
        if(!_decrypted) {
            decrypt();
            _decrypted = true;
        }
        
        if(_position == _values.size()) {
            return false;
        }
        
        termAttribute.setEmpty();
        String token = _values.get(_position++);
        
        _LOGGER.info("EncryptTokenizer.incrementToken: token - " + _position + " / " + token);
        termAttribute.append(token);
        
        return true;
    }
    
    @Override
    public void reset() throws IOException {
        super.reset();
        
        _position = 0;
    }
    
    private void decrypt() throws IOException {
        _LOGGER.info("EncryptTokenizer.decrypt: Setting up...");
        
        StringBuilder encrypted = new StringBuilder();
        
        try(BufferedReader bReader = new BufferedReader(input)) {
            String read = bReader.readLine();
            encrypted.append(read);
        }
        catch(IOException iException) {
            iException.printStackTrace();
        }
        
        _LOGGER.info("EncryptTokenizer.decrypt: encrypted value > " + encrypted.toString());
        try {
            
            String decrypted = _aCipher.decrypt(encrypted.toString());
            
            _LOGGER.info("EncryptTokenizer.decrypt: decrypted value > " + decrypted);
            
            _values = Arrays.asList(decrypted.split(" "));
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException dException) {
            dException.printStackTrace();
        }
        
    }
    
}
