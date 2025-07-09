package com.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LoginPage {

    private JPanel loginPanel;

    @SuppressWarnings("unused")
    public LoginPage(List<StudentCommunity.StudentProfile> profiles) {
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);

        // Email Label and Text Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        loginPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        loginPanel.add(emailField, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JButton loginButton = new JButton("Log In");
        loginButton.setFont(buttonFont);
        loginButton.setBackground(new Color(30, 144, 255)); // Dodger blue background
        loginButton.setForeground(Color.WHITE);             // White text
        loginButton.setFocusPainted(false);                 // Remove focus border
        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(loginPanel, "Please enter your email.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate email against profiles
            boolean valid = false;
            for (StudentCommunity.StudentProfile profile : profiles) {
                
            }

            if (valid) {
                JOptionPane.showMessageDialog(loginPanel, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Navigate to the community page
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(loginPanel);
                frame.getContentPane().removeAll();
                frame.getContentPane().add(new StudentCommunity().getCommunityPanel());
                frame.revalidate();
                frame.repaint();
            } else {
                JOptionPane.showMessageDialog(loginPanel, "Email not found. Please create a profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        loginPanel.add(loginButton, gbc);

        // Create Profile Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton createProfileButton = new JButton("Create Profile");
        createProfileButton.setFont(buttonFont);
        createProfileButton.setBackground(new Color(255, 105, 180)); // Hot pink background
        createProfileButton.setForeground(Color.WHITE);              // White text
        createProfileButton.setFocusPainted(false);                  // Remove focus border
        createProfileButton.addActionListener(e -> {
            // Navigate to the profile creation page
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(loginPanel);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new StudentCommunity().getCommunityPanel());
            frame.revalidate();
            frame.repaint();
        });
        loginPanel.add(createProfileButton, gbc);
    }

    public JPanel getLoginPanel() {
        return loginPanel;
    }
}