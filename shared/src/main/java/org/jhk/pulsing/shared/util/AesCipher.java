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
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * @author Ji Kim
 */
public final class AesCipher {
    
    public static final String DEFAULT_TRANSFORMATION = "AES/GCM/PKCS5Padding" ;
    public static final String DEFAULT_TAG = "pulsing.jhk.org";
    public static final int KEY_SIZE = 128;
    public static final int INITIAL_VECTOR_SIZE = 96;
    public static final int TAG_BIT_LENGTH = 128;
    
    private final String _transformation;
    private final String _tag;
    private final SecretKey _sKey;
    private final GCMParameterSpec _gpSpec;
    
    public AesCipher(String transformation, String tag) {
        super();
        
        _transformation = transformation;
        _tag = tag;
    }
    
    public AesCipher() {
        super();
        
        _transformation = DEFAULT_TRANSFORMATION;
        _tag = DEFAULT_TAG;
    }
    
    {
        KeyGenerator keygen;
        try {
            keygen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException nsaException) {
            nsaException.printStackTrace();
            throw new RuntimeException(nsaException);
        } 
        keygen.init(KEY_SIZE); 
        _sKey = keygen.generateKey();
        
        byte iVector[] = new byte[INITIAL_VECTOR_SIZE];
        new SecureRandom().nextBytes(iVector);
        _gpSpec = new GCMParameterSpec(TAG_BIT_LENGTH, iVector);
    }
    
    public byte[] encrypt(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
                                            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher eCipher = Cipher.getInstance(_transformation);
        eCipher.init(Cipher.ENCRYPT_MODE, _sKey, _gpSpec, new SecureRandom());
        eCipher.updateAAD(_tag.getBytes());
        
        return eCipher.doFinal(message.getBytes());
    }
    
    public byte[] decrypt(byte[] encrypted) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
                                            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher dCipher = Cipher.getInstance(_transformation);
        dCipher.init(Cipher.DECRYPT_MODE, _sKey, _gpSpec, new SecureRandom());
        dCipher.updateAAD(_tag.getBytes());
        
        return dCipher.doFinal(encrypted);
    }
    
}
