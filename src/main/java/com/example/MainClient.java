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
 * MainClient class represents the main JFrame for the Matrob Education Finder application.
 * It sets up the overall GUI structure, including tabbed panels for different search
 * functionalities (Scholarships, Universities, Udemy Courses, Community) and a central
 * result display area.
 *
 * This class orchestrates the interaction between UI panels and dedicated service classes
 * for data retrieval and processing, ensuring the GUI remains responsive by using SwingWorker
 * for long-running operations.
 */
public class MainClient extends JFrame {

    // JEditorPane to display search results, capable of rendering HTML
    private final JEditorPane resultPane;
    // JScrollPane to allow scrolling of the resultPane if content overflows
    private final JScrollPane resultScrollPane;

    // Service classes to encapsulate business logic and data access
    private final ScholarshipService scholarshipService;
    private final UniversityService universityService;
    private final UdemyCourseService udemyCourseService;

    /**
     * Constructor for the MainClient.
     * Initializes the main JFrame, sets up the look and feel, application icon,
     * creates and adds tabbed panels, and initializes service classes.
     */
    public MainClient() {
        // Set FlatLaf look and feel for a modern UI appearance
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (final UnsupportedLookAndFeelException e) {
            // Print stack trace if setting look and feel fails, but allow app to continue
            e.printStackTrace();
        }

        // Configure the main JFrame properties
        setTitle("Matrob Education Finder");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure application exits on close
        setLocationRelativeTo(null); // Center the window on the screen

        // Set the application icon from resources
        try {
            // getResource() looks for the file in the classpath (e.g., src/main/resources)
            setIconImage(new ImageIcon(getClass().getResource("/matrob.png")).getImage());
        } catch (final Exception e) {
            System.err.println("Error loading application icon: " + e.getMessage());
            // Application can continue without an icon if it fails to load
        }

        // Initialize instances of the service classes
        scholarshipService = new ScholarshipService();
        universityService = new UniversityService();
        udemyCourseService = new UdemyCourseService();

        // Create the central JEditorPane for displaying search results (supports HTML)
        resultPane = new JEditorPane();
        resultPane.setContentType("text/html"); // Set content type to HTML for rich text display
        resultPane.setEditable(false); // Make it read-only
        resultPane.setText("<html><center><h2>Welcome to Matrob Education Finder</h2></center></html>"); // Initial welcome message

        // Wrap the resultPane in a JScrollPane to allow scrolling for long results
        resultScrollPane = new JScrollPane(resultPane);
        resultScrollPane.setPreferredSize(new Dimension(780, 200)); // Set a preferred size for the scroll pane

        // Create a JTabbedPane to organize different search functionalities
        final JTabbedPane tabbedPane = new JTabbedPane();

        // Add individual UI panels to the tabbed pane.
        // Each panel is instantiated with a specific ActionListener that will
        // trigger actions in the MainClient (e.g., updating the resultPane).
        tabbedPane.addTab("Scholarships", new ScholarshipPanel(new ScholarshipActionListener()));
        tabbedPane.addTab("Universities", new UniversityPanel(new UniversityActionListener()));
        tabbedPane.addTab("Udemy Courses", new UdemyCoursesPanel(new UdemyCourseActionListener()));

        // Add the community panel (assuming StudentCommunity is a separate class)
        final StudentCommunity studentCommunity = new StudentCommunity();
        tabbedPane.addTab("Community", studentCommunity.getCommunityPanel());

        // Set the layout of the main JFrame to BorderLayout
        setLayout(new BorderLayout());
        // Add the tabbed pane to the center of the frame
        add(tabbedPane, BorderLayout.CENTER);
        // Add the scrollable result pane to the bottom (south) of the frame
        add(resultScrollPane, BorderLayout.SOUTH);
    }

    /**
     * ActionListener for Scholarship panel buttons.
     * Handles search, sort, and save actions for scholarships.
     * Uses SwingWorker to perform background operations to keep the UI responsive.
     */
    private class ScholarshipActionListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            final String command = e.getActionCommand(); // Get the action command (button text)
            // Get input from the ScholarshipPanel
            final ScholarshipPanel sourcePanel = (ScholarshipPanel) ((JButton) e.getSource()).getParent();
            final String degree = sourcePanel.getDegreeInput();
            final String location = sourcePanel.getLocationInput();

            // Input validation
            if (degree.isEmpty()) {
                updateResultPane("<html><font color='red'>Please enter a degree.</font></html>");
                return;
            }
            if (location.isEmpty()) {
                updateResultPane("<html><font color='red'>Please enter a location.</font></html>");
                return;
            }

            // Show loading message while operation is in progress
            updateResultPane("<html><center>Loading scholarships...</center></html>");

            // Execute the long-running operation in a background thread using SwingWorker
            new SwingWorker<List<Scholarship>, Void>() {
                @Override
                protected List<Scholarship> doInBackground() throws Exception {
                    // This code runs in a background thread
                    List<Scholarship> allScholarships = scholarshipService.readScholarshipsFromCSV();
                    return scholarshipService.filterScholarships(allScholarships, degree, location);
                }

                @Override
                protected void done() {
                    // This code runs on the Event Dispatch Thread (EDT) after doInBackground completes
                    try {
                        List<Scholarship> filteredScholarships = get(); // Get the result from doInBackground

                        if ("Search Scholarships".equals(command)) {
                            displayScholarships(filteredScholarships, "No scholarships found for the selected degree and location.");
                        } else if ("Sort by Amount (High to Low)".equals(command)) {
                            scholarshipService.sortScholarshipsByAmount(filteredScholarships); // Sort the filtered list
                            displayScholarships(filteredScholarships, "No scholarships found to sort for the selected criteria.");
                        } else if ("Save as Favorite".equals(command)) {
                            saveCurrentResultsToFile(); // Save the currently displayed results
                        }
                    } catch (final Exception ex) {
                        // Handle exceptions that occurred in doInBackground
                        updateResultPane("<html><font color='red'>An error occurred: " + ex.getMessage() + "</font></html>");
                        ex.printStackTrace();
                    }
                }
            }.execute(); // Start the SwingWorker
        }
    }

    /**
     * ActionListener for University panel buttons.
     * Handles search and save actions for universities.
     * Uses SwingWorker for network operations.
     */
    private class UniversityActionListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            // Get input from the UniversityPanel
            final UniversityPanel sourcePanel = (UniversityPanel) ((JButton) e.getSource()).getParent();
            final String country = sourcePanel.getCountryInput();

            // Input validation
            if (country.isEmpty()) {
                updateResultPane("<html><font color='red'>Please enter a country.</font></html>");
                return;
            }

            // Show loading message
            updateResultPane("<html><center>Searching universities in " + country + "...</center></html>");

            // Execute network operation in background
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return universityService.searchUniversities(country);
                }

                @Override
                protected void done() {
                    try {
                        final String resultHtml = get(); // Get HTML result from background task
                        if ("Save as Favorite".equals(e.getActionCommand())) {
                            saveCurrentResultsToFile(); // Save the currently displayed results
                        } else {
                            updateResultPane(resultHtml); // Update UI with results
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
     * ActionListener for Udemy Courses panel buttons.
     * Handles search and save actions for Udemy courses.
     * Uses SwingWorker for file reading operations.
     */
    private class UdemyCourseActionListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            // Get selected subject from the UdemyCoursesPanel
            final UdemyCoursesPanel sourcePanel = (UdemyCoursesPanel) ((JButton) e.getSource()).getParent();
            final String selectedSubject = sourcePanel.getSelectedSubject();

            // Show loading message
            updateResultPane("<html><center>Searching Udemy courses for " + selectedSubject + "...</center></html>");

            // Execute file reading operation in background
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return udemyCourseService.filterCoursesBySubject(selectedSubject);
                }

                @Override
                protected void done() {
                    try {
                        final String resultHtml = get(); // Get HTML result from background task
                        if ("Save as Favorite".equals(e.getActionCommand())) {
                            saveCurrentResultsToFile(); // Save the currently displayed results
                        } else {
                            updateResultPane(resultHtml); // Update UI with results
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
     * Safely updates the text of the resultPane on the Event Dispatch Thread (EDT).
     * @param htmlContent The HTML string to display in the result pane.
     */
    private void updateResultPane(final String htmlContent) {
        // Ensure UI updates happen on the EDT
        SwingUtilities.invokeLater(() -> resultPane.setText(htmlContent));
    }

    /**
     * Formats and displays a list of scholarships in the resultPane.
     * @param scholarships The list of Scholarship objects to display.
     * @param noResultsMessage The message to display if the list is empty.
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
     * Allows the user to save the current content of the resultPane to a file.
     * Uses JFileChooser for file selection.
     */
    private void saveCurrentResultsToFile() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Search Results as Favorite");
        final int userSelection = fileChooser.showSaveDialog(this); // 'this' refers to MainClient JFrame
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            final File fileToSave = fileChooser.getSelectedFile();
            try (final BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                final String content = resultPane.getText(); // Get current content from result pane
                writer.write(content);
                updateResultPane("<html><font color='green'>Results saved successfully to: " + fileToSave.getAbsolutePath() + "</font></html>");
            } catch (final IOException e) {
                e.printStackTrace();
                updateResultPane("<html><font color='red'>An error occurred while saving the file: " + e.getMessage() + "</font></html>");
            }
        }
    }

    /**
     * Main method to start the Matrob Education Finder application.
     * Ensures the GUI is created and run on the Event Dispatch Thread (EDT).
     * @param args Command line arguments (not used).
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainClient().setVisible(true); // Create and show the main application window
        });
    }

    // ====================================================================
    // --- START: SEPARATED UI PANELS (Inner Classes for Organization) ---
    // These classes encapsulate the UI components and their layout for each tab.
    // They communicate back to MainClient via ActionListeners.
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
         * @param actionListener The ActionListener to be attached to buttons,
         * allowing MainClient to handle button clicks.
         */
        public ScholarshipPanel(final ActionListener actionListener) {
            super(new GridBagLayout()); // Use GridBagLayout for flexible component placement
            setBackground(new Color(240, 255, 240)); // Light green background for the panel

            final GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10); // Padding around components

            // Define common fonts and colors for UI consistency
            final Font labelFont = new Font("SansSerif", Font.BOLD, 14);
            final Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);
            final Color textColor = new Color(0, 100, 0); // Dark green text
            final Color buttonBgColor = new Color(255, 105, 180); // Hot pink background for buttons

            // Degree Label and Text Field
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

            // Location Label and Text Field
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
            gbc.gridwidth = 2; // Span two columns
            final JButton scholarshipSearchButton = new JButton("Search Scholarships");
            scholarshipSearchButton.setFont(buttonFont);
            scholarshipSearchButton.setBackground(buttonBgColor);
            scholarshipSearchButton.setForeground(Color.WHITE);
            scholarshipSearchButton.setFocusPainted(false); // Remove default focus border
            scholarshipSearchButton.addActionListener(actionListener); // Attach MainClient's listener
            add(scholarshipSearchButton, gbc);

            // Sort by Amount Button
            gbc.gridy = 3; // Move to the next row
            final JButton sortByAmountButton = new JButton("Sort by Amount (High to Low)");
            sortByAmountButton.setFont(buttonFont);
            sortByAmountButton.setBackground(buttonBgColor);
            sortByAmountButton.setForeground(Color.WHITE);
            sortByAmountButton.setFocusPainted(false);
            sortByAmountButton.addActionListener(actionListener); // Attach MainClient's listener
            add(sortByAmountButton, gbc);

            // Save as Favorite Button
            gbc.gridy = 4; // Move to the next row
            final JButton saveScholarshipsButton = new JButton("Save as Favorite");
            saveScholarshipsButton.setFont(buttonFont);
            saveScholarshipsButton.setBackground(buttonBgColor);
            saveScholarshipsButton.setForeground(Color.WHITE);
            saveScholarshipsButton.setFocusPainted(false);
            saveScholarshipsButton.addActionListener(actionListener); // Attach MainClient's listener
            add(saveScholarshipsButton, gbc);

            // Set a titled border for visual grouping
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
     * Contains an input field for country and buttons for search and save.
     */
    private static class UniversityPanel extends JPanel {
        private final JTextField countryField;

        /**
         * Constructor for UniversityPanel.
         * @param actionListener The ActionListener to be attached to buttons.
         */
        public UniversityPanel(final ActionListener actionListener) {
            super(new GridBagLayout());
            setBackground(new Color(240, 255, 240)); // Light green background

            final GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            final Font labelFont = new Font("SansSerif", Font.BOLD, 14);
            final Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);
            final Color textColor = new Color(0, 100, 0);
            final Color buttonBgColor = new Color(255, 105, 180);

            // Country Label and Text Field
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
     * Contains a dropdown for subjects and a search button.
     */
    private static class UdemyCoursesPanel extends JPanel {
        private final JComboBox<String> subjectComboBox;
        // Map is kept here for reference, but subjects are directly put into JComboBox
        private final Map<Integer, String> subjects;

        /**
         * Constructor for UdemyCoursesPanel.
         * @param actionListener The ActionListener to be attached to buttons.
         */
        public UdemyCoursesPanel(final ActionListener actionListener) {
            super(new GridBagLayout());
            final GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            // Subjects mapping for the dropdown
            subjects = new HashMap<>();
            subjects.put(1, "Business Finance");
            subjects.put(2, "Graphic Design");
            subjects.put(3, "Musical Instruments");
            subjects.put(4, "Web Development");

            // Label for dropdown
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(new JLabel("Select Subject:"), gbc);

            // Dropdown (JComboBox) for subjects
            gbc.gridx = 1;
            subjectComboBox = new JComboBox<>(subjects.values().toArray(new String[0]));
            add(subjectComboBox, gbc);

            // Search button
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            final JButton searchButton = new JButton("Search Courses");
            searchButton.addActionListener(actionListener);
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
    // --- START: SEPARATED SERVICE CLASSES (Inner Classes for simplicity) ---
    // These classes encapsulate the business logic and data access.
    // In a larger project, these would typically be in separate .java files.
    // ====================================================================

    /**
     * Represents a Scholarship data model.
     * Implements Comparable for sorting by amount.
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

        // Getters for scholarship properties
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
         * Parses the amount string into a double for numerical comparisons.
         * Removes non-numeric characters before parsing.
         * Handles NumberFormatException by returning 0.0 and logging an error.
         * @return The scholarship amount as a double.
         */
        public double getAmountAsDouble() {
            try {
                // Remove any non-numeric characters except the dot for parsing
                return Double.parseDouble(amount.replaceAll("[^0-9.]", ""));
            } catch (final NumberFormatException e) {
                System.err.println("Warning: Could not parse amount '" + amount + "'. Defaulting to 0.0. Error: " + e.getMessage());
                return 0.0;
            }
        }

        /**
         * Formats scholarship details into an HTML string for display in JEditorPane.
         * @return HTML string representing the scholarship.
         */
        public String toHTML() {
            return String.format("Scholarship Name: <b>%s</b>, Degree: %s, Amount: %s, Location: %s", name, degree, amount, location);
        }

        /**
         * Standard toString method for console output.
         * @return String representation of the scholarship.
         */
        @Override
        public String toString() {
            return String.format("Scholarship Name: %s, Degree: %s, Amount: %s, Location: %s", name, degree, amount, location);
        }

        /**
         * Compares this Scholarship object with another for sorting.
         * Sorts in descending order based on the amount (High to Low).
         * @param other The other Scholarship object to compare with.
         * @return A negative integer, zero, or a positive integer as this object
         * is less than, equal to, or greater than the specified object.
         */
        @Override
        public int compareTo(final Scholarship other) {
            // Sort in descending order (High to Low)
            return Double.compare(other.getAmountAsDouble(), this.getAmountAsDouble());
        }
    }

    /**
     * Service class responsible for Scholarship data operations.
     * Handles reading from CSV, filtering, and sorting scholarships.
     */
    private static class ScholarshipService {
        // Path to the CSV file within the resources folder
        private static final String SCHOLARSHIPS_CSV_PATH = "/scholarships.csv";

        /**
         * Reads scholarship data from the CSV file located in resources.
         * @return A list of Scholarship objects.
         * @throws RuntimeException if the CSV file cannot be read or parsed.
         */
        public List<Scholarship> readScholarshipsFromCSV() {
            final List<Scholarship> scholarships = new ArrayList<>();
            // Use try-with-resources for automatic resource management
            try (final InputStream is = getClass().getResourceAsStream(SCHOLARSHIPS_CSV_PATH);
                 final InputStreamReader isr = new InputStreamReader(is);
                 final CSVReader reader = new CSVReader(isr)) {

                final List<String[]> records = reader.readAll();
                if (!records.isEmpty()) {
                    records.remove(0); // Skip header row if it exists
                }

                for (final String[] record : records) {
                    // Ensure there are enough columns before accessing indices
                    // Based on original code: record[1]=Name, record[2]=Degree, record[3]=Amount, record[5]=Location
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
                // Wrap checked exceptions in a RuntimeException to be caught by SwingWorker.done()
                throw new RuntimeException("Failed to load scholarships data from " + SCHOLARSHIPS_CSV_PATH, e);
            } catch (final NullPointerException e) {
                // Catches if getResourceAsStream returns null (file not found in classpath)
                throw new RuntimeException("Scholarships CSV file not found in resources: " + SCHOLARSHIPS_CSV_PATH, e);
            }
            return scholarships;
        }

        /**
         * Filters a list of scholarships based on degree and location.
         * Case-insensitive matching. "all" is treated as a wildcard.
         * @param scholarships The list of all scholarships to filter.
         * @param degree The degree search term.
         * @param location The location search term.
         * @return A new list containing only the filtered scholarships.
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
         * Sorts a list of scholarships by amount in descending order.
         * @param scholarships The list of scholarships to sort (modified in place).
         */
        public void sortScholarshipsByAmount(final List<Scholarship> scholarships) {
            // Uses the compareTo method implemented in Scholarship
            Collections.sort(scholarships);
        }
    }

    /**
     * Service class responsible for interacting with the Hipolabs Universities API.
     * Handles making HTTP requests and parsing JSON responses.
     */
    private static class UniversityService {
        private static final String API_BASE_URL = "http://universities.hipolabs.com/search?country=";

        /**
         * Searches for universities by country using an external API.
         * @param country The country to search for.
         * @return An HTML formatted string of search results or an error message.
         */
        public String searchUniversities(final String country) {
            try {
                // Encode the country name for URL safety
                final String encodedCountry = URLEncoder.encode(country, "UTF-8");
                final URL url = URI.create(API_BASE_URL + encodedCountry).toURL();
                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET"); // Set HTTP request method
                conn.setRequestProperty("Accept", "application/json"); // Request JSON response

                final int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) { // Check for HTTP 200 OK
                    return "<html><font color='red'>Failed to fetch universities. HTTP error code: " + responseCode + "</font></html>";
                }

                final StringBuilder response = new StringBuilder();
                // Read the API response
                try (final BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String output;
                    while ((output = br.readLine()) != null) {
                        response.append(output);
                    }
                }
                conn.disconnect(); // Close the connection

                final JSONArray universities = new JSONArray(response.toString());
                if (universities.length() == 0) {
                    return "<html>No universities found for the country: <b>" + country + "</b></html>";
                } else {
                    final StringBuilder results = new StringBuilder("<html>Universities in <b>" + country + "</b>:<br><br>");
                    for (int i = 0; i < universities.length(); i++) {
                        final JSONObject uni = universities.getJSONObject(i);
                        // Use optString and optJSONArray for safer access to potentially missing fields
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
                // Handle invalid URL formats
                return "<html><font color='red'>Invalid URL format for API call: " + e.getMessage() + "</font></html>";
            } catch (final IOException e) {
                // Handle network errors (e.g., no internet connection, API server down)
                return "<html><font color='red'>Network error or API server not reachable: " + e.getMessage() + "</font></html>";
            } catch (final Exception e) { // Catch all other exceptions, including JSON parsing errors
                return "<html><font color='red'>An unexpected error occurred during university search: " + e.getMessage() + "</font></html>";
            }
        }
    }

    /**
     * Service class responsible for Udemy Course data operations.
     * Handles reading from CSV and filtering courses by subject.
     */
    private static class UdemyCourseService {
        // Path to the CSV file within the resources folder
        private static final String UDEMY_CSV_PATH = "/udemy.csv";

        /**
         * Filters Udemy courses from the CSV file by a given subject.
         * @param subject The subject to filter courses by.
         * @return An HTML formatted string of matching courses or a "not found" message.
         */
        public String filterCoursesBySubject(final String subject) {
            final StringBuilder results = new StringBuilder("<html>Courses available in <b>" + subject + "</b>:<br><br>");
            boolean foundCourse = false;

            // Use try-with-resources for automatic resource management
            try (final InputStream is = getClass().getResourceAsStream(UDEMY_CSV_PATH);
                 final InputStreamReader isr = new InputStreamReader(is);
                 final BufferedReader br = new BufferedReader(isr)) {

                String line;
                while ((line = br.readLine()) != null) {
                    // Simple split by comma. This can be fragile if CSV fields contain commas.
                    // For production, a dedicated CSV parser (like opencsv) would be better here too.
                    final String[] data = line.split(",");
                    // Based on original code: data[1] = Course Title, data[10] = Subject
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

            if (!foundCourse) {
                results.append("No courses found for the selected subject.<br>");
            }
            results.append("</html>");
            return results.toString();
        }
    }

    // ====================================================================
    // --- END: SEPARATED SERVICE CLASSES ---
    // ====================================================================
}
