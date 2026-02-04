package src.main.steganography;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.BitSet;

/**
 * PRO-LEVEL AUDIO STEGANOGRAPHY ENGINE.
 * Implements Pseudo-Random Scatter Injection on WAV Audio.
 * * LOGIC:
 * 1. Skips 44-byte WAV Header.
 * 2. Writes 32-bit Length Header sequentially (Handshake).
 * 3. Scatters Payload bits across random samples using Password Seed.
 */
public class AudioSteganography {

    private static final int WAV_HEADER_SIZE = 44;

    // ==================================================================================
    // EMBEDDING LOGIC (Scatter Mode)
    // ==================================================================================

    public void embedMessage(File sourceFile, File destFile, String message, String password) throws Exception {
        // 1. Read All Bytes
        byte[] audioBytes = readFile(sourceFile);

        // 2. Prepare Payload
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        byte[] lengthBytes = intToBytes(messageBytes.length);

        // 3. Capacity Check
        // Available space = Total - Header
        int dataAreaSize = audioBytes.length - WAV_HEADER_SIZE;
        int requiredBits = (lengthBytes.length + messageBytes.length) * 8;

        if (requiredBits > dataAreaSize) {
            throw new Exception("Audio file too short. Need " + requiredBits + " samples, have " + dataAreaSize);
        }

        // 4. Initialize PRNG
        SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
        prng.setSeed(password.getBytes());

        // 5. EMBED HEADER (Sequential - First 32 bits after WAV Header)
        int audioIndex = WAV_HEADER_SIZE;
        for (byte b : lengthBytes) {
            for (int i = 7; i >= 0; i--) {
                int bit = (b >>> i) & 1;
                audioBytes[audioIndex] = (byte) ((audioBytes[audioIndex] & 0xFE) | bit);
                audioIndex++;
            }
        }

        // 6. EMBED PAYLOAD (Scatter Mode)
        // Use BitSet to track used samples (relative to data area)
        BitSet usedSamples = new BitSet(dataAreaSize);

        // Mark the first 32 bits (Length Header) as used
        usedSamples.set(0, 32);

        for (byte b : messageBytes) {
            for (int i = 7; i >= 0; i--) {
                int bit = (b >>> i) & 1;

                // Find random unused sample relative to data area
                int randomOffset = findUnusedIndex(prng, dataAreaSize, usedSamples);

                // Actual index = WAV Header + Random Offset
                int actualIndex = WAV_HEADER_SIZE + randomOffset;

                audioBytes[actualIndex] = (byte) ((audioBytes[actualIndex] & 0xFE) | bit);
                usedSamples.set(randomOffset);
            }
        }

        // 7. Save File
        writeFile(destFile, audioBytes);
    }

    // ==================================================================================
    // EXTRACTION LOGIC (Scatter Mode)
    // ==================================================================================

    public String extractMessage(File sourceFile, String password) throws Exception {
        byte[] audioBytes = readFile(sourceFile);
        int dataAreaSize = audioBytes.length - WAV_HEADER_SIZE;

        // 1. Initialize PRNG
        SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
        prng.setSeed(password.getBytes());

        // 2. Extract Length Header (Sequential)
        byte[] lengthBytes = new byte[4];
        int audioIndex = WAV_HEADER_SIZE;

        for (int i = 0; i < 4; i++) {
            for (int bit = 7; bit >= 0; bit--) {
                int lsb = audioBytes[audioIndex] & 1;
                lengthBytes[i] = (byte) ((lengthBytes[i] | (lsb << bit)));
                audioIndex++;
            }
        }
        int messageLength = bytesToInt(lengthBytes);

        // Sanity Check
        if (messageLength < 0 || (messageLength * 8) > dataAreaSize) {
            throw new Exception("Invalid Data Header (Possible Wrong Password).");
        }

        // 3. Extract Payload (Scatter Mode)
        BitSet usedSamples = new BitSet(dataAreaSize);
        usedSamples.set(0, 32); // Skip Header

        byte[] messageBytes = new byte[messageLength];

        for (int i = 0; i < messageLength; i++) {
            for (int bit = 7; bit >= 0; bit--) {
                int randomOffset = findUnusedIndex(prng, dataAreaSize, usedSamples);
                int actualIndex = WAV_HEADER_SIZE + randomOffset;

                int lsb = audioBytes[actualIndex] & 1;
                messageBytes[i] = (byte) ((messageBytes[i] | (lsb << bit)));

                usedSamples.set(randomOffset);
            }
        }

        return new String(messageBytes, StandardCharsets.UTF_8);
    }

    // ==================================================================================
    // UTILITIES
    // ==================================================================================

    private int findUnusedIndex(SecureRandom prng, int max, BitSet used) {
        int index;
        // Rejection Sampling: Keep picking random numbers until we find an unused one
        do {
            index = prng.nextInt(max);
        } while (used.get(index));
        return index;
    }

    private byte[] readFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return fis.readAllBytes();
        }
    }

    private void writeFile(File file, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
    }

    private byte[] intToBytes(int i) {
        return new byte[]{ (byte)(i >> 24), (byte)(i >> 16), (byte)(i >> 8), (byte)i };
    }

    private int bytesToInt(byte[] b) {
        return ((b[0] & 0xFF) << 24) | ((b[1] & 0xFF) << 16) | ((b[2] & 0xFF) << 8) | (b[3] & 0xFF);
    }
}