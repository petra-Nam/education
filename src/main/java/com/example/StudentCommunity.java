package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

public class StudentCommunity {

    private static final String PROFILES_CSV_FILE = "student_profiles.csv";

    private JPanel communityPanel;
    private List<StudentProfile> studentProfiles;
    private JList<StudentProfile> profileList;
    private DefaultListModel<StudentProfile> profileListModel;
    private JLabel statusLabel;

    // Input fields for adding/editing profiles
    private JTextField nameField;
    private JTextField emailField;
    private JTextField originCountryField;
    private JTextField currentCountryField;
    private JTextField desiredCountryFilterField; // Field for filtering students

    public StudentCommunity() {
        studentProfiles = new ArrayList<>();
        profileListModel = new DefaultListModel<>();
        communityPanel = new JPanel(new BorderLayout(10, 10));
        communityPanel.setBackground(new Color(240, 255, 240));

        loadProfilesFromCSV(); // Load profiles on startup

        // --- Top Section: Useful Tips ---
        JPanel tipsPanel = new JPanel(new BorderLayout());
        tipsPanel.setBackground(new Color(240, 255, 240));
        tipsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel tipsLabel = new JLabel("<html><center><b>Useful Tips for the Community:</b><br>" +
                "- Add your profile to connect with others.<br>" +
                "- Search by country to find students in specific regions.<br>" +
                "- Share experiences and build your global network!</center></html>");
        tipsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tipsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tipsPanel.add(tipsLabel, BorderLayout.CENTER);
        communityPanel.add(tipsPanel, BorderLayout.NORTH);

        // --- Center Section: Profile Management and List Display ---
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(new EmptyBorder(0, 10, 0, 10));

        JPanel addProfilePanel = createAddProfilePanel();
        centerPanel.add(addProfilePanel, BorderLayout.NORTH);

        profileList = new JList<>(profileListModel);
        profileList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        profileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        profileList.setCellRenderer(new StudentProfileCellRenderer()); // Custom renderer for better display
        JScrollPane profileScrollPane = new JScrollPane(profileList);
        profileScrollPane.setBorder(BorderFactory.createTitledBorder("Student Profiles"));
        centerPanel.add(profileScrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel("Ready.");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        centerPanel.add(statusLabel, BorderLayout.SOUTH);

        communityPanel.add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Section: Connect Students (Search/Filter) and Actions ---
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(createConnectStudentsPanel(), BorderLayout.NORTH);
        bottomPanel.add(createProfileActionsPanel(), BorderLayout.SOUTH);
        communityPanel.add(bottomPanel, BorderLayout.SOUTH);

        updateProfileList(); // Initial update after loading profiles
    }

    private JPanel createAddProfilePanel() {
        JPanel addProfilePanel = new JPanel(new GridBagLayout());
        addProfilePanel.setBorder(BorderFactory.createTitledBorder("Add/Edit Profile"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Removed unused variable to fix the compile error
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 13);
        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);
        Color buttonBgColor = new Color(255, 105, 180);

        // Name
        gbc.gridx = 0; gbc.gridy = 0; addProfilePanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; nameField = new JTextField(20); nameField.setFont(fieldFont); addProfilePanel.add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1; addProfilePanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; emailField = new JTextField(20); emailField.setFont(fieldFont); addProfilePanel.add(emailField, gbc);

        // Country of Origin
        gbc.gridx = 0; gbc.gridy = 2; addProfilePanel.add(new JLabel("Country of Origin:"), gbc);
        gbc.gridx = 1; originCountryField = new JTextField(20); originCountryField.setFont(fieldFont); addProfilePanel.add(originCountryField, gbc);

        // Current Country
        gbc.gridx = 0; gbc.gridy = 3; addProfilePanel.add(new JLabel("Current Country:"), gbc);
        gbc.gridx = 1; currentCountryField = new JTextField(20); currentCountryField.setFont(fieldFont); addProfilePanel.add(currentCountryField, gbc);

        // Add Profile Button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton addProfileButton = new JButton("Add New Profile");
        addProfileButton.setFont(buttonFont);
        addProfileButton.setBackground(buttonBgColor);
        addProfileButton.setForeground(Color.WHITE);
        addProfileButton.setFocusPainted(false);
        addProfileButton.addActionListener(e -> addProfile());
        addProfilePanel.add(addProfileButton, gbc);

        return addProfilePanel;
    }

    private JPanel createConnectStudentsPanel() {
        JPanel connectStudentsPanel = new JPanel(new GridBagLayout());
        connectStudentsPanel.setBorder(BorderFactory.createTitledBorder("Find Students by Country"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);
        Color connectButtonBgColor = new Color(30, 144, 255);

        // Desired Country input
        gbc.gridx = 0; gbc.gridy = 0; connectStudentsPanel.add(new JLabel("Filter by Country:"), gbc);
        gbc.gridx = 1; desiredCountryFilterField = new JTextField(20); connectStudentsPanel.add(desiredCountryFilterField, gbc);

        // Filter Button
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        JButton filterButton = new JButton("Filter Students");
        filterButton.setFont(buttonFont);
        filterButton.setBackground(connectButtonBgColor);
        filterButton.setForeground(Color.WHITE);
        filterButton.setFocusPainted(false);
        filterButton.addActionListener(e -> filterProfiles());
        connectStudentsPanel.add(filterButton, gbc);

        // Clear Filter Button
        gbc.gridx = 1; gbc.gridy = 1;
        JButton clearFilterButton = new JButton("Clear Filter");
        clearFilterButton.setFont(buttonFont);
        clearFilterButton.setBackground(new Color(100, 149, 237));
        clearFilterButton.setForeground(Color.WHITE);
        clearFilterButton.setFocusPainted(false);
        clearFilterButton.addActionListener(e -> clearFilter());
        connectStudentsPanel.add(clearFilterButton, gbc);

        return connectStudentsPanel;
    }

    private JPanel createProfileActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);

        JButton editButton = new JButton("Edit Selected Profile");
        editButton.setFont(buttonFont);
        editButton.setBackground(new Color(255, 165, 0));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> editProfile());
        actionsPanel.add(editButton);

        JButton deleteButton = new JButton("Delete Selected Profile");
        deleteButton.setFont(buttonFont);
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteProfile());
        actionsPanel.add(deleteButton);

        return actionsPanel;
    }

    private void addProfile() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String originCountry = originCountryField.getText().trim();
        String currentCountry = currentCountryField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || originCountry.isEmpty() || currentCountry.isEmpty()) {
            setStatus("<html><font color='red'>Error: Please fill out all profile fields.</font></html>");
            return;
        }
        if (!isValidEmail(email)) {
            setStatus("<html><font color='red'>Error: Please enter a valid email address.</font></html>");
            return;
        }

        int selectedIndex = profileList.getSelectedIndex();
        if (selectedIndex != -1 && selectedIndex < studentProfiles.size()) {
            // Update existing profile
            StudentProfile existingProfile = studentProfiles.get(selectedIndex);
            existingProfile.setName(name);
            existingProfile.setEmail(email);
            existingProfile.setOriginCountry(originCountry);
            existingProfile.setCurrentCountry(currentCountry);
            setStatus("<html><font color='green'>Profile updated successfully!</font></html>");
        } else {
            // Add new profile
            for (StudentProfile p : studentProfiles) {
                if (p.getEmail().equalsIgnoreCase(email)) {
                    setStatus("<html><font color='red'>Error: A profile with this email already exists.</font></html>");
                    return;
                }
            }
            studentProfiles.add(new StudentProfile(name, email, originCountry, currentCountry));
            setStatus("<html><font color='green'>New profile added!</font></html>");
        }

        clearProfileInputFields();
        updateProfileList();
        saveProfilesToCSV();
    }

    private void editProfile() {
        int selectedIndex = profileList.getSelectedIndex();
        if (selectedIndex == -1 || selectedIndex >= studentProfiles.size()) {
            setStatus("<html><font color='red'>Please select a profile to edit.</font></html>");
            return;
        }

        StudentProfile selectedProfile = studentProfiles.get(selectedIndex);
        nameField.setText(selectedProfile.getName());
        emailField.setText(selectedProfile.getEmail());
        originCountryField.setText(selectedProfile.getOriginCountry());
        currentCountryField.setText(selectedProfile.getCurrentCountry());
        setStatus("<html><font color='blue'>Profile loaded for editing. Make changes and click 'Add New Profile' to save.</font></html>");
    }

    private void deleteProfile() {
        int selectedIndex = profileList.getSelectedIndex();
        if (selectedIndex == -1 || selectedIndex >= studentProfiles.size()) {
            setStatus("<html><font color='red'>Please select a profile to delete.</font></html>");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(communityPanel,
                "Are you sure you want to delete this profile?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            studentProfiles.remove(selectedIndex);
            updateProfileList();
            saveProfilesToCSV();
            clearProfileInputFields();
            setStatus("<html><font color='green'>Profile deleted successfully!</font></html>");
        }
    }

    private void filterProfiles() {
        String desiredCountry = desiredCountryFilterField.getText().trim();
        if (desiredCountry.isEmpty()) {
            setStatus("<html><font color='red'>Please enter a country to filter by.</font></html>");
            return;
        }

        profileListModel.clear();
        boolean found = false;
        for (StudentProfile profile : studentProfiles) {
            if (profile.getCurrentCountry().equalsIgnoreCase(desiredCountry)) {
                profileListModel.addElement(profile);
                found = true;
            }
        }

        if (!found) {
            setStatus("<html><font color='blue'>No students found in " + desiredCountry + ".</font></html>");
        } else {
            setStatus("<html><font color='green'>Showing students in " + desiredCountry + ".</font></html>");
        }
    }

    private void clearFilter() {
        desiredCountryFilterField.setText("");
        updateProfileList();
        setStatus("<html><font color='green'>Filter cleared. Showing all profiles.</font></html>");
    }

    private void updateProfileList() {
        profileListModel.clear();
        for (StudentProfile profile : studentProfiles) {
            profileListModel.addElement(profile);
        }
    }

    private void clearProfileInputFields() {
        nameField.setText("");
        emailField.setText("");
        originCountryField.setText("");
        currentCountryField.setText("");
        profileList.clearSelection();
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void saveProfilesToCSV() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(PROFILES_CSV_FILE))) {
            writer.writeNext(new String[]{"Name", "Email", "OriginCountry", "CurrentCountry"});
            for (StudentProfile profile : studentProfiles) {
                writer.writeNext(new String[]{
                        profile.getName(),
                        profile.getEmail(),
                        profile.getOriginCountry(),
                        profile.getCurrentCountry()
                });
            }
            setStatus("<html><font color='green'>Profiles saved successfully!</font></html>");
        } catch (IOException e) {
            System.err.println("Error saving profiles to CSV: " + e.getMessage());
            setStatus("<html><font color='red'>Error saving profiles: " + e.getMessage() + "</font></html>");
        }
    }

    private void loadProfilesFromCSV() {
        File file = new File(PROFILES_CSV_FILE);
        if (!file.exists()) {
            System.out.println("Profile CSV file not found. Starting with empty profiles.");
            return;
        }

        try (CSVReader reader = new CSVReader(new FileReader(PROFILES_CSV_FILE))) {
            List<String[]> records = reader.readAll();
            if (records.isEmpty()) return;

            // Skip header row if it exists
            int startRow = 0;
            if (records.get(0).length == 4 && records.get(0)[0].equalsIgnoreCase("Name")) {
                startRow = 1;
            }

            for (int i = startRow; i < records.size(); i++) {
                String[] record = records.get(i);
                if (record.length >= 4) {
                    studentProfiles.add(new StudentProfile(record[0], record[1], record[2], record[3]));
                } else {
                    System.err.println("Skipping malformed record in CSV: " + String.join(",", record));
                }
            }
            setStatus("<html><font color='green'>Profiles loaded successfully!</font></html>");
        } catch (IOException | CsvException e) {
            System.err.println("Error loading profiles from CSV: " + e.getMessage());
            setStatus("<html><font color='red'>Error loading profiles: " + e.getMessage() + "</font></html>");
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

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getOriginCountry() { return originCountry; }
        public String getCurrentCountry() { return currentCountry; }

        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }
        public void setCurrentCountry(String currentCountry) { this.currentCountry = currentCountry; }

        @Override
        public String toString() {
            return String.format("Name: %s, Email: %s, Origin: %s, Current: %s", name, email, originCountry, currentCountry);
        }
    }

    private static class StudentProfileCellRenderer extends JPanel implements ListCellRenderer<StudentProfile> {
        private final JLabel nameLabel;
        private final JLabel emailLabel;
        private final JLabel countryLabel;

        public StudentProfileCellRenderer() {
            setLayout(new BorderLayout(5, 0));
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            nameLabel = new JLabel();
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            nameLabel.setForeground(new Color(0, 100, 0));

            emailLabel = new JLabel();
            emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            emailLabel.setForeground(Color.DARK_GRAY);

            countryLabel = new JLabel();
            countryLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
            countryLabel.setForeground(new Color(100, 100, 100));

            JPanel textPanel = new JPanel(new GridLayout(3, 1));
            textPanel.add(nameLabel);
            textPanel.add(emailLabel);
            textPanel.add(countryLabel);
            add(textPanel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends StudentProfile> list,
                                                      StudentProfile profile,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            nameLabel.setText(profile.getName());
            emailLabel.setText("Email: " + profile.getEmail());
            countryLabel.setText("From: " + profile.getOriginCountry() + " | Currently in: " + profile.getCurrentCountry());

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            // Ensure child components also inherit the background
            nameLabel.setBackground(getBackground());
            emailLabel.setBackground(getBackground());
            countryLabel.setBackground(getBackground());
            ((JPanel) nameLabel.getParent()).setBackground(getBackground());

            return this;
        }
    }
}
