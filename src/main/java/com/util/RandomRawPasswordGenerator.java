package com.util;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomRawPasswordGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

//    public static String generateRandomPassword(int byteLength) {
    public static String generateRandomPassword() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

//    public static void main(String[] args) {
//        System.out.println(generateRandomPassword(16)); // 16 bytes → 約 22 字元
//    }
}
