/*
 * @Author: bgcode
 * @Date: 2025-03-16 14:14:27
 * @LastEditTime: 2025-03-17 04:13:05
 * @LastEditors: bgcode
 * @Description: 描述
 * @FilePath: /TVjar/app/src/main/java/com/github/catvod/utils/decrpy.java
 * 本项目采用GPL 许可证，欢迎任何人使用、修改和分发。
 */
package com.github.catvod.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
public class decrpy {
    public static String encrypt(String data) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec("66dc309cbeeca454".getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec("66dc309cbeeca454".getBytes(StandardCharsets.UTF_8));
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 解密函数
    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec("66dc309cbeeca454".getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec("66dc309cbeeca454".getBytes(StandardCharsets.UTF_8));
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
   
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder hashStringBuilder = new StringBuilder();
            for (byte b : digest) {
                hashStringBuilder.append(String.format("%02x", b & 0xff));
            }
            return hashStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // SHA-1 加密方法
    public static String sha1(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder hashStringBuilder = new StringBuilder();
            for (byte b : digest) {
                hashStringBuilder.append(String.format("%02x", b & 0xff));
            }
            return hashStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // SHA-256 加密方法
    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder hashStringBuilder = new StringBuilder();
            for (byte b : digest) {
                hashStringBuilder.append(String.format("%02x", b & 0xff));
            }
            return hashStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String Dm(String data, String keyHex, String ivHex) throws Exception {
        // 将十六进制的密钥转换为字节数组
        byte[] keyBytes = hexToBytes(keyHex);
        // 创建 AES 密钥规范
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // 将十六进制的 IV 转换为字节数组
        byte[] ivBytes = hexToBytes(ivHex);
        // 创建 IV 参数规范
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

        // 创建 Cipher 实例，使用 AES/CBC/PKCS5Padding 模式
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // 初始化为解密模式
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        // 将 Base64 编码的密文解码为字节数组
        byte[] decodedData = Base64.getDecoder().decode(data);
        // 进行解密操作
        byte[] decryptedBytes = cipher.doFinal(decodedData);

        // 将解密后的字节数组转换为 UTF-8 字符串
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
