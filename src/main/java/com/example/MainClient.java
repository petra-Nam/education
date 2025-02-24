
package com.example;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainClient extends JFrame {

    private JTextField degreeField;
    private JTextField locationField;
    private JTextField countryField;
    private JTextArea resultArea;

    public MainClient() {
        setTitle("Matrob Education Finder");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Scholarship Panel
        JPanel scholarshipPanel = createScholarshipPanel();
        JPanel universityPanel = createUniversityPanel();

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        tabbedPane.addTab("Scholarships", scholarshipPanel);
        tabbedPane.addTab("Universities", universityPanel);

        add(tabbedPane, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createScholarshipPanel() {
        JPanel scholarshipPanel = new JPanel();
        scholarshipPanel.setLayout(new GridLayout(5, 1));

        scholarshipPanel.add(new JLabel("Enter the degree type you're looking for (e.g., Bachelor, Master, PhD, or 'all' for all degrees):"));
        degreeField = new JTextField();
        scholarshipPanel.add(degreeField);

        scholarshipPanel.add(new JLabel("Enter the location (country) you're looking for (e.g., United States, or 'all' for all locations):"));
        locationField = new JTextField();
        scholarshipPanel.add(locationField);

        JButton scholarshipSearchButton = new JButton("Search Scholarships");
        scholarshipSearchButton.addActionListener(e -> searchScholarships());
        scholarshipPanel.add(scholarshipSearchButton);

        return scholarshipPanel;
    }

    private JPanel createUniversityPanel() {
        JPanel universityPanel = new JPanel();
        universityPanel.setLayout(new GridLayout(3, 1));

        universityPanel.add(new JLabel("Enter the country you're looking for universities in:"));
        countryField = new JTextField();
        universityPanel.add(countryField);

        JButton universitySearchButton = new JButton("Search Universities");
        universitySearchButton.addActionListener(e -> searchUniversities());
        universityPanel.add(universitySearchButton);

        return universityPanel;
    }

    private void searchScholarships() {
        String csvFilePath = "scholarships.csv"; // Update with your file path
        List<Scholarship> scholarships = readCSV(csvFilePath);

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

        List<Scholarship> filteredScholarships = filterScholarships(scholarships, degree, location);

        resultArea.setText("");
        if (!filteredScholarships.isEmpty()) {
            StringBuilder results = new StringBuilder();
            for (Scholarship scholarship : filteredScholarships) {
                results.append(scholarship.toString()).append("\n");
            }
            resultArea.setText(results.toString());
        } else {
            resultArea.setText("No scholarships found for the selected degree and location.");
        }
    }

    private void searchUniversities() {
        String country = countryField.getText().trim();
        if (country.isEmpty()) {
            resultArea.setText("Please enter a country.");
            return;
        }

        try {
            String apiUrl = "http://universities.hipolabs.com/search?country=" + URLEncoder.encode(country, "UTF-8");
            URL url = URI.create(apiUrl).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

            JSONArray universities = new JSONArray(response.toString());
            if (universities.length() == 0) {
                resultArea.setText("No universities found for the country: " + country);
            } else {
                StringBuilder results = new StringBuilder("Universities in " + country + ":\n");
                for (int i = 0; i < universities.length(); i++) {
                    JSONObject uni = universities.getJSONObject(i);
                    String name = uni.getString("name");
                    JSONArray webPages = uni.getJSONArray("web_pages");
                    if (webPages.length() > 0) {
                        String website = webPages.getString(0);
                        results.append("University: ").append(name).append("\n");
                        results.append("Website: ").append(website).append("\n");
                    } else {
                        results.append("University: ").append(name).append("\n");
                        results.append("Website: No website available\n");
                    }
                    results.append("---------------------------------\n");
                }
                resultArea.setText(results.toString());
            }
        } catch (Exception e) {
            resultArea.setText("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Read the CSV file and load scholarships into a list
    public static List<Scholarship> readCSV(String csvFilePath) {
        List<Scholarship> scholarships = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            List<String[]> records = reader.readAll();
            if (!records.isEmpty()) records.remove(0); // Skip header row if exists

            for (String[] record : records) {
                if (record.length >= 5) { // Ensure there are at least 5 columns (index, title, degrees, funds, location)
                    String name = record[1];
                    String degree = record[2];
                    String amount = record[3];
                    String location = record[4].toLowerCase().trim(); // Normalize location to lowercase

                    scholarships.add(new Scholarship(name, degree, amount, location));
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return scholarships;
    }

    // Filter scholarships based on degree and location
    public static List<Scholarship> filterScholarships(List<Scholarship> scholarships, String degree, String location) {
        List<Scholarship> filteredList = new ArrayList<>();
        for (Scholarship scholarship : scholarships) {
            boolean matchesDegree = degree.equalsIgnoreCase("all") || scholarship.getDegree().toLowerCase().contains(degree.toLowerCase());
            boolean matchesLocation = location.equalsIgnoreCase("all") || scholarship.getLocation().contains(location.toLowerCase());

            if (matchesDegree && matchesLocation) {
                filteredList.add(scholarship);
            }
        }
        return filteredList;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainClient().setVisible(true));
    }

    // Scholarship class
    static class Scholarship {
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
            return String.format("Scholarship Name: %s, Degree: %s, Amount: %s, Location: %s", name, degree, amount, location);
        }
    }
}
