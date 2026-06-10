package com.erp.auth.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256-GCM 加密工具类, 用于ClientSecret加解密.
 *
 * @author AI
 * @since 2026-06-05
 */
public final class AesEncryptUtil {

    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final String MASKED = "********";
    private static final String PREFIX = "AES:";

    private AesEncryptUtil() {
    }

    public static String encrypt(String plainText, String secretKey) {
        try {
            byte[] keyBytes = getKeyBytes(secretKey);
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
            byte[] ciphertext = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return PREFIX + Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(ciphertext);
        } catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }

    public static String decrypt(String encryptedText, String secretKey) {
        try {
            if (encryptedText == null || !encryptedText.startsWith(PREFIX)) {
                return encryptedText;
            }
            String payload = encryptedText.substring(PREFIX.length());
            String[] parts = payload.split(":", 2);
            if (parts.length != 2) {
                throw new RuntimeException("Invalid encrypted format");
            }
            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] ciphertext = Base64.getDecoder().decode(parts[1]);
            byte[] keyBytes = getKeyBytes(secretKey);
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES decryption failed", e);
        }
    }

    public static String mask(String encryptedText) {
        return MASKED;
    }

    private static byte[] getKeyBytes(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[32];
        System.arraycopy(keyBytes, 0, result, 0, Math.min(keyBytes.length, 32));
        return result;
    }
}
