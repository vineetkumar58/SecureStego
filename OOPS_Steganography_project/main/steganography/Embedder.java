package main.steganography;

import java.awt.image.BufferedImage;

public class Embedder {

    public BufferedImage embedMessage(BufferedImage image, String message) {
        // Create a copy of the image to avoid modifying the original
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        // Copy the original image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                outputImage.setRGB(x, y, image.getRGB(x, y));
            }
        }
        
        byte[] messageBytes = message.getBytes();
        int messageLength = messageBytes.length;
        
        // Check if the image can hold the message
        int maxBytes = (width * height * 3) / 8;
        if (messageLength + 4 > maxBytes) {
            throw new IllegalArgumentException("Message is too large for this image");
        }
        
        // Embed the length (4 bytes) followed by the message
        int[] lengthBytes = new int[4];
        lengthBytes[0] = (messageLength >> 24) & 0xFF;
        lengthBytes[1] = (messageLength >> 16) & 0xFF;
        lengthBytes[2] = (messageLength >> 8) & 0xFF;
        lengthBytes[3] = messageLength & 0xFF;
        
        // Start embedding data
        int bitIndex = 0;
        
        // First embed length (32 bits)
        for (int i = 0; i < 4; i++) {
            for (int bit = 7; bit >= 0; bit--) {
                int x = (bitIndex / 3) % width;
                int y = (bitIndex / 3) / width;
                int bitValue = (lengthBytes[i] >> bit) & 1;
                
                if (y < height) { // Safety check
                    embedBitInPixel(outputImage, x, y, bitIndex % 3, bitValue);
                    bitIndex++;
                }
            }
        }
        
        // Then embed message
        for (byte b : messageBytes) {
            for (int bit = 7; bit >= 0; bit--) {
                int x = (bitIndex / 3) % width;
                int y = (bitIndex / 3) / width;
                int bitValue = (b >> bit) & 1;
                
                if (y < height) { // Safety check
                    embedBitInPixel(outputImage, x, y, bitIndex % 3, bitValue);
                    bitIndex++;
                }
            }
        }
        
        return outputImage;
    }
    
    private void embedBitInPixel(BufferedImage image, int x, int y, int colorIndex, int bit) {
        int rgb = image.getRGB(x, y);
        int alpha = (rgb >> 24) & 0xFF;
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        
        switch (colorIndex) {
            case 0: // Red
                red = (red & 0xFE) | bit;
                break;
            case 1: // Green
                green = (green & 0xFE) | bit;
                break;
            case 2: // Blue
                blue = (blue & 0xFE) | bit;
                break;
        }
        
        int newRgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
        image.setRGB(x, y, newRgb);
    }
}




