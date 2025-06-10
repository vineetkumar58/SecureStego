package main.encryption;

public abstract class AbstractCrypto {
    protected static final String ALGORITHM = "AES";
    protected static final String FILE_PATH = "src/main/input_output/encrypted_message.txt";

    // Abstract method to be implemented by Encryption and Decryption classes
    public abstract String process(String input, String passkey) throws Exception;
}
