package src.main.ui;

import java.awt.*;
import javax.swing.*;

public abstract class AbstractUI {
    protected JFrame frame;
    protected JButton encryptButton, decryptButton;

    public AbstractUI() {
        frame = new JFrame("Steganography Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(30, 30, 30));
        initializeComponents();
        frame.setVisible(true);
    }

    protected abstract void initializeComponents();

    private void styleButton(JButton button, Color color) {  // Changed to private
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
