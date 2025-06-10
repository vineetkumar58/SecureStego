package main.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class AppUI extends AbstractUI {
    private JButton encryptHideButton, extractDecryptButton;
    private JButton encryptAudioButton, decryptAudioButton;
    private BufferedImage backgroundImage;

    public AppUI() {
        super();
        frame.setTitle("SecureStego");
        loadBackgroundImage();
    }

    private void loadBackgroundImage() {
        try {
            File imgFile = new File("src/main/resources/background.jpg"); // Ensure the path is correct
            backgroundImage = ImageIO.read(imgFile);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to load background image.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void initializeComponents() {
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());
        frame.setContentPane(backgroundPanel);

        // Blur simulation panel
        JPanel overlayPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.SrcOver.derive(0.1f)); // LOWER opacity for subtle blur
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        overlayPanel.setOpaque(false);
        overlayPanel.setPreferredSize(new Dimension(500, 600));
        backgroundPanel.add(overlayPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel title = new JLabel("SecureStego");
        title.setFont(new Font("Arial", Font.BOLD, 50));
        title.setForeground(new Color(0x000));
        overlayPanel.add(title, gbc);


        gbc.gridy++;
        JLabel encryptionHeading = new JLabel("Encryption");
        encryptionHeading.setFont(new Font("Arial", Font.BOLD, 36));
        encryptionHeading.setForeground(Color.WHITE);
        overlayPanel.add(encryptionHeading, gbc);

        encryptButton = new JButton("Encrypt & Save Message");
        decryptButton = new JButton("Upload & Decrypt Message");
        styleButton(encryptButton, new Color(0, 102, 204, 180));
        styleButton(decryptButton, new Color(0, 51, 102, 180));

        encryptButton.setFont(new Font("Arial", Font.PLAIN, 20));
        decryptButton.setFont(new Font("Arial", Font.PLAIN, 20));

        gbc.gridy++;
        overlayPanel.add(encryptButton, gbc);
        gbc.gridy++;
        overlayPanel.add(decryptButton, gbc);

        gbc.gridy++;
        JLabel stegoHeading = new JLabel("Steganography");
        stegoHeading.setFont(new Font("Arial", Font.BOLD, 36));
        stegoHeading.setForeground(Color.WHITE);
        overlayPanel.add(stegoHeading, gbc);

        encryptHideButton = new JButton("Encrypt & Hide in Image");
        extractDecryptButton = new JButton("Extract & Decrypt from Image");
        styleButton(encryptHideButton, new Color(0, 128, 255, 180));
        styleButton(extractDecryptButton, new Color(0, 77, 179, 180));

        encryptHideButton.setFont(new Font("Arial", Font.PLAIN, 20));
        extractDecryptButton.setFont(new Font("Arial", Font.PLAIN, 20));

        gbc.gridy++;
        overlayPanel.add(encryptHideButton, gbc);
        gbc.gridy++;
        overlayPanel.add(extractDecryptButton, gbc);

        encryptAudioButton = new JButton("Encrypt & Hide in Audio");
        decryptAudioButton = new JButton("Extract & Decrypt from Audio");
        styleButton(encryptAudioButton, new Color(0, 153, 255, 180));
        styleButton(decryptAudioButton, new Color(0, 102, 204, 180));

        encryptAudioButton.setFont(new Font("Arial", Font.PLAIN, 20));
        decryptAudioButton.setFont(new Font("Arial", Font.PLAIN, 20));

        gbc.gridy++;
        overlayPanel.add(encryptAudioButton, gbc);
        gbc.gridy++;
        overlayPanel.add(decryptAudioButton, gbc);
    }

    public JButton getEncryptButton() {
        return encryptButton;
    }

    public JButton getDecryptButton() {
        return decryptButton;
    }

    public JButton getEncryptHideButton() {
        return encryptHideButton;
    }

    public JButton getExtractDecryptButton() {
        return extractDecryptButton;
    }

    public JButton getEncryptAudioButton() {
        return encryptAudioButton;
    }

    public JButton getDecryptAudioButton() {
        return decryptAudioButton;
    }

    public JFrame getFrame() {
        return frame;
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(350, 55));
    }
}
