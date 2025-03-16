package com.github.catvod.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class decrpy {
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
}
