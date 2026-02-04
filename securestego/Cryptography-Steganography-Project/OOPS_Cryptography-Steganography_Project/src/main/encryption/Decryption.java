package src.main.encryption;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * Core Decryption Engine.
 * REVERSES AES-256-GCM.
 * * LOGIC:
 * 1. Read Salt (16 bytes) & IV (12 bytes) from the header.
 * 2. Regenerate the Secret Key using the Password + Salt.
 * 3. Perform Authenticated Decryption.
 */
public class Decryption {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH_BIT = 256;

    // ==================================================================================
    // FILE DECRYPTION
    // ==================================================================================

    public void decryptFile(File inputFile, File outputFile, String password) throws Exception {
        try (FileInputStream fis = new FileInputStream(inputFile)) {

            // 1. Read the Salt (First 16 Bytes)
            byte[] salt = new byte[SALT_LENGTH_BYTE];
            if (fis.read(salt) != SALT_LENGTH_BYTE) {
                throw new Exception("File corrupted: Missing Salt header.");
            }

            // 2. Read the IV (Next 12 Bytes)
            byte[] iv = new byte[IV_LENGTH_BYTE];
            if (fis.read(iv) != IV_LENGTH_BYTE) {
                throw new Exception("File corrupted: Missing IV header.");
            }

            // 3. Regenerate Key
            SecretKey secretKey = getSecretKey(password, salt);

            // 4. Initialize Cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            // 5. Decrypt Stream
            try (CipherInputStream cis = new CipherInputStream(fis, cipher);
                 FileOutputStream fos = new FileOutputStream(outputFile)) {

                byte[] buffer = new byte[8192];
                int count;
                while ((count = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, count);
                }
            }
        }
    }

    // ==================================================================================
    // MESSAGE (STRING) DECRYPTION
    // ==================================================================================

    public String decryptMessage(String encryptedBase64, String password) throws Exception {
        // 1. Decode Base64 to get raw bytes
        byte[] combined = Base64.getDecoder().decode(encryptedBase64);

        // 2. Extract Salt
        byte[] salt = Arrays.copyOfRange(combined, 0, SALT_LENGTH_BYTE);

        // 3. Extract IV
        byte[] iv = Arrays.copyOfRange(combined, SALT_LENGTH_BYTE, SALT_LENGTH_BYTE + IV_LENGTH_BYTE);

        // 4. Extract Encrypted Content
        byte[] encryptedContent = Arrays.copyOfRange(combined, SALT_LENGTH_BYTE + IV_LENGTH_BYTE, combined.length);

        // 5. Regenerate Key & Decrypt
        SecretKey secretKey = getSecretKey(password, salt);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        byte[] decryptedBytes = cipher.doFinal(encryptedContent);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    // ==================================================================================
    // UTILITIES
    // ==================================================================================

    private SecretKey getSecretKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH_BIT);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }
}