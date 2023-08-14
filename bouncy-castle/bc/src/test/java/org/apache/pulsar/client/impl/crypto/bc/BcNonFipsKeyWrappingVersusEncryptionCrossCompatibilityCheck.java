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
package org.apache.pulsar.client.impl.crypto.bc;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.testng.Assert.assertTrue;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.codec.DecoderException;
import org.apache.pulsar.client.impl.crypto.BcVersionSpecificCryptoUtility;
import org.apache.pulsar.client.impl.crypto.EncKeyReader;
import org.apache.pulsar.client.impl.crypto.WrappingVersusEncryptionCrossCompatibilityTestBase;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BcNonFipsKeyWrappingVersusEncryptionCrossCompatibilityCheck
        extends WrappingVersusEncryptionCrossCompatibilityTestBase {

    @DataProvider
    @Override
    public Object[][] badEncryptionInputs()
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, NoSuchProviderException,
            DecoderException {
        return new Object[][]{
                {EncKeyReader.getKeyAsPEM(EncKeyReader.KeyType.PUBLIC, "rsa-256.pem", ImmutableMap.of()),
                        EncKeyReader.getKeyAsPEM(EncKeyReader.KeyType.PRIVATE, "rsa-256.pem", ImmutableMap.of()),
                        loadAESKEy("aes128bit"),
                        allOf(
                                instanceOf(ArrayIndexOutOfBoundsException.class),
                                hasProperty("message", containsString("too much data for RSA block"))
                        )
                },
                {EncKeyReader.getKeyAsPEM(EncKeyReader.KeyType.PUBLIC, "rsa-256.pem", ImmutableMap.of()),
                        EncKeyReader.getKeyAsPEM(EncKeyReader.KeyType.PRIVATE, "rsa-256.pem", ImmutableMap.of()),
                        loadAESKEy("aes256bit"),
                        allOf(
                                instanceOf(ArrayIndexOutOfBoundsException.class),
                                hasProperty("message", containsString("too much data for RSA block"))
                        )
                }
        };
    }

    @Test
    public void testNonFipsLoaded() {
        BcVersionSpecificCryptoUtility bcFipsSpecificUtility = BcVersionSpecificCryptoUtility.INSTANCE;
        assertTrue(bcFipsSpecificUtility instanceof BCNonFipsSpecificUtility);
    }
}
