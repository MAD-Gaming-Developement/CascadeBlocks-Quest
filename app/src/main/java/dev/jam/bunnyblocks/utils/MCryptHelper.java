package dev.jam.bunnyblocks.utils;

import android.util.Log;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

public class MCryptHelper {

    private static final String METHOD = "AES/CBC/PKCS5Padding";
    private static final String IV = "fedcba9876543210";

    public static String decrypt(String message, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        if (Objects.equals(message, "")) {
            Log.e("MCrypt:Error","Message cannot be empty");
        }
        else {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(METHOD);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes()));
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(message));

            byte[] trimmedBytes = new byte[decryptedBytes.length - 16];
            System.arraycopy(decryptedBytes, 16, trimmedBytes, 0, trimmedBytes.length);

            return new String(trimmedBytes, StandardCharsets.UTF_8);
        }
        return message;
    }
}