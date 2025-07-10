package com.example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * MainClient class represents the primary window for the Matrob Education Finder application.
 * It sets up the main GUI, organizes different search functionalities into tabs,
 * and manages the display of results. It orchestrates interactions between UI components
 * and dedicated service classes for data handling.
 */
public class MainClient extends JFrame {

    // Component to display search results, capable of rendering HTML
    private final JEditorPane resultPane;
    // Scroll pane to allow scrolling of the resultPane if content is long
    private final JScrollPane resultScrollPane;

    // Service layer instances to handle business logic and data operations
    private final ScholarshipService scholarshipService;
    private final UniversityService universityService;
    private final UdemyCourseService udemyCourseService;

    /**
     * Constructor for the MainClient.
     * Initializes the main application window, sets up the UI theme, loads resources,
     * and arranges the various functional panels.
     */
    public MainClient() {
        // Apply a modern look and feel to the application
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (final UnsupportedLookAndFeelException e) {
            e.printStackTrace(); // Log error if L&F cannot be set
        }

        // Set basic JFrame properties
        setTitle("Matrob Education Finder");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close application on window close
        setLocationRelativeTo(null); // Center the window on screen

        // Attempt to set the application icon
        try {
            setIconImage(new ImageIcon(getClass().getResource("/matrob.png")).getImage());
        } catch (final Exception e) {
            System.err.println("Error loading application icon: " + e.getMessage());
        }

        // Initialize service classes responsible for data operations
        scholarshipService = new ScholarshipService();
        universityService = new UniversityService();
        udemyCourseService = new UdemyCourseService();

        // Setup the result display area at the bottom of the main window
        resultPane = new JEditorPane();
        resultPane.setContentType("text/html"); // Enable HTML rendering
        resultPane.setEditable(false); // Make it read-only
        resultPane.setText("<html><center><h2>Welcome to Matrob Education Finder</h2></center></html>"); // Initial welcome message
        resultScrollPane = new JScrollPane(resultPane); // Add scrollability
        resultScrollPane.setPreferredSize(new Dimension(780, 200)); // Set its preferred size

        // Create a tabbed interface to organize different features
        final JTabbedPane tabbedPane = new JTabbedPane();

        // Add each functional panel to a new tab, passing an ActionListener to handle user interactions
        tabbedPane.addTab("Scholarships", new ScholarshipPanel(new ScholarshipActionListener()));
        tabbedPane.addTab("Universities", new UniversityPanel(new UniversityActionListener()));
        tabbedPane.addTab("Udemy Courses", new UdemyCoursesPanel(new UdemyCourseActionListener()));

        // Add the community panel (assuming it's implemented in StudentCommunity class)
        final StudentCommunity studentCommunity = new StudentCommunity();
        tabbedPane.addTab("Community", studentCommunity.getCommunityPanel());

        // Set the main window's layout and add the tabbed pane and result display
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(resultScrollPane, BorderLayout.SOUTH);
    }

    /**
     * Handles actions triggered by buttons in the Scholarship panel.
     * It performs search, sort, and save operations for scholarships,
     * executing long-running tasks in a background thread to maintain UI responsiveness.
     */
    private class ScholarshipActionListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            final String command = e.getActionCommand(); // Get the text of the button clicked
            // Retrieve input values from the ScholarshipPanel
            final ScholarshipPanel sourcePanel = (ScholarshipPanel) ((JButton) e.getSource()).getParent();
            final String degree = sourcePanel.getDegreeInput();
            final String location = sourcePanel.getLocationInput();

            // Validate user input
            if (degree.isEmpty()) {
                updateResultPane("<html><font color='red'>Please enter a degree.</font></html>");
                return;
            }
            if (location.isEmpty()) {
                updateResultPane("<html><font color='red'>Please enter a location.</font></html>");
                return;
            }

            // Display a loading message immediately
            updateResultPane("<html><center>Loading scholarships...</center></html>");

            // Use SwingWorker to run data operations in a separate thread
            new SwingWorker<List<Scholarship>, Void>() {
                @Override
                protected List<Scholarship> doInBackground() throws Exception {
                    // This code runs off the Event Dispatch Thread (EDT)
                    List<Scholarship> allScholarships = scholarshipService.readScholarshipsFromCSV();
                    return scholarshipService.filterScholarships(allScholarships, degree, location);
                }

                @Override
                protected void done() {
                    // This code runs back on the EDT after doInBackground completes
                    try {
                        List<Scholarship> filteredScholarships = get(); // Retrieve result from background task

                        // Perform action based on which button was clicked
                        if ("Search Scholarships".equals(command)) {
                            displayScholarships(filteredScholarships, "No scholarships found for the selected degree and location.");
                        } else if ("Sort by Amount (High to Low)".equals(command)) {
                            scholarshipService.sortScholarshipsByAmount(filteredScholarships); // Sort the filtered list
                            displayScholarships(filteredScholarships, "No scholarships found to sort for the selected criteria.");
                        } else if ("Save as Favorite".equals(command)) {
                            saveCurrentResultsToFile(); // Save the currently displayed results to a file
                        }
                    } catch (final Exception ex) {
                        // Display any errors that occurred during the background operation
                        updateResultPane("<html><font color='red'>An error occurred: " + ex.getMessage() + "</font></html>");
                        ex.printStackTrace();
                    }
                }
            }.execute(); // Start the SwingWorker
        }
    }

    /**
     * Handles actions triggered by buttons in the University panel.
     * It performs university searches by country, using a network API call
     * executed in a background thread.
     */
    private class UniversityActionListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            // Retrieve country input from the UniversityPanel
            final UniversityPanel sourcePanel = (UniversityPanel) ((JButton) e.getSource()).getParent();
            final String country = sourcePanel.getCountryInput();

            // Validate country input
            if (country.isEmpty()) {
                updateResultPane("<html><font color='red'>Please enter a country.</font></html>");
                return;
            }

            // Display a loading message
            updateResultPane("<html><center>Searching universities in " + country + "...</center></html>");

            // Use SwingWorker for the network operation
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    // This runs in a background thread to avoid freezing the UI
                    return universityService.searchUniversities(country);
                }

                @Override
                protected void done() {
                    // This runs on the EDT after the background task is complete
                    try {
                        final String resultHtml = get(); // Get HTML results from background task
                        if ("Save as Favorite".equals(e.getActionCommand())) {
                            saveCurrentResultsToFile(); // Save current results if the save button was clicked
                        } else {
                            updateResultPane(resultHtml); // Display university search results
                        }
                    } catch (final Exception ex) {
                        updateResultPane("<html><font color='red'>An error occurred: " + ex.getMessage() + "</font></html>");
                        ex.printStackTrace();
                    }
                }
            }.execute();
        }
    }

    /**
     * Handles actions triggered by buttons in the Udemy Courses panel.
     * It performs searches for Udemy courses by subject, reading from a local CSV
     * in a background thread.
     */
    private class UdemyCourseActionListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            // Retrieve selected subject from the UdemyCoursesPanel
            final UdemyCoursesPanel sourcePanel = (UdemyCoursesPanel) ((JButton) e.getSource()).getParent();
            final String selectedSubject = sourcePanel.getSelectedSubject();

            // Display a loading message
            updateResultPane("<html><center>Searching Udemy courses for " + selectedSubject + "...</center></html>");

            // Use SwingWorker for the file reading operation
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    // This runs in a background thread
                    return udemyCourseService.filterCoursesBySubject(selectedSubject);
                }

                @Override
                protected void done() {
                    // This runs on the EDT
                    try {
                        final String resultHtml = get(); // Get HTML results from background task
                        if ("Save as Favorite".equals(e.getActionCommand())) {
                            saveCurrentResultsToFile(); // Save current results if the save button was clicked
                        } else {
                            updateResultPane(resultHtml); // Display Udemy course search results
                        }
                    } catch (final Exception ex) {
                        updateResultPane("<html><font color='red'>An error occurred: " + ex.getMessage() + "</font></html>");
                        ex.printStackTrace();
                    }
                }
            }.execute();
        }
    }

    /**
     * Updates the text content of the resultPane. Ensures this update happens
     * safely on the Event Dispatch Thread (EDT).
     * @param htmlContent The HTML string to display.
     */
    private void updateResultPane(final String htmlContent) {
        SwingUtilities.invokeLater(() -> resultPane.setText(htmlContent));
    }

    /**
     * Formats and displays a list of Scholarship objects in the resultPane.
     * @param scholarships The list of scholarships to be displayed.
     * @param noResultsMessage The message to show if the list is empty.
     */
    private void displayScholarships(final List<Scholarship> scholarships, final String noResultsMessage) {
        if (!scholarships.isEmpty()) {
            final StringBuilder results = new StringBuilder("<html>");
            for (final Scholarship scholarship : scholarships) {
                results.append(scholarship.toHTML()).append("<br>");
            }
            results.append("</html>");
            updateResultPane(results.toString());
        } else {
            updateResultPane(noResultsMessage);
        }
    }

    /**
     * Opens a file chooser dialog and saves the current content of the resultPane
     * to a user-selected file.
     */
    private void saveCurrentResultsToFile() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Search Results as Favorite");
        final int userSelection = fileChooser.showSaveDialog(this); // Show save dialog
        if (userSelection == JFileChooser.APPROVE_OPTION) { // If user confirms save
            final File fileToSave = fileChooser.getSelectedFile();
            try (final BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                final String content = resultPane.getText(); // Get content from the result display area
                writer.write(content); // Write content to file
                updateResultPane("<html><font color='green'>Results saved successfully to: " + fileToSave.getAbsolutePath() + "</font></html>");
            } catch (final IOException e) {
                e.printStackTrace();
                updateResultPane("<html><font color='red'>An error occurred while saving the file: " + e.getMessage() + "</font></html>");
            }
        }
    }

    /**
     * Main method to launch the application.
     * Ensures that the GUI creation and updates are handled on the Event Dispatch Thread.
     * @param args Command line arguments (not used).
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainClient().setVisible(true); // Create and display the main application window
        });
    }

    // ====================================================================
    // --- START: SEPARATED UI PANELS (Inner Classes) ---
    // These classes encapsulate the UI components and their layout for each tab.
    // They are responsible for their own visual presentation and providing input
    // values to the main application logic.
    // ====================================================================

    /**
     * JPanel for the Scholarship search functionality.
     * Contains input fields for degree and location, and buttons for search, sort, and save.
     */
    private static class ScholarshipPanel extends JPanel {
        private final JTextField degreeField;
        private final JTextField locationField;

        /**
         * Constructor for ScholarshipPanel.
         * Sets up the layout, adds labels, text fields, and buttons.
         * @param actionListener The ActionListener (from MainClient) to handle button clicks.
         */
        public ScholarshipPanel(final ActionListener actionListener) {
            super(new GridBagLayout()); // Use GridBagLayout for flexible component positioning
            setBackground(new Color(240, 255, 240)); // Set panel background color

            final GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10); // Set padding around components

            // Define common styling elements
            final Font labelFont = new Font("SansSerif", Font.BOLD, 14);
            final Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);
            final Color textColor = new Color(0, 100, 0); // Dark green
            final Color buttonBgColor = new Color(255, 105, 180); // Hot pink

            // Degree input components
            gbc.gridx = 0;
            gbc.gridy = 0;
            final JLabel degreeLabel = new JLabel("Degree:");
            degreeLabel.setFont(labelFont);
            degreeLabel.setForeground(textColor);
            add(degreeLabel, gbc);

            gbc.gridx = 1;
            degreeField = new JTextField(20);
            degreeField.setFont(new Font("SansSerif", Font.PLAIN, 14));
            add(degreeField, gbc);

            // Location input components
            gbc.gridx = 0;
            gbc.gridy = 1;
            final JLabel locationLabel = new JLabel("Location:");
            locationLabel.setFont(labelFont);
            locationLabel.setForeground(textColor);
            add(locationLabel, gbc);

            gbc.gridx = 1;
            locationField = new JTextField(20);
            locationField.setFont(new Font("SansSerif", Font.PLAIN, 14));
            add(locationField, gbc);

            // Search Scholarships Button
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2; // Button spans two columns
            final JButton scholarshipSearchButton = new JButton("Search Scholarships");
            scholarshipSearchButton.setFont(buttonFont);
            scholarshipSearchButton.setBackground(buttonBgColor);
            scholarshipSearchButton.setForeground(Color.WHITE);
            scholarshipSearchButton.setFocusPainted(false);
            scholarshipSearchButton.addActionListener(actionListener); // Attach the listener
            add(scholarshipSearchButton, gbc);

            // Sort by Amount Button
            gbc.gridy = 3; // Move to next row
            final JButton sortByAmountButton = new JButton("Sort by Amount (High to Low)");
            sortByAmountButton.setFont(buttonFont);
            sortByAmountButton.setBackground(buttonBgColor);
            sortByAmountButton.setForeground(Color.WHITE);
            sortByAmountButton.setFocusPainted(false);
            sortByAmountButton.addActionListener(actionListener); // Attach the listener
            add(sortByAmountButton, gbc);

            // Save as Favorite Button
            gbc.gridy = 4; // Move to next row
            final JButton saveScholarshipsButton = new JButton("Save as Favorite");
            saveScholarshipsButton.setFont(buttonFont);
            saveScholarshipsButton.setBackground(buttonBgColor);
            saveScholarshipsButton.setForeground(Color.WHITE);
            saveScholarshipsButton.setFocusPainted(false);
            saveScholarshipsButton.addActionListener(actionListener); // Attach the listener
            add(saveScholarshipsButton, gbc);

            // Add a titled border for visual grouping
            setBorder(BorderFactory.createTitledBorder("Scholarship Search"));
        }

        /**
         * Retrieves the trimmed text from the degree input field.
         * @return The degree search term.
         */
        public String getDegreeInput() {
            return degreeField.getText().trim();
        }

        /**
         * Retrieves the trimmed text from the location input field.
         * @return The location search term.
         */
        public String getLocationInput() {
            return locationField.getText().trim();
        }
    }

    /**
     * JPanel for the University search functionality.
     * Provides an input field for the country and buttons to search and save.
     */
    private static class UniversityPanel extends JPanel {
        private final JTextField countryField;

        /**
         * Constructor for UniversityPanel.
         * Sets up UI components and attaches the provided action listener.
         * @param actionListener The ActionListener (from MainClient) to handle button clicks.
         */
        public UniversityPanel(final ActionListener actionListener) {
            super(new GridBagLayout());
            setBackground(new Color(240, 255, 240));

            final GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            final Font labelFont = new Font("SansSerif", Font.BOLD, 14);
            final Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);
            final Color textColor = new Color(0, 100, 0);
            final Color buttonBgColor = new Color(255, 105, 180);

            // Country input components
            gbc.gridx = 0;
            gbc.gridy = 0;
            final JLabel countryLabel = new JLabel("Country:");
            countryLabel.setFont(labelFont);
            countryLabel.setForeground(textColor);
            add(countryLabel, gbc);

            gbc.gridx = 1;
            countryField = new JTextField(20);
            countryField.setFont(new Font("SansSerif", Font.PLAIN, 14));
            add(countryField, gbc);

            // Search Universities Button
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            final JButton universitySearchButton = new JButton("Search Universities");
            universitySearchButton.setFont(buttonFont);
            universitySearchButton.setBackground(buttonBgColor);
            universitySearchButton.setForeground(Color.WHITE);
            universitySearchButton.setFocusPainted(false);
            universitySearchButton.addActionListener(actionListener);
            add(universitySearchButton, gbc);

            // Save as Favorite Button
            gbc.gridy = 2;
            final JButton saveUniversitiesButton = new JButton("Save as Favorite");
            saveUniversitiesButton.setFont(buttonFont);
            saveUniversitiesButton.setBackground(buttonBgColor);
            saveUniversitiesButton.setForeground(Color.WHITE);
            saveUniversitiesButton.setFocusPainted(false);
            saveUniversitiesButton.addActionListener(actionListener);
            add(saveUniversitiesButton, gbc);

            setBorder(BorderFactory.createTitledBorder("University Search"));
        }

        /**
         * Retrieves the trimmed text from the country input field.
         * @return The country search term.
         */
        public String getCountryInput() {
            return countryField.getText().trim();
        }
    }

    /**
     * JPanel for the Udemy Courses search functionality.
     * Contains a dropdown for selecting subjects and a search button.
     */
    private static class UdemyCoursesPanel extends JPanel {
        private final JComboBox<String> subjectComboBox;
        private final Map<Integer, String> subjects; // Map to hold subject IDs and names

        /**
         * Constructor for UdemyCoursesPanel.
         * Sets up UI components and attaches the provided action listener.
         * @param actionListener The ActionListener (from MainClient) to handle button clicks.
         */
        public UdemyCoursesPanel(final ActionListener actionListener) {
            super(new GridBagLayout());
            final GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            // Initialize and populate the map of subjects
            subjects = new HashMap<>();
            subjects.put(1, "Business Finance");
            subjects.put(2, "Graphic Design");
            subjects.put(3, "Musical Instruments");
            subjects.put(4, "Web Development");

            // Label for the subject dropdown
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(new JLabel("Select Subject:"), gbc);

            // Dropdown (JComboBox) populated with subject names
            gbc.gridx = 1;
            subjectComboBox = new JComboBox<>(subjects.values().toArray(new String[0]));
            add(subjectComboBox, gbc);

            // Search button for courses
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            final JButton searchButton = new JButton("Search Courses");
            searchButton.addActionListener(actionListener); // Attach the listener
            add(searchButton, gbc);

            setBorder(BorderFactory.createTitledBorder("Udemy Courses Search"));
        }

        /**
         * Retrieves the currently selected subject from the dropdown.
         * @return The selected subject string.
         */
        public String getSelectedSubject() {
            return (String) subjectComboBox.getSelectedItem();
        }
    }

    // ====================================================================
    // --- END: SEPARATED UI PANELS ---
    // ====================================================================

    // ====================================================================
    // --- START: SEPARATED SERVICE CLASSES (Inner Classes) ---
    // These classes handle the core business logic and data access,
    // separated from the UI concerns.
    // ====================================================================

    /**
     * Data model class representing a single Scholarship.
     * Implements Comparable to allow sorting scholarships by their amount.
     */
    static class Scholarship implements Comparable<Scholarship> {
        private final String name;
        private final String degree;
        private final String amount; // Stored as String from CSV, parsed to double when needed
        private final String location;

        /**
         * Constructor for Scholarship.
         * @param name The name of the scholarship.
         * @param degree The degree level.
         * @param amount The scholarship amount as a string (e.g., "$10,000").
         * @param location The location associated with the scholarship.
         */
        public Scholarship(final String name, final String degree, final String amount, final String location) {
            this.name = name;
            this.degree = degree;
            this.amount = amount;
            this.location = location;
        }

        // Getter methods for scholarship properties
        public String getName() {
            return name;
        }

        public String getDegree() {
            return degree;
        }

        public String getLocation() {
            return location;
        }

        /**
         * Converts the scholarship amount string to a double for numerical operations.
         * Handles potential parsing errors by returning 0.0 and logging a warning.
         * @return The scholarship amount as a double.
         */
        public double getAmountAsDouble() {
            try {
                // Remove non-numeric characters (except dot) before parsing
                return Double.parseDouble(amount.replaceAll("[^0-9.]", ""));
            } catch (final NumberFormatException e) {
                System.err.println("Warning: Could not parse amount '" + amount + "'. Defaulting to 0.0. Error: " + e.getMessage());
                return 0.0;
            }
        }

        /**
         * Formats scholarship details into an HTML string suitable for display in a JEditorPane.
         * @return HTML string representing the scholarship.
         */
        public String toHTML() {
            return String.format("Scholarship Name: <b>%s</b>, Degree: %s, Amount: %s, Location: %s", name, degree, amount, location);
        }

        /**
         * Provides a string representation of the Scholarship object.
         * @return String representation.
         */
        @Override
        public String toString() {
            return String.format("Scholarship Name: %s, Degree: %s, Amount: %s, Location: %s", name, degree, amount, location);
        }

        /**
         * Compares this Scholarship object with another for sorting purposes.
         * Sorts in descending order based on the scholarship amount (highest amount first).
         * @param other The other Scholarship object to compare with.
         * @return A negative integer, zero, or a positive integer as this object
         * is less than, equal to, or greater than the specified object.
         */
        @Override
        public int compareTo(final Scholarship other) {
            return Double.compare(other.getAmountAsDouble(), this.getAmountAsDouble());
        }
    }

    /**
     * Service class dedicated to handling scholarship data operations.
     * This includes reading scholarship data from a CSV file, filtering, and sorting.
     */
    private static class ScholarshipService {
        // Path to the scholarships CSV file within the application's resources
        private static final String SCHOLARSHIPS_CSV_PATH = "/scholarships.csv";

        /**
         * Reads scholarship data from the specified CSV file.
         * It expects the CSV to be located in the application's classpath resources.
         * @return A list of Scholarship objects parsed from the CSV.
         * @throws RuntimeException if the CSV file cannot be found or read, or if parsing fails.
         */
        public List<Scholarship> readScholarshipsFromCSV() {
            final List<Scholarship> scholarships = new ArrayList<>();
            // Use try-with-resources for automatic closing of streams
            try (final InputStream is = getClass().getResourceAsStream(SCHOLARSHIPS_CSV_PATH);
                 final InputStreamReader isr = new InputStreamReader(is);
                 final CSVReader reader = new CSVReader(isr)) {

                final List<String[]> records = reader.readAll();
                if (!records.isEmpty()) {
                    records.remove(0); // Skip the header row if present
                }

                for (final String[] record : records) {
                    // Check if the record has enough columns before accessing specific indices
                    // (Assuming columns: [0], Name[1], Degree[2], Amount[3], [4], Location[5])
                    if (record.length >= 6) {
                        final String name = record[1];
                        final String degree = record[2];
                        final String amount = record[3];
                        final String location = record[5];
                        scholarships.add(new Scholarship(name, degree, amount, location));
                    } else {
                        System.err.println("Skipping malformed scholarship record (too few columns): " + String.join(",", record));
                    }
                }
            } catch (final IOException | CsvException e) {
                System.err.println("Error reading scholarships CSV: " + e.getMessage());
                // Wrap checked exceptions in RuntimeException for SwingWorker to handle
                throw new RuntimeException("Failed to load scholarships data from " + SCHOLARSHIPS_CSV_PATH, e);
            } catch (final NullPointerException e) {
                // Catches if getResourceAsStream returns null (file not found in classpath)
                throw new RuntimeException("Scholarships CSV file not found in resources: " + SCHOLARSHIPS_CSV_PATH, e);
            }
            return scholarships;
        }

        /**
         * Filters a given list of scholarships based on matching degree and location.
         * The search is case-insensitive, and "all" can be used as a wildcard for either field.
         * @param scholarships The original list of scholarships to filter.
         * @param degree The degree search term.
         * @param location The location search term.
         * @return A new list containing only the scholarships that match the criteria.
         */
        public List<Scholarship> filterScholarships(final List<Scholarship> scholarships, final String degree, final String location) {
            final List<Scholarship> filteredList = new ArrayList<>();
            for (final Scholarship scholarship : scholarships) {
                // Check if degree matches (case-insensitive, "all" is a wildcard)
                final boolean matchesDegree = degree.equalsIgnoreCase("all") || scholarship.getDegree().toLowerCase().contains(degree.toLowerCase());
                // Check if location matches (case-insensitive, "all" is a wildcard)
                final boolean matchesLocation = location.equalsIgnoreCase("all") || scholarship.getLocation().toLowerCase().contains(location.toLowerCase());

                if (matchesDegree && matchesLocation) {
                    filteredList.add(scholarship);
                }
            }
            return filteredList;
        }

        /**
         * Sorts a list of scholarships by their amount in descending order (highest amount first).
         * This method modifies the provided list in place.
         * @param scholarships The list of scholarships to sort.
         */
        public void sortScholarshipsByAmount(final List<Scholarship> scholarships) {
            // Uses the compareTo method implemented in the Scholarship class
            Collections.sort(scholarships);
        }
    }

    /**
     * Service class responsible for interacting with the Hipolabs Universities API.
     * It handles constructing API requests, making HTTP calls, and parsing JSON responses.
     */
    private static class UniversityService {
        // Base URL for the Hipolabs Universities API search endpoint
        private static final String API_BASE_URL = "http://universities.hipolabs.com/search?country=";

        /**
         * Searches for universities in a specified country using the Hipolabs API.
         * @param country The country name to search for.
         * @return An HTML formatted string of university results, or an error message if the API call fails.
         */
        public String searchUniversities(final String country) {
            try {
                // Encode the country name to handle spaces and special characters in the URL
                final String encodedCountry = URLEncoder.encode(country, "UTF-8");
                final URL url = URI.create(API_BASE_URL + encodedCountry).toURL();
                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET"); // Set HTTP GET request method
                conn.setRequestProperty("Accept", "application/json"); // Request JSON response format

                final int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) { // Check for successful HTTP response (200 OK)
                    return "<html><font color='red'>Failed to fetch universities. HTTP error code: " + responseCode + "</font></html>";
                }

                final StringBuilder response = new StringBuilder();
                // Read the API response from the input stream
                try (final BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String output;
                    while ((output = br.readLine()) != null) {
                        response.append(output);
                    }
                }
                conn.disconnect(); // Close the HTTP connection

                final JSONArray universities = new JSONArray(response.toString());
                if (universities.length() == 0) {
                    return "<html>No universities found for the country: <b>" + country + "</b></html>";
                } else {
                    final StringBuilder results = new StringBuilder("<html>Universities in <b>" + country + "</b>:<br><br>");
                    for (int i = 0; i < universities.length(); i++) {
                        final JSONObject uni = universities.getJSONObject(i);
                        // Safely extract university name and web pages, providing default if not found
                        final String name = uni.optString("name", "N/A");
                        final JSONArray webPages = uni.optJSONArray("web_pages");

                        results.append("<b>University:</b> ").append(name).append("<br>");
                        if (webPages != null && webPages.length() > 0) {
                            final String website = webPages.getString(0); // Get the first website URL
                            results.append("<b>Website:</b> <a href=\"").append(website).append("\">").append(website).append("</a><br>");
                        } else {
                            results.append("<b>Website:</b> No website available<br>");
                        }
                        results.append("---------------------------------<br>");
                    }
                    results.append("</html>");
                    return results.toString();
                }
            } catch (final MalformedURLException e) {
                // Handle errors related to invalid URL construction
                return "<html><font color='red'>Invalid URL format for API call: " + e.getMessage() + "</font></html>";
            } catch (final IOException e) {
                // Handle network-related errors (e.g., no internet, API server unreachable)
                return "<html><font color='red'>Network error or API server not reachable: " + e.getMessage() + "</font></html>";
            } catch (final Exception e) { // Catch all other unexpected exceptions (e.g., JSON parsing errors)
                return "<html><font color='red'>An unexpected error occurred during university search: " + e.getMessage() + "</font></html>";
            }
        }
    }

    /**
     * Service class dedicated to handling Udemy course data operations.
     * It reads course data from a local CSV file and filters it by subject.
     */
    private static class UdemyCourseService {
        // Path to the Udemy courses CSV file within the application's resources
        private static final String UDEMY_CSV_PATH = "/udemy.csv";

        /**
         * Filters Udemy courses from the CSV file based on a specified subject.
         * @param subject The subject to filter courses by.
         * @return An HTML formatted string of matching courses, or a message indicating no courses were found.
         */
        public String filterCoursesBySubject(final String subject) {
            final StringBuilder results = new StringBuilder("<html>Courses available in <b>" + subject + "</b>:<br><br>");
            boolean foundCourse = false;

            // Use try-with-resources for automatic closing of streams
            try (final InputStream is = getClass().getResourceAsStream(UDEMY_CSV_PATH);
                 final InputStreamReader isr = new InputStreamReader(is);
                 final BufferedReader br = new BufferedReader(isr)) {

                String line;
                while ((line = br.readLine()) != null) {
                    // Split the CSV line by comma. Note: This is a simple split and might
                    // not handle commas within quoted fields correctly.
                    final String[] data = line.split(",");
                    // Check if the line has enough columns and if the subject matches (case-insensitive)
                    // (Assuming data[1] is Course Title and data[10] is Subject)
                    if (data.length > 10 && data[10].trim().equalsIgnoreCase(subject.trim())) {
                        results.append("- ").append(data[1]).append("<br>"); // Append course title
                        foundCourse = true;
                    }
                }
            } catch (final IOException e) {
                System.err.println("Error reading Udemy CSV: " + e.getMessage());
                return "<html><font color='red'>An error occurred while reading the Udemy courses file: " + e.getMessage() + "</font></html>";
            } catch (final NullPointerException e) {
                // Catches if getResourceAsStream returns null (file not found in classpath)
                return "<html><font color='red'>Udemy CSV file not found in resources: " + UDEMY_CSV_PATH + "</font></html>";
            }

            // If no courses were found for the subject, append a message
            if (!foundCourse) {
                results.append("No courses found for the selected subject.<br>");
            }
            results.append("</html>");
            return results.toString();
        }
    }

  
}