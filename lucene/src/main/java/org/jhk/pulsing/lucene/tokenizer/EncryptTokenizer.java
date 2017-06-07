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
import java.io.StringReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.jhk.pulsing.shared.util.AesCipher;

/**
 * Initial try using StandardTokenizer in composition pattern
 * 
 * @author Ji Kim
 */
public final class EncryptTokenizer extends Tokenizer {
    
    private StandardTokenizer _sTokenizer = new StandardTokenizer();
    private AesCipher _aCipher = new AesCipher();
    private StringReader _sReader;
    private boolean _started;
    
    @Override
    public boolean incrementToken() throws IOException {
        if(!_started) {
            decrypt();
            _sTokenizer.setReader(_sReader);
        }
        
        return _sTokenizer.incrementToken();
    }
    
    @Override
    public void reset() throws IOException {
        super.reset();
        
        _started = false;
        _sTokenizer.reset();
    }
    
    @Override
    public void close() throws IOException {
        super.close();
        
        _started = false;
        _sTokenizer.close();
    }
    
    @Override
    public void end() throws IOException {
        super.end();
        
        _started = false;
        _sTokenizer.end();
    }
    
    private void decrypt() {
        
        StringBuilder builder = new StringBuilder();
        
        try(BufferedReader bReader = new BufferedReader(input)) {
            builder.append(new String(_aCipher.decrypt(bReader.readLine().getBytes())));
        }
        catch(IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException 
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException iException) {
            iException.printStackTrace();
        }
        
        _sReader = new StringReader(builder.toString());
    }
    
}
