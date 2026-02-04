package src.main.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AESAlgorithm {

    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final String KEY_DERIVATION_ALGO = "PBKDF2WithHmacSHA256";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
    private static final int AES_KEY_BIT = 256;
    private static final int ITERATION_COUNT = 600000;

    // Encrypt raw bytes (Used for compressed data)
    public String encrypt(byte[] data, String password) throws Exception {
        byte[] salt = getRandomBytes(SALT_LENGTH_BYTE);
        byte[] iv = getRandomBytes(IV_LENGTH_BYTE);

        SecretKey secretKey = getSecretKey(password, salt);

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        byte[] cipherText = cipher.doFinal(data);

        // Combine Salt + IV + CipherText
        ByteBuffer byteBuffer = ByteBuffer.allocate(salt.length + iv.length + cipherText.length);
        byteBuffer.put(salt);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);

        return Base64.getEncoder().encodeToString(byteBuffer.array());
    }

    // Encrypt string (Convenience method)
    public String encrypt(String pText, String password) throws Exception {
        return encrypt(pText.getBytes(StandardCharsets.UTF_8), password);
    }

    // Decrypt to raw bytes (Used for decompression)
    public byte[] decryptToBytes(String cText, String password) throws Exception {
        byte[] decode = Base64.getDecoder().decode(cText.getBytes(StandardCharsets.UTF_8));
        ByteBuffer byteBuffer = ByteBuffer.wrap(decode);

        byte[] salt = new byte[SALT_LENGTH_BYTE];
        byteBuffer.get(salt);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        byteBuffer.get(iv);

        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);

        SecretKey secretKey = getSecretKey(password, salt);

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        return cipher.doFinal(cipherText);
    }

    // Decrypt to String (Convenience method)
    public String decrypt(String cText, String password) throws Exception {
        return new String(decryptToBytes(cText, password), StandardCharsets.UTF_8);
    }

    private SecretKey getSecretKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGO);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, AES_KEY_BIT);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    private byte[] getRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }
}