package com.jeffrey.processimageservice.security;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

import com.jeffrey.processimageservice.entities.Info;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.Cipher;
import java.util.Base64;

public class Decrypt {
    private final Cipher cipher;
    private final PrivateKey privateKey;
    private final KeyFactory keyFactory;
    private final String RSA_ALGORITHM = "RSA";
    private final String AES_ALGORITHM = "AES";
    @Autowired
    public Decrypt(Info info) {

        byte[] privateKeyBytes = Base64.getDecoder().decode(info.getRsaPrivateKey());

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        try {
            keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            privateKey = keyFactory.generatePrivate(keySpec);
            cipher = Cipher.getInstance(RSA_ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized String rsaDecrypt(String decryptedParams) throws Exception {

        if (decryptedParams == null || StringUtils.isBlank(decryptedParams)) {
            return null;
        }

        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] encryptedParam = Base64.getDecoder().decode(decryptedParams);

        byte[] decryptedParam = cipher.doFinal(encryptedParam);

        return new String(decryptedParam);
    }
}