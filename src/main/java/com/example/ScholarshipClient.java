package com.example;

import java.io.*;
import java.util.*;
import com.opencsv.CSVReader;

public class ScholarshipClient {

    public static void main(String[] args) {
        // File path to the unzipped CSV file
        String csvFilePath = "/Users/I750363/Desktop/educationsite/scholarships.csv"; 
        List<Scholarship> scholarships = readCSV(csvFilePath); 

        // Step 1: Ask user for the degree they are looking for
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the degree type you're looking for (e.g., Bachelor, Master, PhD, or 'all' for all degrees):");
        String degree = scanner.nextLine().trim();

        // Step 2: Ask user for the location (country) they are looking for
        System.out.println("Enter the location (country) you're looking for (e.g., United States, or 'all' for all locations):");
        String location = scanner.nextLine().trim();

        // Step 3: Filter scholarships by degree and location
        List<Scholarship> filteredScholarships = filterScholarships(scholarships, degree, location);

        // Step 4: Display filtered scholarships
        if (!filteredScholarships.isEmpty()) {
            System.out.println("Filtered Scholarships:");
            for (Scholarship scholarship : filteredScholarships) {
                System.out.println(scholarship);
            }
        } else {
            System.out.println("No scholarships found for the selected degree and location.");
        }

        scanner.close();
    }

    // Read the CSV file and load scholarships into a list
    public static List<Scholarship> readCSV(String csvFilePath) {
        List<Scholarship> scholarships = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            List<String[]> records = reader.readAll();
            records.remove(0); // Skip header row

            for (String[] record : records) {
                if (record.length >= 5) { // Ensure there are at least 5 columns (Name, Degree, Funds, Date, Location)
                    String name = record[1];
                    String degree = record[2];
                    String amount = record[3];
                    String location = record[5]; // Corrected to index 4 for location

                    scholarships.add(new Scholarship(name, degree, amount, location));
                }
            }
        } catch (IOException | com.opencsv.exceptions.CsvException e) {
            e.printStackTrace();
        }

        return scholarships;
    }

    // Filter scholarships based on degree and location
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

