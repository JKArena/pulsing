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
package org.jhk.pulsing.shared.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * TODO: Use javax.crypto.Cipher
 * 
 * @author Ji Kim
 */
public final class AesCipher {
    
    private final Base64.Encoder _encoder = Base64.getEncoder();
    private final Base64.Decoder _decoder = Base64.getDecoder();
    
    public AesCipher(String transformation, String tag) {
        super();
    }
    
    public AesCipher() {
        super();
    }
    
    public String encrypt(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
                                            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        return _encoder.encodeToString(message.getBytes());
    }
    
    public String decrypt(String encrypted) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
                                            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] decoded = _decoder.decode(encrypted);
        
        return new String(decoded);
    }
    
}
