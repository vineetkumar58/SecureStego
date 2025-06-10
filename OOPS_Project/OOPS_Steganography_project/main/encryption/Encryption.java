package main.encryption;

import java.util.Base64;

public class Encryption {
    public String process(String message, String passkey) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char ch = (char) (message.charAt(i) ^ passkey.charAt(i % passkey.length()));
            result.append(ch);
        }
        return Base64.getEncoder().encodeToString(result.toString().getBytes());
    }
}


