package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class UniversityAPIClient {
    public static void main(String[] args) {
        try {
            // Step 1: Show the welcome message and options
            System.out.println("Welcome to Matrob Education Website!");
            System.out.println("What are you searching for?");
            System.out.println("1. Universities");
            System.out.println("2. Scholarships");
            System.out.print("Choose an option (1 or 2): ");
            
            // Step 2: Read user input for the option
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String choice = reader.readLine().trim();

            // Step 3: If "1" is selected, proceed with university search
            if (choice.equals("1")) {
                System.out.print("Choose a country: ");
                String country = reader.readLine().trim();

                // Call the API to get universities based on country
                String apiUrl = "http://universities.hipolabs.com/search?country=" + country;
                URL url = URI.create(apiUrl).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    System.out.println("Failed: HTTP error code : " + conn.getResponseCode());
                    return;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    response.append(output);
                }
                conn.disconnect();

                // Step 4: Parse and display universities
                JSONArray universities = new JSONArray(response.toString());
                if (universities.length() == 0) {
                    System.out.println("No universities found for the country: " + country);
                } else {
                    System.out.println("Universities in " + country + ":");
                    for (int i = 0; i < universities.length(); i++) {
                        JSONObject uni = universities.getJSONObject(i);
                        String name = uni.getString("name");
                        String website = uni.getJSONArray("web_pages").getString(0);
                        System.out.println("University: " + name);
                        System.out.println("Website: " + website);
                        System.out.println("---------------------------------");
                    }
                }
            } else if (choice.equals("2")) {
                // Step 5: Placeholder for scholarship functionality
                System.out.println("Scholarship functionality is under development.");
            } else {
                System.out.println("Invalid choice! Please choose 1 or 2.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




