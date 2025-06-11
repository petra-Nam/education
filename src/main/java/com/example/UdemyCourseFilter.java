package com.example;

import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import java.awt.*;

public class UdemyCourseFilter {
    private JTextPane resultPane = new JTextPane();
    private JLabel statusBar;
    private List<String> searchHistory = new ArrayList<>();

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (Scanner scanner = new Scanner(System.in)) {  // Using try-with-resources to automatically close the scanner
            // Subjects mapping
            Map<Integer, String> subjects = new HashMap<>();
            subjects.put(1, "Business Finance");
            subjects.put(2, "Graphic Design");
            subjects.put(3, "Musical Instruments");
            subjects.put(4, "Web Development");
            
            // Display options
            System.out.println("Pick the course subject you are interested in:");
            for (Map.Entry<Integer, String> entry : subjects.entrySet()) {
                System.out.println(entry.getKey() + ". " + entry.getValue());
            }
            
            // User selection
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            if (!subjects.containsKey(choice)) {
                System.out.println("Invalid choice. Please restart and pick a valid number.");
                return;
            }
            
            String selectedSubject = subjects.get(choice);
            System.out.println("You selected: " + selectedSubject);
            UdemyCourseFilter filter = new UdemyCourseFilter();
            filter.addToSearchHistory(selectedSubject); // Add selected subject to search history
            
            // Read and filter dataset
            String filePath = "/Users/I750363/Desktop/education/udemy.csv"; // Absolute path
            filterCoursesBySubject(filePath, selectedSubject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Display the welcome message
        UdemyCourseFilter filter = new UdemyCourseFilter();
        filter.displayWelcomeMessage();

        // Create and display the status bar
        filter.createStatusBar();

        // Add a search button
        JButton searchButton = new JButton("Search");

        // Display search history
        filter.showSearchHistory();
        searchButton.setIcon(new ImageIcon("path/to/icon.png")); // Add an icon
        searchButton.setPreferredSize(new Dimension(150, 30));  // Set button size
    }
    
    private static void filterCoursesBySubject(String filePath, String subject) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean foundCourse = false;
            System.out.println("Courses available in " + subject + ":");
            
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                // Print data to debug what is being read
                System.out.println("Reading line: " + Arrays.toString(data)); // Debugging line
                
                // Use equalsIgnoreCase for case-insensitive comparison
                if (data.length > 10 && data[10].equalsIgnoreCase(subject)) {
                    System.out.println("- " + data[1]); // Course title
                    foundCourse = true;
                }
            }
            
            if (!foundCourse) {
                System.out.println("No courses found for the selected subject.");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayWelcomeMessage() {
        resultPane.setText("<html><h1>Welcome to Matrob Education Finder</h1>"
            + "<p>Use the tabs above to search for scholarships, universities, and Udemy courses.</p></html>");
    }

    private void createStatusBar() {
        statusBar = new JLabel("Ready");
        JFrame frame = new JFrame("Udemy Course Filter");
        frame.setLayout(new BorderLayout());
        frame.add(statusBar, BorderLayout.SOUTH);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        updateStatusBar("Application started successfully");
        frame.setVisible(true);
    }

    private void updateStatusBar(String message) {
        statusBar.setText(message);
    }

    private void addToSearchHistory(String search) {
        searchHistory.add(search);
    }

    private void showSearchHistory() {
        StringBuilder history = new StringBuilder("<html><h2>Search History</h2><ul>");
        for (String search : searchHistory) {
            history.append("<li>").append(search).append("</li>");
        }
        history.append("</ul></html>");
        resultPane.setText(history.toString());
    }
}
