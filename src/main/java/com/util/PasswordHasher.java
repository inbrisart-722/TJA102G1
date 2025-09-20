// 已不需要使用 先 archive -> 改採 BCrpyptEncoder of Spring Security

//package com.util;
//
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//
//// 待轉成 Spring Security -> BCryptPasswordEncoder 
//public class PasswordHasher {
//
//    /**
//     * 將給定的密碼字串轉換為 SHA-256 雜湊值。
//     *
//     * @param password 待雜湊的明文密碼
//     * @return 雜湊後的十六進位字串
//     * @throws NoSuchAlgorithmException 如果 JVM 不支援 SHA-256 演算法
//     */
//    public static String hashPassword(String password) throws NoSuchAlgorithmException {
//        // 取得 MessageDigest 實例，指定演算法為 SHA-256
//        MessageDigest digest = MessageDigest.getInstance("SHA-256");
//
//        // 將密碼字串轉換為位元組陣列並進行雜湊
//        byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
//
//        // 將雜湊後的位元組陣列轉換為十六進位字串
//        return bytesToHex(encodedhash);
//    }
//
//    /**
//     * 將位元組陣列轉換為十六進位字串。
//     *
//     * @param hash 雜湊後的位元組陣列
//     * @return 十六進位字串
//     */
//    private static String bytesToHex(byte[] hash) {
//        StringBuilder hexString = new StringBuilder(2 * hash.length);
//        for (int i = 0; i < hash.length; i++) {
//            String hex = Integer.toHexString(0xff & hash[i]);
//            if (hex.length() == 1) {
//                hexString.append('0');
//            }
//            hexString.append(hex);
//        }
//        return hexString.toString();
//    }
//
////    public static void main(String[] args) {
////        String originalPassword = "MySuperSecretPassword123";
////        try {
////            String hashedPassword = hashPassword(originalPassword);
////            System.out.println("原始密碼: " + originalPassword);
////            System.out.println("雜湊後結果: " + hashedPassword);
////        } catch (NoSuchAlgorithmException e) {
////            e.printStackTrace();
////            System.err.println("找不到 SHA-256 演算法！");
////        }
////    }
//}
