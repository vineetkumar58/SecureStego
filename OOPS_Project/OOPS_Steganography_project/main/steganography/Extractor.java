package main.steganography;

import java.awt.image.BufferedImage;

public class Extractor {

    public String extractMessage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Extract length (32 bits)
        int[] lengthBits = new int[32];
        
        for (int i = 0; i < 32; i++) {
            int x = (i / 3) % width;
            int y = (i / 3) / width;
            int colorIndex = i % 3;
            
            lengthBits[i] = extractBitFromPixel(image, x, y, colorIndex);
        }
        
        // Convert bits to length (4 bytes)
        int length = 0;
        for (int i = 0; i < 32; i++) {
            length = (length << 1) | lengthBits[i];
        }
        
        // Sanity check
        if (length <= 0 || length > (width * height * 3) / 8) {
            System.out.println("Detected message length: " + length);
            System.out.println("Maximum possible length: " + (width * height * 3) / 8);
            throw new IllegalArgumentException("Invalid message length detected. This image may not contain hidden data.");
        }
        
        // Extract message bytes
        byte[] messageBytes = new byte[length];
        
        for (int byteIndex = 0; byteIndex < length; byteIndex++) {
            byte extractedByte = 0;
            
            for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
                // Calculate bit position in the overall data stream
                // 32 bits for length + current position in message
                int overallBitIndex = 32 + (byteIndex * 8) + bitIndex;
                
                int x = (overallBitIndex / 3) % width;
                int y = (overallBitIndex / 3) / width;
                int colorIndex = overallBitIndex % 3;
                
                int bit = extractBitFromPixel(image, x, y, colorIndex);
                extractedByte = (byte)((extractedByte << 1) | bit);
            }
            
            messageBytes[byteIndex] = extractedByte;
        }
        
        return new String(messageBytes);
    }
    
    private int extractBitFromPixel(BufferedImage image, int x, int y, int colorIndex) {
        int rgb = image.getRGB(x, y);
        
        switch (colorIndex) {
            case 0: // Red
                return (rgb >> 16) & 1;
            case 1: // Green
                return (rgb >> 8) & 1;
            case 2: // Blue
                return rgb & 1;
            default:
                return 0;
        }
    }
}