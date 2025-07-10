package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.*; // Added for URI and URL
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

/**
 * The StudentCommunity class manages the student community panel within the Matrob Education Finder.
 * It allows users to add their profiles, view a list of profiles, connect with students
 * based on their current country, and now also provides a section for helpful videos.
 * This version includes data persistence, improved UI for profile display,
 * and functionality to edit and delete profiles, alongside the new video feature.
 */
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

    /**
     * Constructor for StudentCommunity.
     * Initializes the UI components, loads existing student profiles, and sets up event listeners.
     */
    public StudentCommunity() {
        studentProfiles = new ArrayList<>();
        profileListModel = new DefaultListModel<>();

        // Initialize statusLabel here, before any method calls that might use setStatus
        statusLabel = new JLabel("Ready.");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));

        communityPanel = new JPanel(new BorderLayout(10, 10)); // Add some padding
        communityPanel.setBackground(new Color(240, 255, 240));

        // Load profiles from CSV on startup. statusLabel is now initialized.
        loadProfilesFromCSV();

        // --- Top Section: Useful Tips ---
        JPanel tipsPanel = new JPanel(new BorderLayout());
        tipsPanel.setBackground(new Color(240, 255, 240));
        tipsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        JLabel tipsLabel = new JLabel("<html><center><b>Useful Tips for the Community:</b><br>" +
                "- Add your profile to connect with others.<br>" +
                "- Search by country to find students in specific regions.<br>" +
                "- Share experiences and build your global network!</center></html>");
        tipsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tipsLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center text
        tipsPanel.add(tipsLabel, BorderLayout.CENTER);
        communityPanel.add(tipsPanel, BorderLayout.NORTH);

        // --- Main Content Panel (Profiles and Actions) ---
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        mainContentPanel.setBorder(new EmptyBorder(0, 10, 0, 10)); // Horizontal padding

        // Profile Management (Add/Edit) Section
        JPanel addProfilePanel = createAddProfilePanel();
        mainContentPanel.add(addProfilePanel, BorderLayout.NORTH);

        // Profile List Section
        profileList = new JList<>(profileListModel);
        profileList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        profileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only one selection
        profileList.setCellRenderer(new StudentProfileCellRenderer()); // Set custom cell renderer for better display
        JScrollPane profileScrollPane = new JScrollPane(profileList);
        profileScrollPane.setBorder(BorderFactory.createTitledBorder("Student Profiles")); // Titled border for list
        mainContentPanel.add(profileScrollPane, BorderLayout.CENTER);

        // Status Label for user feedback
        mainContentPanel.add(statusLabel, BorderLayout.SOUTH); // Add the status label to the panel

        // --- Bottom Section of Main Content: Connect Students (Search/Filter) and Actions ---
        JPanel bottomActionPanel = new JPanel(new BorderLayout(5, 5));
        bottomActionPanel.add(createConnectStudentsPanel(), BorderLayout.NORTH);
        bottomActionPanel.add(createProfileActionsPanel(), BorderLayout.SOUTH); // Add Edit/Delete buttons
        mainContentPanel.add(bottomActionPanel, BorderLayout.SOUTH);

        // Add the main content panel to the center of the communityPanel
        communityPanel.add(mainContentPanel, BorderLayout.CENTER);

        // NEW: Add the VideoPanel to the east side of the communityPanel
        communityPanel.add(new VideoPanel(), BorderLayout.EAST);

        // Initial update of the profile list after loading
        updateProfileList();
    }

    /**
     * Creates the panel for adding new student profiles.
     * Includes input fields for name, email, origin country, current country, and an "Add Profile" button.
     * @return The JPanel for adding profiles.
     */
    private JPanel createAddProfilePanel() {
        JPanel addProfilePanel = new JPanel(new GridBagLayout());
        addProfilePanel.setBorder(BorderFactory.createTitledBorder("Add/Edit Profile"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Smaller padding for input fields
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fieldFont = new Font("SansSerif", Font.PLAIN, 13);
        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);
        Color buttonBgColor = new Color(255, 105, 180); // Hot pink

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

    /**
     * Creates the panel for connecting with students (search/filter functionality).
     * Includes an input field for desired country and buttons to filter and clear the filter.
     * @return The JPanel for connecting students.
     */
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

    /**
     * Creates the panel containing buttons for editing and deleting student profiles.
     * @return The JPanel for profile actions.
     */
    private JPanel createProfileActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);

        JButton editButton = new JButton("Edit Selected Profile");
        editButton.setFont(buttonFont);
        editButton.setBackground(new Color(255, 165, 0)); // Orange
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> editProfile());
        actionsPanel.add(editButton);

        JButton deleteButton = new JButton("Delete Selected Profile");
        deleteButton.setFont(buttonFont);
        deleteButton.setBackground(new Color(220, 20, 60)); // Crimson
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteProfile());
        actionsPanel.add(deleteButton);

        return actionsPanel;
    }

    /**
     * Handles adding a new student profile or updating an existing one.
     * Performs input validation and updates the profile list and CSV.
     */
    private void addProfile() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String originCountry = originCountryField.getText().trim();
        String currentCountry = currentCountryField.getText().trim();

        // Input validation
        if (name.isEmpty() || email.isEmpty() || originCountry.isEmpty() || currentCountry.isEmpty()) {
            setStatus("<html><font color='red'>Error: Please fill out all profile fields.</font></html>");
            return;
        }
        if (!isValidEmail(email)) {
            setStatus("<html><font color='red'>Error: Please enter a valid email address.</font></html>");
            return;
        }

        // Check if editing an existing profile
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
        saveProfilesToCSV(); // Save changes to CSV
    }

    /**
     * Loads the selected profile from the JList into the input fields for editing.
     */
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

    /**
     * Deletes the selected profile from the JList and updates the list and CSV.
     */
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
            saveProfilesToCSV(); // Save changes to CSV
            clearProfileInputFields();
            setStatus("<html><font color='green'>Profile deleted successfully!</font></html>");
        }
    }

    /**
     * Filters the displayed student profiles in the JList based on the desired country.
     */
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

    /**
     * Clears the current filter and displays all student profiles in the JList.
     */
    private void clearFilter() {
        desiredCountryFilterField.setText("");
        updateProfileList(); // Reload all profiles
        setStatus("<html><font color='green'>Filter cleared. Showing all profiles.</font></html>");
    }

    /**
     * Updates the JList model with the current contents of the studentProfiles list.
     * This method is called after adding, editing, or deleting profiles.
     */
    private void updateProfileList() {
        profileListModel.clear();
        for (StudentProfile profile : studentProfiles) {
            profileListModel.addElement(profile);
        }
    }

    /**
     * Clears the text from all profile input fields.
     */
    private void clearProfileInputFields() {
        nameField.setText("");
        emailField.setText("");
        originCountryField.setText("");
        currentCountryField.setText("");
        profileList.clearSelection();
    }

    /**
     * Sets the text of the status label, providing feedback to the user.
     * @param message The message to display.
     */
    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    /**
     * Validates if a given string is a basic valid email format using regex.
     * @param email The email string to validate.
     * @return true if the email is valid, false otherwise.
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Saves the current list of student profiles to a CSV file.
     * Each profile is written as a row in the CSV.
     */
    private void saveProfilesToCSV() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(PROFILES_CSV_FILE))) {
            // Write header
            writer.writeNext(new String[]{"Name", "Email", "OriginCountry", "CurrentCountry"});
            // Write profile data
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

    /**
     * Loads student profiles from the CSV file into the application.
     * This method is called during initialization.
     */
    private void loadProfilesFromCSV() {
        File file = new File(PROFILES_CSV_FILE);
        if (!file.exists()) {
            System.out.println("Profile CSV file not found. Starting with empty profiles.");
            return; // No file to load from
        }

        try (CSVReader reader = new CSVReader(new FileReader(PROFILES_CSV_FILE))) {
            List<String[]> records = reader.readAll();
            if (records.isEmpty()) return;

            // Skip header row if it exists
            int startRow = 0;
            if (records.get(0).length == 4 && records.get(0)[0].equalsIgnoreCase("Name")) {
                startRow = 1; // Assuming header row
            }

            for (int i = startRow; i < records.size(); i++) {
                String[] record = records.get(i);
                if (record.length >= 4) { // Ensure enough columns
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

    /**
     * Returns the main JPanel for the student community feature.
     * This panel can be added to other Swing containers (e.g., JTabbedPane).
     * @return The JPanel representing the student community interface.
     */
    public JPanel getCommunityPanel() {
        return communityPanel;
    }

    /**
     * Inner static class representing a Student Profile.
     * Encapsulates student details and provides methods for access and string representation.
     */
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

        // Getters
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getOriginCountry() { return originCountry; }
        public String getCurrentCountry() { return currentCountry; }

        // Setters (for editing functionality)
        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }
        public void setCurrentCountry(String currentCountry) { this.currentCountry = currentCountry; }

        @Override
        public String toString() {
            return String.format("Name: %s, Email: %s, Origin: %s, Current: %s", name, email, originCountry, currentCountry);
        }
    }

    /**
     * Custom ListCellRenderer to provide a more visually appealing display
     * for StudentProfile objects in the JList.
     * It renders each profile as a JPanel with multiple labels.
     */
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
            nameLabel.setBackground(getBackground());
            emailLabel.setBackground(getBackground());
            countryLabel.setBackground(getBackground());
            ((JPanel) nameLabel.getParent()).setBackground(getBackground());

            return this;
        }
    }

    /**
     * Inner static class for the Video display panel.
     * Contains a JList to display video titles and opens URLs in the default browser.
     */
    private static class VideoPanel extends JPanel {
        private final JList<Video> videoList;
        private final DefaultListModel<Video> videoListModel;

        public VideoPanel() {
            super(new BorderLayout(10, 10));
            setBackground(new Color(240, 255, 240));
            setBorder(BorderFactory.createTitledBorder("Helpful Videos for International Students"));
            setPreferredSize(new Dimension(300, 400)); // Give it a preferred size for the EAST region

            videoListModel = new DefaultListModel<>();
            videoList = new JList<>(videoListModel);
            videoList.setFont(new Font("SansSerif", Font.PLAIN, 14));
            videoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            videoList.setCellRenderer(new VideoCellRenderer());

            // Listener to open video URL when selected
            videoList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    final Video selectedVideo = videoList.getSelectedValue();
                    if (selectedVideo != null) {
                        try {
                            // Check if Desktop API is supported (important for headless environments)
                            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                Desktop.getDesktop().browse(new URI(selectedVideo.getUrl()));
                            } else {
                                JOptionPane.showMessageDialog(this,
                                        "Desktop browsing is not supported on this system.",
                                        "Feature Not Supported", JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (final IOException | URISyntaxException ex) {
                            JOptionPane.showMessageDialog(this,
                                    "Could not open video link: " + selectedVideo.getUrl() + "\nError: " + ex.getMessage(),
                                    "Error Opening Link", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    }
                }
            });

            add(new JScrollPane(videoList), BorderLayout.CENTER);

            loadExampleVideos(); // Populate with example videos
        }

        /**
         * Populates the video list with hardcoded example videos.
         */
        private void loadExampleVideos() {
            videoListModel.addElement(new Video(
                    "How to move abroad for studies in 2024 (My experience)",
                    "A comprehensive guide to preparing for your move abroad.",
                    "https://youtu.be/_3pfqU6hEkU" // Your provided URL
            ));
            videoListModel.addElement(new Video(
                    "Moving Abroad: How to pick the right country for you",
                    "Tips on choosing the best country for your international studies.",
                    "https://youtu.be/9r059QdBybA"
            ));
            videoListModel.addElement(new Video(
                    "Study Abroad: What to do before you leave?",
                    "Essential steps to take before departing for your study abroad journey.",
                    "https://youtu.be/QlFDaAeQQc0"
            ));
            videoListModel.addElement(new Video(
                    "Study Abroad: How to prepare for your new life!",
                    "Practical advice for adapting to life in a new country.",
                    "https://youtu.be/HFIITY2J9rQ"
            ));
        }
    }

    /**
     * Data model for a Video.
     * Holds the title, description, and URL of a YouTube video.
     */
    private static class Video {
        private final String title;
        private final String description;
        private final String url;

        public Video(final String title, final String description, final String url) {
            this.title = title;
            this.description = description;
            this.url = url;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getUrl() { return url; }

        @Override
        public String toString() {
            return title;
        }
    }

    /**
     * Custom ListCellRenderer for Video objects.
     * Renders each video in the JList with a title and description.
     */
    private static class VideoCellRenderer extends JPanel implements ListCellRenderer<Video> {
        private final JLabel titleLabel;
        private final JLabel descriptionLabel;

        public VideoCellRenderer() {
            super(new BorderLayout(5, 0));
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            titleLabel = new JLabel();
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            titleLabel.setForeground(new Color(0, 100, 0));

            descriptionLabel = new JLabel();
            descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            descriptionLabel.setForeground(Color.DARK_GRAY);

            final JPanel textPanel = new JPanel(new GridLayout(2, 1));
            textPanel.add(titleLabel);
            textPanel.add(descriptionLabel);
            add(textPanel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(final JList<? extends Video> list,
                                                      final Video video,
                                                      final int index,
                                                      final boolean isSelected,
                                                      final boolean cellHasFocus) {
            titleLabel.setText(video.getTitle());
            descriptionLabel.setText(video.getDescription());

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            titleLabel.setBackground(getBackground());
            descriptionLabel.setBackground(getBackground());
            ((JPanel) titleLabel.getParent()).setBackground(getBackground());

            return this;
        }
    }
}
