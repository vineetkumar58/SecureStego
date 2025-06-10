package main.ui;

import java.awt.*;
import javax.swing.*;

public class AppUI extends AbstractUI {
    private JButton imageButton, audioButton, videoButton;
    private JButton encryptHideButton, extractDecryptButton;

    public AppUI() {
        super(); // Ensure frame is initialized
    }

    @Override
    protected void initializeComponents() {
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

        JLabel stegoHeading = new JLabel("Steganography");
        stegoHeading.setFont(new Font("Arial", Font.BOLD, 24));
        stegoHeading.setForeground(Color.WHITE);
        gbc.gridy = 3;
        frame.add(stegoHeading, gbc);

        encryptHideButton = new JButton("Encrypt & Hide in Image");
        extractDecryptButton = new JButton("Extract & Decrypt from Image");
        styleButton(encryptHideButton, new Color(120, 180, 120));
        styleButton(extractDecryptButton, new Color(180, 120, 120));

        gbc.gridy = 4;
        frame.add(encryptHideButton, gbc);
        gbc.gridy = 5;
        frame.add(extractDecryptButton, gbc);

        imageButton = new JButton("Image");
        audioButton = new JButton("Audio");
        videoButton = new JButton("Video");

        styleButton(imageButton, new Color(100, 200, 100));
        styleButton(audioButton, new Color(200, 200, 50));
        styleButton(videoButton, new Color(200, 100, 250));

        gbc.gridy = 6;
        frame.add(imageButton, gbc);
        gbc.gridy = 7;
        frame.add(audioButton, gbc);
        gbc.gridy = 8;
        frame.add(videoButton, gbc);
    }

    public JButton getEncryptButton() {
        return encryptButton;
    }

    public JButton getDecryptButton() {
        return decryptButton;
    }

    public JButton getImageButton() {
        return imageButton;
    }

    public JButton getAudioButton() {
        return audioButton;
    }

    public JButton getVideoButton() {
        return videoButton;
    }

    public JButton getEncryptHideButton() {
        return encryptHideButton;
    }

    public JButton getExtractDecryptButton() {
        return extractDecryptButton;
    }
}

