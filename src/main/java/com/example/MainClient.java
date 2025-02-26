
package com.example;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainClient extends JFrame {

    private JTextField degreeField;
    private JTextField locationField;
    private JTextField countryField;
    private JEditorPane resultPane;

    public MainClient() {
        setTitle("Matrob Education Finder");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel scholarshipPanel = createScholarshipPanel();
        JPanel universityPanel = createUniversityPanel();

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

        add(tabbedPane, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        displayWelcomeMessage();
    }

    private JPanel createScholarshipPanel() {
        JPanel scholarshipPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        scholarshipPanel.add(new JLabel("Degree:"), gbc);

        gbc.gridx = 1;
        degreeField = new JTextField(20);
        scholarshipPanel.add(degreeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        scholarshipPanel.add(new JLabel("Location:"), gbc);

        gbc.gridx = 1;
        locationField = new JTextField(20);
        scholarshipPanel.add(locationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton scholarshipSearchButton = new JButton("Search Scholarships");
        scholarshipSearchButton.addActionListener(e -> searchScholarships());
        scholarshipPanel.add(scholarshipSearchButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton sortByAmountButton = new JButton("Sort by Amount (High to Low)");
        sortByAmountButton.addActionListener(e -> sortScholarshipsByAmount());
        scholarshipPanel.add(sortByAmountButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton saveScholarshipsButton = new JButton("Save as Favorite");
        saveScholarshipsButton.addActionListener(e -> saveFavorites());
        scholarshipPanel.add(saveScholarshipsButton, gbc);

        scholarshipPanel.setBorder(BorderFactory.createTitledBorder("Scholarship Search"));

        return scholarshipPanel;
    }

    private JPanel createUniversityPanel() {
        JPanel universityPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        universityPanel.add(new JLabel("Country:"), gbc);

        gbc.gridx = 1;
        countryField = new JTextField(20);
        universityPanel.add(countryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JButton universitySearchButton = new JButton("Search Universities");
        universitySearchButton.addActionListener(e -> searchUniversities());
        universityPanel.add(universitySearchButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton saveUniversitiesButton = new JButton("Save as Favorite");
        saveUniversitiesButton.addActionListener(e -> saveFavorites());
        universityPanel.add(saveUniversitiesButton, gbc);

        universityPanel.setBorder(BorderFactory.createTitledBorder("University Search"));

        return universityPanel;
    }

    private void searchScholarships() {
        String csvFilePath = "scholarships.csv";
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

        displayScholarships(filteredScholarships);
    }

    private void sortScholarshipsByAmount() {
        String csvFilePath = "scholarships.csv";
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

        displayScholarships(filteredScholarships);
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

    private List<Scholarship> readCSV(String csvFilePath) {
        List<Scholarship> scholarships = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            List<String[]> records = reader.readAll();
            if (!records.isEmpty()) records.remove(0); // Skip header row if exists

            for (String[] record : records) {
                if (record.length >= 5) { // Ensure there are at least 5 columns (index, title, degrees, funds, location)
                    String name = record[1];
                    String degree = record[2];
                    String amount = record[3];
                    String location = record[5].toLowerCase().trim(); // Normalize location to lowercase

                    scholarships.add(new Scholarship(name, degree, amount, location));
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return scholarships;
    }

    private List<Scholarship> filterScholarships(List<Scholarship> scholarships, String degree, String location) {
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

    private void displayScholarships(List<Scholarship> scholarships) {
        if (!scholarships.isEmpty()) {
            StringBuilder results = new StringBuilder("<html>");
            for (Scholarship scholarship : scholarships) {
                results.append(scholarship.toHTML()).append("<br>");
            }
            resultPane.setText(results.toString());
        } else {
            resultPane.setText("No scholarships found for the selected degree and location.");
        }
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

        @Override
        public String toString() {
            return String.format("Scholarship Name: %s, Degree: %s, Amount: %s, Location: %s", name, degree, amount, location);
        }

        public String toHTML() {
            return String.format("Scholarship Name: %s, Degree: %s, Amount: %s, Location: %s", name, degree, amount, location);
        }

        @Override
        public int compareTo(Scholarship other) {
            return Double.compare(other.getAmountAsDouble(), this.getAmountAsDouble());
        }
    }
}
