package main.steganography;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import javax.sound.sampled.*;

public class AudioSteganography {

    // 🎵 Embed base64-encoded message in a WAV file using LSB
    public void embedMessage(File inputWav, String message, File outputWav) throws Exception {
        byte[] messageBytes = message.getBytes("UTF-8");
        int messageLength = messageBytes.length;

        // Add 4 bytes (32-bit) length header
        byte[] lengthBytes = new byte[] {
            (byte)((messageLength >> 24) & 0xFF),
            (byte)((messageLength >> 16) & 0xFF),
            (byte)((messageLength >> 8) & 0xFF),
            (byte)(messageLength & 0xFF)
        };

        byte[] fullPayload = new byte[4 + messageLength];
        System.arraycopy(lengthBytes, 0, fullPayload, 0, 4);
        System.arraycopy(messageBytes, 0, fullPayload, 4, messageLength);

        // Convert to bits
        byte[] audioData = readAudioBytes(inputWav);
        int totalBits = fullPayload.length * 8;

        if (totalBits > audioData.length) {
            throw new IllegalArgumentException("Message is too large for the selected audio file.");
        }

        // Embed bits into LSB of audio bytes
        for (int i = 0; i < totalBits; i++) {
            int bit = (fullPayload[i / 8] >> (7 - (i % 8))) & 1;
            audioData[i] = (byte)((audioData[i] & 0xFE) | bit);
        }

        writeAudioBytes(inputWav, audioData, outputWav);
    }

    // 🎧 Extract base64-encoded message from WAV
    public String extractMessage(File audioFile) throws Exception {
        byte[] audioData = readAudioBytes(audioFile);

        // Extract 32-bit length
        int length = 0;
        for (int i = 0; i < 32; i++) {
            int bit = audioData[i] & 1;
            length = (length << 1) | bit;
        }

        if (length <= 0 || (32 + length * 8) > audioData.length) {
            throw new IllegalArgumentException("Invalid or corrupted hidden message length.");
        }

        byte[] messageBytes = new byte[length];
        for (int i = 0; i < length * 8; i++) {
            int bitIndex = i + 32;
            int bit = audioData[bitIndex] & 1;
            messageBytes[i / 8] = (byte)((messageBytes[i / 8] << 1) | bit);
        }

        return new String(messageBytes, "UTF-8");
    }

    // ✅ Safely read audio bytes from PCM WAV file (skipping header)
    private byte[] readAudioBytes(File file) throws Exception {
        byte[] fullAudioBytes = Files.readAllBytes(file.toPath());

        try (ByteArrayInputStream bais = new ByteArrayInputStream(fullAudioBytes)) {
            AudioInputStream stream = AudioSystem.getAudioInputStream(bais);
            AudioFormat format = stream.getFormat();

            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED &&
                format.getEncoding() != AudioFormat.Encoding.PCM_UNSIGNED) {
                throw new UnsupportedAudioFileException("Only PCM WAV files are supported.");
            }

            return Arrays.copyOfRange(fullAudioBytes, 44, fullAudioBytes.length); // skip 44-byte header
        }
    }

    private void writeAudioBytes(File originalFile, byte[] newAudioData, File outputFile) throws Exception {
        byte[] originalBytes = Files.readAllBytes(originalFile.toPath());
        byte[] header = Arrays.copyOfRange(originalBytes, 0, 44); // WAV header

        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            out.write(header);
            out.write(newAudioData);
        }
    }
}
