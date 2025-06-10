package main.encryption;

import java.util.Base64;

public class Decryption {
    public String process(String passkey, String encryptedMessage) {
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
        String xorEncrypted = new String(decodedBytes);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < xorEncrypted.length(); i++) {
            char ch = (char) (xorEncrypted.charAt(i) ^ passkey.charAt(i % passkey.length()));
            result.append(ch);
        }
        return result.toString();
    }
}


