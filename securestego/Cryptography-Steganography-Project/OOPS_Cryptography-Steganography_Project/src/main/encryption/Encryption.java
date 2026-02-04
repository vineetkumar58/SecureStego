package src.main.encryption;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Core Encryption Engine.
 * STANDARD: AES-256-GCM (Galois/Counter Mode).
 * KEY DERIVATION: PBKDF2WithHmacSHA256 (65,536 Iterations).
 */
public class Encryption {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128; // Authentication Tag Length
    private static final int IV_LENGTH_BYTE = 12;  // NIST recommended IV length for GCM
    private static final int SALT_LENGTH_BYTE = 16;
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH_BIT = 256;

    // ==================================================================================
    // FILE ENCRYPTION
    // ==================================================================================

    public void encryptFile(File inputFile, File outputFile, String password) throws Exception {
        // 1. Generate Random Salt and IV
        byte[] salt = getRandomBytes(SALT_LENGTH_BYTE);
        byte[] iv = getRandomBytes(IV_LENGTH_BYTE);

        // 2. Derive Secret Key from Password
        SecretKey secretKey = getSecretKey(password, salt);

        // 3. Initialize Cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        // 4. Write Header (Salt + IV) to the beginning of the file
        // This is needed for decryption later
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(salt);
            fos.write(iv);

            // 5. Encrypt File Stream
            try (CipherOutputStream cos = new CipherOutputStream(fos, cipher);
                 FileInputStream fis = new FileInputStream(inputFile)) {

                byte[] buffer = new byte[8192]; // 8KB Buffer
                int count;
                while ((count = fis.read(buffer)) != -1) {
                    cos.write(buffer, 0, count);
                }
            }
        }
    }

    // ==================================================================================
    // MESSAGE (STRING) ENCRYPTION
    // ==================================================================================

    public String encryptMessage(String message, String password) throws Exception {
        byte[] salt = getRandomBytes(SALT_LENGTH_BYTE);
        byte[] iv = getRandomBytes(IV_LENGTH_BYTE);

        SecretKey secretKey = getSecretKey(password, salt);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

        // Combine all parts: [Salt] + [IV] + [EncryptedData]
        byte[] combined = new byte[salt.length + iv.length + encryptedBytes.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(iv, 0, combined, salt.length, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, salt.length + iv.length, encryptedBytes.length);

        // Return as Base64 String for easy storage in Steganography
        return Base64.getEncoder().encodeToString(combined);
    }

    // ==================================================================================
    // CRYPTO UTILITIES
    // ==================================================================================

    private SecretKey getSecretKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH_BIT);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    private byte[] getRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }
}