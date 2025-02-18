
package com.example;

import com.opencsv.CSVReader;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainClient extends JFrame {

    private JTextField countryField, degreeField, locationField, courseField;
    private JTextArea resultArea;

    public MainClient() {
        setTitle("Matrob Education Website");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel with title
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Matrob Education Search");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        topPanel.add(titleLabel);

        // Main panel with GridBagLayout for better control
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel countryLabel = new JLabel("Country:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(countryLabel, gbc);

        countryField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(countryField, gbc);

        JButton searchUniversitiesButton = new JButton("Search Universities");
        gbc.gridx = 2;
        gbc.gridy = 0;
        mainPanel.add(searchUniversitiesButton, gbc);

        JLabel degreeLabel = new JLabel("Degree:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(degreeLabel, gbc);

        degreeField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(degreeField, gbc);

        JLabel locationLabel = new JLabel("Location:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(locationLabel, gbc);

        locationField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(locationField, gbc);

        JButton searchScholarshipsButton = new JButton("Search Scholarships");
        gbc.gridx = 2;
        gbc.gridy = 2;
        mainPanel.add(searchScholarshipsButton, gbc);

        JLabel courseLabel = new JLabel("Course Topic:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(courseLabel, gbc);

        courseField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(courseField, gbc);

        JButton searchCoursesButton = new JButton("Search Courses");
        gbc.gridx = 2;
        gbc.gridy = 3;
        mainPanel.add(searchCoursesButton, gbc);

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        // Bottom panel with scrollable text area
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(resultArea);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action listeners for buttons
        searchUniversitiesButton.addActionListener(e -> searchUniversities());
        searchScholarshipsButton.addActionListener(e -> searchScholarships());
        searchCoursesButton.addActionListener(e -> searchCourses());
    }

    private void searchUniversities() {
        String country = countryField.getText().trim();
        if (country.isEmpty()) {
            resultArea.setText("Please enter a country.");
            return;
        }

        try {
            // Call the API to get universities based on country
            String apiUrl = "http://universities.hipolabs.com/search?country=" + country;
            @SuppressWarnings("deprecation")
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                resultArea.setText("Failed: HTTP error code : " + conn.getResponseCode());
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            conn.disconnect();

            // Parse and display universities
            JSONArray universities = new JSONArray(response.toString());
            if (universities.length() == 0) {
                resultArea.setText("No universities found for the country: " + country);
            } else {
                StringBuilder sb = new StringBuilder("Universities in " + country + ":\n");
                for (int i = 0; i < universities.length(); i++) {
                    JSONObject uni = universities.getJSONObject(i);
                    String name = uni.getString("name");
                    String website = uni.getJSONArray("web_pages").getString(0);
                    sb.append("University: ").append(name).append("\n");
                    sb.append("Website: ").append(website).append("\n");
                    sb.append("---------------------------------\n");
                }
                resultArea.setText(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultArea.setText("Error occurred while searching for universities.");
        }
    }

    private void searchScholarships() {
        String degree = degreeField.getText().trim();
        String location = locationField.getText().trim();

        if (degree.isEmpty()) {
            resultArea.setText("Please enter a degree.");
            return;
        }

        if (location.isEmpty()) {
            resultArea.setText("Please enter a location.");
            return;
        }

        // File path to the unzipped CSV file
        String csvFilePath = "/Users/I750363/Desktop/educationsite/scholarships.csv";
        List<Scholarship> scholarships = readCSV(csvFilePath);

        // Filter scholarships by degree and location
        List<Scholarship> filteredScholarships = filterScholarships(scholarships, degree, location);

        // Display filtered scholarships
        if (!filteredScholarships.isEmpty()) {
            StringBuilder sb = new StringBuilder("Filtered Scholarships:\n");
            for (Scholarship scholarship : filteredScholarships) {
                sb.append(scholarship).append("\n");
            }
            resultArea.setText(sb.toString());
        } else {
            resultArea.setText("No scholarships found for the selected degree and location.");
        }
    }

    private void searchCourses() {
        String courseTopic = courseField.getText().trim();
        if (courseTopic.isEmpty()) {
            resultArea.setText("Please enter a course topic.");
            return;
        }

        // Mock list of courses (you can replace this with an actual API or database query)
        List<String> courses = Arrays.asList(
                "Java Programming",
                "Python for Data Science",
                "Introduction to Machine Learning",
                "Web Development with JavaScript",
                "Data Structures and Algorithms"
        );

        // Filter courses based on the search input
        List<String> matchedCourses = new ArrayList<>();
        for (String course : courses) {
            if (course.toLowerCase().contains(courseTopic.toLowerCase())) {
                matchedCourses.add(course);
            }
        }

        // Display results
        if (!matchedCourses.isEmpty()) {
            StringBuilder sb = new StringBuilder("Courses matching '" + courseTopic + "':\n");
            for (String course : matchedCourses) {
                sb.append(course).append("\n");
            }
            resultArea.setText(sb.toString());
        } else {
            resultArea.setText("No courses found for the topic: " + courseTopic);
        }
    }

    public static List<Scholarship> readCSV(String csvFilePath) {
        List<Scholarship> scholarships = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            List<String[]> records = reader.readAll();
            records.remove(0); // Skip header row

            for (String[] record : records) {
                if (record.length >= 5) { // Ensure there are at least 5 columns (Name, Degree, Funds, Location)
                    String name = record[1];
                    String degree = record[2];
                    String amount = record[3];
                    String location = record[4]; // Corrected to index 4 for location

                    scholarships.add(new Scholarship(name, degree, amount, location));
                }
            }
        } catch (IOException | com.opencsv.exceptions.CsvException e) {
            e.printStackTrace();
        }

        return scholarships;
    }

    public static List<Scholarship> filterScholarships(List<Scholarship> scholarships, String degree, String location) {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainClient client = new MainClient();
            client.setVisible(true);
        });
    }
}

// Scholarship class
class Scholarship {
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

    @Override
    public String toString() {
        return "Scholarship Name: " + name + ", Degree: " + degree + ", Amount: " + amount + ", Location: " + location;
    }
}
