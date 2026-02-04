package src.main.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtils {

    /**
     * Compresses raw byte data using GZIP.
     */
    public static byte[] compress(byte[] data) throws IOException {
        if (data == null || data.length == 0) return data;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            gzip.write(data);
        }
        return bos.toByteArray();
    }

    /**
     * Decompresses GZIP-compressed data.
     */
    public static byte[] decompress(byte[] compressedData) throws IOException {
        if (compressedData == null || compressedData.length == 0) return compressedData;

        // Check for GZIP magic header (0x1f 0x8b)
        if (!isCompressed(compressedData)) {
            return compressedData; // Return as-is if not compressed
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzip = new GZIPInputStream(bis)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzip.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
        }
        return bos.toByteArray();
    }

    /**
     * Checks if the byte array starts with the GZIP magic number.
     */
    public static boolean isCompressed(byte[] data) {
        return data.length >= 2 && (data[0] == (byte) 0x1f) && (data[1] == (byte) 0x8b);
    }
}