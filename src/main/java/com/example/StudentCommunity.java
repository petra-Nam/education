package com.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StudentCommunity {

    private JPanel communityPanel;
    private List<StudentProfile> studentProfiles;
    private JList<String> profileList;
    private DefaultListModel<String> profileListModel = new DefaultListModel<>();

    public StudentCommunity() {
        studentProfiles = new ArrayList<>();
        communityPanel = new JPanel(new BorderLayout());
        communityPanel.setBackground(new Color(240, 255, 240)); // Light green background

        // Add a useful tips section at the top of the community page
        JPanel tipsPanel = new JPanel(new BorderLayout());
        tipsPanel.setBackground(new Color(240, 255, 240));
        JLabel tipsLabel = new JLabel("<html><b>Useful Tips:</b><br>- Connect with students in your desired country.<br>- Share experiences and advice.<br>- Build a global network.</html>");
        tipsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tipsPanel.add(tipsLabel, BorderLayout.CENTER);

        communityPanel.add(tipsPanel, BorderLayout.NORTH);

        // Center panel for adding profiles and displaying the profile list
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Profile Management Section
        JPanel addProfilePanel = createAddProfilePanel();
        centerPanel.add(addProfilePanel, BorderLayout.NORTH);

        // Profile List Section
        profileList = new JList<>(profileListModel);
        profileList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane profileScrollPane = new JScrollPane(profileList);
        centerPanel.add(profileScrollPane, BorderLayout.CENTER);

        communityPanel.add(centerPanel, BorderLayout.CENTER);

        // Connect Students Section
        JPanel connectStudentsPanel = createConnectStudentsPanel();
        communityPanel.add(connectStudentsPanel, BorderLayout.SOUTH);
    }

    private JPanel createAddProfilePanel() {
        JPanel addProfilePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);

        // Name Label and Text Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(labelFont);
        addProfilePanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        addProfilePanel.add(nameField, gbc);

        // Email Label and Text Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        addProfilePanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        addProfilePanel.add(emailField, gbc);

        // Country of Origin Label and Text Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel originCountryLabel = new JLabel("Country of Origin:");
        originCountryLabel.setFont(labelFont);
        addProfilePanel.add(originCountryLabel, gbc);

        gbc.gridx = 1;
        JTextField originCountryField = new JTextField(20);
        originCountryField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        addProfilePanel.add(originCountryField, gbc);

        // Current Country Label and Text Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel currentCountryLabel = new JLabel("Current Country:");
        currentCountryLabel.setFont(labelFont);
        addProfilePanel.add(currentCountryLabel, gbc);

        gbc.gridx = 1;
        JTextField currentCountryField = new JTextField(20);
        currentCountryField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        addProfilePanel.add(currentCountryField, gbc);

        // Add Profile Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton addProfileButton = new JButton("Add Profile");
        addProfileButton.setFont(buttonFont);
        addProfileButton.setBackground(new Color(255, 105, 180)); // Hot pink background
        addProfileButton.setForeground(Color.WHITE);              // White text
        addProfileButton.setFocusPainted(false);
        addProfileButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String originCountry = originCountryField.getText().trim();
            String currentCountry = currentCountryField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || originCountry.isEmpty() || currentCountry.isEmpty()) {
                JOptionPane.showMessageDialog(communityPanel, "Please fill out all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            studentProfiles.add(new StudentProfile(name, email, originCountry, currentCountry));
            updateProfileList();
            nameField.setText("");
            emailField.setText("");
            originCountryField.setText("");
            currentCountryField.setText("");
        });
        addProfilePanel.add(addProfileButton, gbc);

        return addProfilePanel;
    }

    private JPanel createConnectStudentsPanel() {
        JPanel connectStudentsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);

        // Desired Country Label and Text Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel desiredCountryLabel = new JLabel("Enter Desired Country:");
        desiredCountryLabel.setFont(labelFont);
        connectStudentsPanel.add(desiredCountryLabel, gbc);

        gbc.gridx = 1;
        JTextField desiredCountryField = new JTextField(20);
        desiredCountryField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        connectStudentsPanel.add(desiredCountryField, gbc);

        // Connect Button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JButton connectButton = new JButton("Connect with Students");
        connectButton.setFont(buttonFont);
        connectButton.setBackground(new Color(30, 144, 255)); // Dodger blue background
        connectButton.setForeground(Color.WHITE);             // White text
        connectButton.setFocusPainted(false);
        connectButton.addActionListener(e -> {
            String desiredCountry = desiredCountryField.getText().trim();
            if (desiredCountry.isEmpty()) {
                JOptionPane.showMessageDialog(communityPanel, "Please enter a desired country.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<StudentProfile> matchingProfiles = new ArrayList<>();
            for (StudentProfile profile : studentProfiles) {
                if (profile.getCurrentCountry().equalsIgnoreCase(desiredCountry)) {
                    matchingProfiles.add(profile);
                }
            }

            if (matchingProfiles.isEmpty()) {
                JOptionPane.showMessageDialog(communityPanel, "No students found in the specified desired country.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder results = new StringBuilder("<html>Students in " + desiredCountry + ":<br>");
                for (StudentProfile profile : matchingProfiles) {
                    results.append(profile.toString()).append("<br>");
                }
                results.append("</html>");
                JOptionPane.showMessageDialog(communityPanel, results.toString(), "Matching Students", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        connectStudentsPanel.add(connectButton, gbc);

        return connectStudentsPanel;
    }

    private void updateProfileList() {
        profileListModel.clear();
        for (StudentProfile profile : studentProfiles) {
            profileListModel.addElement(profile.toString());
        }
    }

    public JPanel getCommunityPanel() {
        return communityPanel;
    }

    public static class StudentProfile {
        private String name;
        private String email;
        private String originCountry;
        private String currentCountry;

        public StudentProfile(String name, String email, String originCountry, String currentCountry) {
            this.name = name;
            this.email = email;
            this.originCountry = originCountry;
            this.currentCountry = currentCountry;
        }

        public String getCurrentCountry() {
            return currentCountry;
        }

        @Override
        public String toString() {
            return String.format("Name: %s, Email: %s, Origin Country: %s, Current Country: %s", name, email, originCountry, currentCountry);
        }
    }
}
