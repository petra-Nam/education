package com.example;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import com.opencsv.CSVReader;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import com.formdev.flatlaf.FlatLightLaf;


public class MainClient extends JFrame {

    private JTextField degreeField;
    private JTextField locationField;
    private JTextField countryField;
    private JEditorPane resultPane;

    public MainClient() {
        // Set FlatLaf look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setTitle("Matrob Education Finder");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel scholarshipPanel = createScholarshipPanel();
        JPanel universityPanel = createUniversityPanel();
        JPanel udemyPanel = createUdemyCoursesPanel();

        resultPane = new JEditorPane();
        resultPane.setContentType("text/html");
        resultPane.setEditable(false);
        resultPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(resultPane);

        tabbedPane.addTab("Scholarships", scholarshipPanel);
        tabbedPane.addTab("Universities", universityPanel);
        tabbedPane.addTab("Udemy Courses", udemyPanel);

        add(tabbedPane, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        displayWelcomeMessage();
    }

    private JPanel createScholarshipPanel() {
        JPanel scholarshipPanel = new JPanel(new GridBagLayout());
        scholarshipPanel.setBackground(new Color(240, 255, 240)); // Light green background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding between components

        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);

        // Degree Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel degreeLabel = new JLabel("Degree:");
        degreeLabel.setFont(labelFont);
        degreeLabel.setForeground(new Color(0, 100, 0)); // Dark green text
        scholarshipPanel.add(degreeLabel, gbc);

        // Degree Text Field
        gbc.gridx = 1;
        degreeField = new JTextField(20);
        degreeField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        scholarshipPanel.add(degreeField, gbc);

        // Location Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setFont(labelFont);
        locationLabel.setForeground(new Color(0, 100, 0)); // Dark green text
        scholarshipPanel.add(locationLabel, gbc);

        // Location Text Field
        gbc.gridx = 1;
        locationField = new JTextField(20);
        locationField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        scholarshipPanel.add(locationField, gbc);

        // Search Scholarships Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton scholarshipSearchButton = new JButton("Search Scholarships");
        scholarshipSearchButton.setFont(buttonFont);
        scholarshipSearchButton.setBackground(new Color(255, 105, 180)); // Hot pink background
        scholarshipSearchButton.setForeground(Color.WHITE);              // White text
        scholarshipSearchButton.setFocusPainted(false);                  // Remove focus border
        scholarshipSearchButton.addActionListener(e -> searchScholarships());
        scholarshipPanel.add(scholarshipSearchButton, gbc);

        // Sort by Amount Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton sortByAmountButton = new JButton("Sort by Amount (High to Low)");
        sortByAmountButton.setFont(buttonFont);
        sortByAmountButton.setBackground(new Color(255, 105, 180)); // Hot pink background
        sortByAmountButton.setForeground(Color.WHITE);              // White text
        sortByAmountButton.setFocusPainted(false);                  // Remove focus border
        sortByAmountButton.addActionListener(e -> sortScholarshipsByAmount());
        scholarshipPanel.add(sortByAmountButton, gbc);

        // Save as Favorite Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton saveScholarshipsButton = new JButton("Save as Favorite");
        saveScholarshipsButton.setFont(buttonFont);
        saveScholarshipsButton.setBackground(new Color(255, 105, 180)); // Hot pink background
        saveScholarshipsButton.setForeground(Color.WHITE);              // White text
        saveScholarshipsButton.setFocusPainted(false);                  // Remove focus border
        saveScholarshipsButton.addActionListener(e -> saveFavorites());
        scholarshipPanel.add(saveScholarshipsButton, gbc);

        scholarshipPanel.setBorder(BorderFactory.createTitledBorder("Scholarship Search"));

        return scholarshipPanel;
    }

    private JPanel createUniversityPanel() {
        JPanel universityPanel = new JPanel(new GridBagLayout());
        universityPanel.setBackground(new Color(240, 255, 240)); // Light green background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding between components

        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Font buttonFont = new Font("SansSerif", Font.PLAIN, 14);

        // Country Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel countryLabel = new JLabel("Country:");
        countryLabel.setFont(labelFont);
        countryLabel.setForeground(new Color(0, 100, 0)); // Dark green text
        universityPanel.add(countryLabel, gbc);

        // Country Text Field
        gbc.gridx = 1;
        countryField = new JTextField(20);
        countryField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        universityPanel.add(countryField, gbc);

        // Search Universities Button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JButton universitySearchButton = new JButton("Search Universities");
        universitySearchButton.setFont(buttonFont);
        universitySearchButton.setBackground(new Color(255, 105, 180)); // Hot pink background
        universitySearchButton.setForeground(Color.WHITE);              // White text
        universitySearchButton.setFocusPainted(false);                  // Remove focus border
        universitySearchButton.addActionListener(e -> searchUniversities());
        universityPanel.add(universitySearchButton, gbc);

        // Save as Favorite Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton saveUniversitiesButton = new JButton("Save as Favorite");
        saveUniversitiesButton.setFont(buttonFont);
        saveUniversitiesButton.setBackground(new Color(255, 105, 180)); // Hot pink background
        saveUniversitiesButton.setForeground(Color.WHITE);              // White text
        saveUniversitiesButton.setFocusPainted(false);                  // Remove focus border
        saveUniversitiesButton.addActionListener(e -> saveFavorites());
        universityPanel.add(saveUniversitiesButton, gbc);

        universityPanel.setBorder(BorderFactory.createTitledBorder("University Search"));

        return universityPanel;
    }

    private JPanel createUdemyCoursesPanel() {
        JPanel udemyPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Top, left, bottom, right padding

        // Subjects mapping
        Map<Integer, String> subjects = new HashMap<>();
        subjects.put(1, "Business Finance");
        subjects.put(2, "Graphic Design");
        subjects.put(3, "Musical Instruments");
        subjects.put(4, "Web Development");

        // Dropdown for subjects
        gbc.gridx = 0;
        gbc.gridy = 0;
        udemyPanel.add(new JLabel("Select Subject:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> subjectComboBox = new JComboBox<>(subjects.values().toArray(new String[0]));
        udemyPanel.add(subjectComboBox, gbc);

        // Search button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JButton searchButton = new JButton("Search Courses");
        searchButton.addActionListener(e -> {
            String selectedSubject = (String) subjectComboBox.getSelectedItem();
            String filePath = "/Users/I750363/Desktop/education/udemy.csv"; // Absolute path
            filterCoursesBySubject(filePath, selectedSubject);
        });
        udemyPanel.add(searchButton, gbc);

        udemyPanel.setBorder(BorderFactory.createTitledBorder("Udemy Courses Search"));

        return udemyPanel;
    }

    private void searchScholarships() {
        String csvFilePath = "/Users/I750363/Desktop/educationsite/scholarships.csv"; // Update path if needed
        List<Scholarship> scholarships = readCSV(csvFilePath);

        String degree = degreeField.getText().trim();
        String location = locationField.getText().trim();

        if (degree.isEmpty()) {
            resultPane.setText("Please enter a degree.");
            return;
        }
        if (location.isEmpty()) {
            resultPane.setText("Please enter a location.");
            return;
        }

        List<Scholarship> filteredScholarships = filterScholarships(scholarships, degree, location);

        if (!filteredScholarships.isEmpty()) {
            StringBuilder results = new StringBuilder("<html>");
            for (Scholarship scholarship : filteredScholarships) {
                results.append(scholarship.toHTML()).append("<br>");
            }
            results.append("</html>");
            resultPane.setText(results.toString());
        } else {
            resultPane.setText("No scholarships found for the selected degree and location.");
        }
    }

    private void sortScholarshipsByAmount() {
        String csvFilePath = "/Users/I750363/Desktop/educationsite/scholarships.csv"; // Update path if needed
        List<Scholarship> scholarships = readCSV(csvFilePath);

        String degree = degreeField.getText().trim();
        String location = locationField.getText().trim();

        if (degree.isEmpty()) {
            resultPane.setText("Please enter a degree.");
            return;
        }
        if (location.isEmpty()) {
            resultPane.setText("Please enter a location.");
            return;
        }

        List<Scholarship> filteredScholarships = filterScholarships(scholarships, degree, location);
        filteredScholarships.sort(Scholarship::compareTo);

        if (!filteredScholarships.isEmpty()) {
            StringBuilder results = new StringBuilder("<html>");
            for (Scholarship scholarship : filteredScholarships) {
                results.append(scholarship.toHTML()).append("<br>");
            }
            results.append("</html>");
            resultPane.setText(results.toString());
        } else {
            resultPane.setText("No scholarships found for the selected degree and location.");
        }
    }

    private void searchUniversities() {
        String country = countryField.getText().trim();
        if (country.isEmpty()) {
            resultPane.setText("Please enter a country.");
            return;
        }

        try {
            String apiUrl = "http://universities.hipolabs.com/search?country=" + URLEncoder.encode(country, "UTF-8");
            URL url = URI.create(apiUrl).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                resultPane.setText("Failed: HTTP error code : " + conn.getResponseCode());
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            conn.disconnect();

            JSONArray universities = new JSONArray(response.toString());
            if (universities.length() == 0) {
                resultPane.setText("No universities found for the country: " + country);
            } else {
                StringBuilder results = new StringBuilder("<html>Universities in " + country + ":</br>");
                for (int i = 0; i < universities.length(); i++) {
                    JSONObject uni = universities.getJSONObject(i);
                    String name = uni.getString("name");
                    JSONArray webPages = uni.getJSONArray("web_pages");
                    if (webPages.length() > 0) {
                        String website = webPages.getString(0);
                        results.append("University: ").append(name).append("<br>");
                        results.append("Website: <a href=\"").append(website).append("\">").append(website).append("</a><br>");
                    } else {
                        results.append("University: ").append(name).append("<br>");
                        results.append("Website: No website available<br>");
                    }
                    results.append("---------------------------------<br>");
                }
                resultPane.setText(results.toString());
            }
        } catch (Exception e) {
            resultPane.setText("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterCoursesBySubject(String filePath, String subject) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean foundCourse = false;
            StringBuilder results = new StringBuilder("<html>Courses available in " + subject + ":<br>");

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                // Use equalsIgnoreCase for case-insensitive comparison
                if (data.length > 10 && data[10].equalsIgnoreCase(subject)) {
                    results.append("- ").append(data[1]).append("<br>"); // Course title
                    foundCourse = true;
                }
            }

            if (!foundCourse) {
                results.append("No courses found for the selected subject.<br>");
            }

            results.append("</html>");
            resultPane.setText(results.toString());
        } catch (IOException e) {
            resultPane.setText("An error occurred while reading the file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Scholarship> readCSV(String csvFilePath) {
        List<Scholarship> scholarships = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            List<String[]> records = reader.readAll();
            if (!records.isEmpty()) records.remove(0); // Skip header row if exists

            for (String[] record : records) {
                if (record.length >= 5) { // Ensure there are at least 5 columns (Name, Degree, Funds, Date, Location)
                    String name = record[1];
                    String degree = record[2];
                    String amount = record[3];
                    String location = record[5]; // Corrected to index 5 for location

                    scholarships.add(new Scholarship(name, degree, amount, location));
                }
            }
        } catch (IOException | com.opencsv.exceptions.CsvException e) {
            e.printStackTrace();
        }

        return scholarships;
    }

    private List<Scholarship> filterScholarships(List<Scholarship> scholarships, String degree, String location) {
        List<Scholarship> filteredList = new ArrayList<>();
        for (Scholarship scholarship : scholarships) {
            boolean matchesDegree = degree.equalsIgnoreCase("all") || scholarship.getDegree().toLowerCase().contains(degree.toLowerCase());
            boolean matchesLocation = location.equalsIgnoreCase("all") || scholarship.getLocation().toLowerCase().contains(location.toLowerCase());

            if (matchesDegree && matchesLocation) {
                filteredList.add(scholarship);
            }
        }
        return filteredList;
    }

    private void displayWelcomeMessage() {
        resultPane.setText("<html><center><h2>Welcome to Matrob Education Finder</h2></center></html>");
    }

    private void saveFavorites() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Search Results as Favorite");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                String content = resultPane.getText();
                writer.write(content);
            } catch (IOException e) {
                e.printStackTrace();
                resultPane.setText("An error occurred while saving the file: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainClient().setVisible(true));
    }

    static class Scholarship implements Comparable<Scholarship> {
        private String name;
        private String degree;
        private String amount;
        private String location;

        public Scholarship(String name, String degree, String amount, String location) {
            this.name = name;
            this.degree = degree;
            this.amount = amount;
            this.location = location;
        }

        public String getDegree() {
            return degree;
        }

        public String getLocation() {
            return location;
        }

        public double getAmountAsDouble() {
            try {
                return Double.parseDouble(amount.replaceAll("[^0-9.]", ""));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

        public String toHTML() {
            return String.format("Scholarship Name: %s, Degree: %s, Amount: %s, Location: %s", name, degree, amount, location);
        }

        @Override
        public String toString() {
            return String.format("Scholarship Name: %s, Degree: %s, Amount: %s, Location: %s", name, degree, amount, location);
        }

        @Override
        public int compareTo(Scholarship other) {
            return Double.compare(other.getAmountAsDouble(), this.getAmountAsDouble());
        }
    }
}
