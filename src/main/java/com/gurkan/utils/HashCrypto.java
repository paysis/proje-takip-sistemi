package com.gurkan.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.gurkan.enums.EHashTypes;

public class HashCrypto {

    /**
     * Inspired from https://www.geeksforgeeks.org/sha-512-hash-in-java/
     * 
     * @param input
     * @param hashType
     * @return
     */
    public static String encrypt(String input, EHashTypes hashType) {
        String hashTypeStr = "";

        switch (hashType) {
            case SHA512:
                hashTypeStr = "SHA-512";
                break;
            default:
                hashTypeStr = "SHA-512";
                break;
        }

        try {
            MessageDigest md = MessageDigest.getInstance(hashTypeStr);

            BigInteger bint = new BigInteger(1, md.digest(input.getBytes()));

            String hashTxt = bint.toString(16);

            while (hashTxt.length() < 32) {
                hashTxt = '0' + hashTxt;
            }

            return hashTxt;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(
                    String.format("%s hash algoritmasi MessageDigest tarafindan taninmadi.", hashTypeStr));
        }
    }
}
