package src.main.encryption;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class AbstractCrypto {

    protected final AESAlgorithm aesAlgorithm;

    public AbstractCrypto() {
        this.aesAlgorithm = new AESAlgorithm();
    }

    protected byte[] readBytes(File inputFile) throws IOException {
        if (inputFile == null || !inputFile.exists()) {
            throw new IOException("Input file not found: " + (inputFile != null ? inputFile.getName() : "null"));
        }
        return Files.readAllBytes(inputFile.toPath());
    }

    protected void writeBytes(File outputFile, byte[] data) throws IOException {
        if (outputFile == null) {
            throw new IOException("Output destination is null.");
        }
        // Ensure directory exists
        if (outputFile.getParentFile() != null) {
            outputFile.getParentFile().mkdirs();
        }
        Files.write(outputFile.toPath(), data);
    }
}