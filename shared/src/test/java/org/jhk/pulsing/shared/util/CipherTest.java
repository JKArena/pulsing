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

import static org.junit.Assert.assertTrue;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;

/**
 * @author Ji Kim
 */
public class CipherTest {
    
    @Test
    public void testEncryptDecrypt() {
        AesCipher first = new AesCipher();
        AesCipher second = new AesCipher();
        
        String[] eTest = new String[] {"foobar forever...", "overwatch dying TOT", "Never give up *_*"};
        
        Arrays.asList(eTest).forEach(value -> {
            try {
                String fEncrypted = first.encrypt(value);
                String sEncrypted = second.encrypt(value);
                
                String fDecrypted = first.decrypt(fEncrypted);
                String sDecrypted = second.decrypt(sEncrypted);
                
                assertTrue("Compare decryption values for " + value, fDecrypted.equals(sDecrypted));
                
                fDecrypted = first.decrypt(sEncrypted);
                sDecrypted = second.decrypt(fEncrypted);
                
                assertTrue("Compare decryption same when swapped (since don't want to expose as service atm) " + value, fDecrypted.equals(sDecrypted));
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                    | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException eException) {
                assertTrue("Failed encryption...", false);
                eException.printStackTrace();
            }
        });
        
    }
    
}
