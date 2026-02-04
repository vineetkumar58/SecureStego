package src.main.steganography;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class Embedder {

    /**
     * Embeds a string message (usually Encrypted Base64) into an image.
     * * @param sourceFile  The original image file (JPG, PNG, BMP).
     * @param outputFile  The destination file (Will be saved as PNG).
     * @param message     The string to hide.
     * @throws Exception  If the image is too small or invalid.
     */
    public void embedMessage(File sourceFile, File outputFile, String message) throws Exception {
        if (sourceFile == null || !sourceFile.exists()) {
            throw new Exception("Source image not found.");
        }
        if (message == null || message.isEmpty()) {
            throw new Exception("Message is empty. Nothing to embed.");
        }

        // 1. Load the Image
        BufferedImage image = ImageIO.read(sourceFile);
        if (image == null) {
            throw new Exception("Failed to load image. Format might be unsupported.");
        }

        // 2. Convert to 3-Byte BGR (Standardize format for LSB)
        // This ensures that regardless of input (JPG, Indexed PNG), we work with standard RGB bytes.
        BufferedImage userSpaceImage = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        userSpaceImage.getGraphics().drawImage(image, 0, 0, null);

        // 3. Prepare Payload
        // We embed: [32-bit Length Header] + [Message Bytes]
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        int messageLength = messageBytes.length;

        // 4. Check Capacity
        // 3 bytes per pixel * (Width * Height) = Total available bytes.
        // We use 1 bit per byte (LSB), so we can store 1/8th of the total bytes.
        // Actually, we modify the byte directly, so 1 pixel (3 bytes) holds 3 bits.
        WritableRaster raster = userSpaceImage.getRaster();
        DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
        byte[] imageBytes = buffer.getData();

        // 32 bits for length = 32 bytes needed.
        // messageLength bytes = messageLength * 8 bits = messageLength * 8 bytes needed.
        int requiredBytes = 32 + (messageLength * 8);

        if (requiredBytes > imageBytes.length) {
            throw new Exception("Image is too small! Needed: " + requiredBytes +
                    " bytes, Available: " + imageBytes.length + " bytes.");
        }

        // 5. Embed Length Header (32 bits)
        // We use the first 32 bytes of the image to store the length of the message.
        int offset = 0;
        for (int i = 0; i < 32; i++) {
            int bit = (messageLength >> (31 - i)) & 1;
            imageBytes[offset] = (byte) ((imageBytes[offset] & 0xFE) | bit);
            offset++;
        }

        // 6. Embed Message Body
        for (byte b : messageBytes) {
            for (int i = 7; i >= 0; i--) {
                int bit = (b >> i) & 1;
                imageBytes[offset] = (byte) ((imageBytes[offset] & 0xFE) | bit);
                offset++;
            }
        }

        // 7. Save as PNG
        // IMPORTANT: Must be PNG. JPG compression would destroy the LSBs.
        if (!ImageIO.write(userSpaceImage, "png", outputFile)) {
            throw new Exception("Failed to save the Stego-Image.");
        }
    }
}