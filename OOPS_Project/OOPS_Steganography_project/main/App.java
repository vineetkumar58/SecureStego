package main;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import main.encryption.Decryption;
import main.encryption.Encryption;
import main.steganography.Embedder;
import main.steganography.Extractor;
import main.ui.AppUI;

public class App {

    private static boolean isValidBase64(String str) {
        try {
            java.util.Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @SuppressWarnings({"CallToPrintStackTrace", "ConvertToTryWithResources", "UseSpecificCatch"})
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppUI appUI = new AppUI();

            // 🔐 Encrypt & Save to File
            appUI.getEncryptButton().addActionListener(e -> {
                JTextField messageField = new JTextField();
                JTextField passkeyField = new JTextField();

                Object[] inputs = {
                    "Enter message:", messageField,
                    "Enter passkey:", passkeyField
                };

                int result = JOptionPane.showConfirmDialog(appUI.getFrame(), inputs, "Encrypt Message", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    String message = messageField.getText();
                    String passkey = passkeyField.getText();

                    if (!message.isEmpty() && !passkey.isEmpty()) {
                        try {
                            Encryption encryption = new Encryption();
                            String encrypted = encryption.process(message, passkey);

                            File file = new File("src/main/input_output/encrypted_message.txt");
                            file.getParentFile().mkdirs();
                            FileWriter writer = new FileWriter(file);
                            writer.write(encrypted);
                            writer.close();

                            JOptionPane.showMessageDialog(appUI.getFrame(), "Encrypted message saved successfully.");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(appUI.getFrame(), "Encryption failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });

            // 🔓 Decrypt from File
            appUI.getDecryptButton().addActionListener(e -> {
                JTextField passkeyField = new JTextField();

                Object[] inputs = {
                    "Enter passkey:", passkeyField
                };

                int result = JOptionPane.showConfirmDialog(appUI.getFrame(), inputs, "Decrypt Message", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    String passkey = passkeyField.getText();

                    if (!passkey.isEmpty()) {
                        try {
                            JFileChooser chooser = new JFileChooser("src/main/input_output/");
                            int returnVal = chooser.showOpenDialog(null);

                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                File file = chooser.getSelectedFile();
                                BufferedReader reader = new BufferedReader(new FileReader(file));
                                StringBuilder sb = new StringBuilder();
                                String line;

                                while ((line = reader.readLine()) != null) {
                                    sb.append(line);
                                }

                                reader.close();

                                String encrypted = sb.toString();
                                Decryption decryption = new Decryption();
                                String decrypted = decryption.process(passkey, encrypted);

                                JOptionPane.showMessageDialog(appUI.getFrame(), "Decrypted Message:\n" + decrypted);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(appUI.getFrame(), "Decryption failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });

            // 🖼️ Encrypt & Hide in Image
            appUI.getEncryptHideButton().addActionListener(e -> {
                JTextField messageField = new JTextField();
                JTextField passkeyField = new JTextField();

                Object[] inputs = {
                    "Enter message:", messageField,
                    "Enter passkey:", passkeyField
                };

                int result = JOptionPane.showConfirmDialog(appUI.getFrame(), inputs, "Encrypt & Hide", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    String message = messageField.getText();
                    String passkey = passkeyField.getText();

                    if (!message.isEmpty() && !passkey.isEmpty()) {
                        try {
                            Encryption encryption = new Encryption();
                            String encryptedMessage = encryption.process(message, passkey);

                            JFileChooser chooser = new JFileChooser();
                            chooser.setDialogTitle("Choose Image to Hide Message");
                            chooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg"));
                            int returnVal = chooser.showOpenDialog(null);

                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                File imageFile = chooser.getSelectedFile();
                                BufferedImage image = ImageIO.read(imageFile);

                                if (image == null) {
                                    throw new IllegalArgumentException("The selected file is not a supported image type.");
                                }

                                Embedder embedder = new Embedder();
                                BufferedImage outputImage = embedder.embedMessage(image, encryptedMessage);

                                File output = new File("src/main/input_output/stego_image.png");
                                output.getParentFile().mkdirs();
                                ImageIO.write(outputImage, "png", output);

                                JOptionPane.showMessageDialog(appUI.getFrame(), "Message successfully hidden in image.");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(appUI.getFrame(), "Failed to hide message in image.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });

            // 🔍 Extract & Decrypt from Image
            appUI.getExtractDecryptButton().addActionListener(e -> {
                JTextField passkeyField = new JTextField();

                Object[] inputs = {
                    "Enter passkey:", passkeyField
                };

                int result = JOptionPane.showConfirmDialog(appUI.getFrame(), inputs, "Extract & Decrypt", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    String passkey = passkeyField.getText();

                    if (!passkey.isEmpty()) {
                        try {
                            JFileChooser chooser = new JFileChooser();
                            chooser.setDialogTitle("Choose Image with Hidden Message");
                            chooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg"));
                            int returnVal = chooser.showOpenDialog(null);

                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                File imageFile = chooser.getSelectedFile();
                                BufferedImage image = ImageIO.read(imageFile);

                                if (image == null) {
                                    throw new IllegalArgumentException("The selected file is not a supported image.");
                                }

                                Extractor extractor = new Extractor();
                                String extractedMessage = extractor.extractMessage(image);
                                System.out.println("Extracted message: " + extractedMessage);

                                if (!isValidBase64(extractedMessage)) {
                                    JOptionPane.showMessageDialog(appUI.getFrame(),
                                        "The extracted message is not valid Base64. Please try with another image.",
                                        "Invalid Hidden Data",
                                        JOptionPane.WARNING_MESSAGE);
                                    return;
                                }

                                Decryption decryption = new Decryption();
                                String decrypted = decryption.process(passkey, extractedMessage);

                                JOptionPane.showMessageDialog(appUI.getFrame(), "Decrypted Message:\n" + decrypted);
                            }
                        } catch (IllegalArgumentException ex) {
                            JOptionPane.showMessageDialog(appUI.getFrame(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(appUI.getFrame(), "Failed to extract or decrypt message.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
        });
    }
}

