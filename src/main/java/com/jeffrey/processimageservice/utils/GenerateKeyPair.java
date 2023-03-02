package com.jeffrey.processimageservice.utils;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


import java.io.FileOutputStream;
import java.security.*;
import java.util.Base64;

public class GenerateKeyPair {
    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String encodedPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String encodedPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        FileOutputStream publicKeyWriter = new FileOutputStream("/Users/jeffrey/IdeaProjects/processImageService/src/main/java/com/jeffrey/processimageservice/utils/rsa/public.key");
        FileOutputStream privateKeyWriter = new FileOutputStream("/Users/jeffrey/IdeaProjects/processImageService/src/main/java/com/jeffrey/processimageservice/utils/rsa/private.key");

        publicKeyWriter.write(encodedPublicKey.getBytes());
        privateKeyWriter.write(encodedPrivateKey.getBytes());
    }
}

