package com.example;

import java.io.*;
import java.util.*;

public class UdemyCourseFilter {
    public static void main(String[] args) {
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
            
            // Read and filter dataset
            String filePath = "udemy.csv"; // Ensure the correct path
            filterCoursesBySubject(filePath, selectedSubject);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
