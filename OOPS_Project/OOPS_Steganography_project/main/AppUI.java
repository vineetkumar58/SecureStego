package main;

import javax.swing.*;
import java.awt.*;

public class AppUI {
    private JFrame frame;
    private JButton encryptButton, decryptButton, imageButton, audioButton, videoButton;

    public AppUI() {
        frame = new JFrame("Steganography Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel encryptionHeading = new JLabel("Encryption");
        encryptionHeading.setFont(new Font("Arial", Font.BOLD, 24));
        encryptionHeading.setForeground(Color.WHITE);
        frame.add(encryptionHeading, gbc);

        encryptButton = new JButton("Encrypt & Save Message");
        decryptButton = new JButton("Upload & Decrypt Message");
        styleButton(encryptButton, new Color(50, 150, 250));
        styleButton(decryptButton, new Color(250, 100, 100));

        gbc.gridy = 1;
        frame.add(encryptButton, gbc);
        gbc.gridy = 2;
        frame.add(decryptButton, gbc);

        gbc.gridy = 3;
        JLabel stegoHeading = new JLabel("Steganography");
        stegoHeading.setFont(new Font("Arial", Font.BOLD, 24));
        stegoHeading.setForeground(Color.WHITE);
        frame.add(stegoHeading, gbc);

        imageButton = new JButton("Image");
        audioButton = new JButton("Audio");
        videoButton = new JButton("Video");
        styleButton(imageButton, new Color(100, 200, 100));
        styleButton(audioButton, new Color(200, 200, 50));
        styleButton(videoButton, new Color(200, 100, 250));

        gbc.gridy = 4;
        frame.add(imageButton, gbc);
        gbc.gridy = 5;
        frame.add(audioButton, gbc);
        gbc.gridy = 6;
        frame.add(videoButton, gbc);

        frame.setVisible(true);
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    public JButton getEncryptButton() {
        return encryptButton;
    }

    public JButton getDecryptButton() {
        return decryptButton;
    }

    public JFrame getFrame() {
        return frame;
    }
}