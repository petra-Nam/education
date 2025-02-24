
# Matrob Education Finder

## Overview

Matrob Education Finder is a Java application that allows users to search for educational scholarships and universities based on specific criteria. The application features a graphical user interface (GUI) built with Swing, offering two tabs: one for searching scholarships and another for searching universities.

## Features

- Search for scholarships based on degree type and location.
- Search for universities in a specified country.
- Display results in a text area within the application.
- Filter results based on user input.

## Requirements

To run this application, you will need:

- Java Development Kit (JDK) 8 or later.
- OpenCSV library for reading CSV files.
- JSON library for parsing JSON data.

You can download these libraries and add them to your project:

- [OpenCSV](http://opencsv.sourceforge.net/)
- [JSON](https://mvnrepository.com/artifact/org.json/json)

## Setup Instructions

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/yourusername/matrob-education-finder.git
   cd matrob-education-finder
   ```

2. **Download Required Libraries:**

   - Add the OpenCSV library to your project. You can download it from [here](http://opencsv.sourceforge.net/).
   - Add the JSON library to your project. You can download it from [here](https://mvnrepository.com/artifact/org.json/json).

3. **Update the CSV File Path:**

   - Make sure you have a CSV file named `scholarships.csv` in the root directory of your project or update the file path in the `EducationClient.java` file.

4. **Compile and Run the Application:**
   ```bash
   javac EducationClient.java
   java EducationClient
   ```

## Usage

1. **Open the Application:**

   Run the application using the command `java EducationClient`. A window will open with two tabs: "Scholarships" and "Universities".

2. **Search for Scholarships:**

   - Enter the degree type you are looking for (e.g., Bachelor, Master, PhD, or 'all' for all degrees).
   - Enter the location (country) you are looking for (e.g., United States, or 'all' for all locations).
   - Click the "Search Scholarships" button.

3. **Search for Universities:**

   - Enter the country you are looking for universities in.
   - Click the "Search Universities" button.

## Code Structure

- `EducationClient.java`: The main class that contains the GUI and logic for searching scholarships and universities.
- `Scholarship`: A nested class within `EducationClient` that represents a scholarship with attributes like name, degree, amount, and location.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit issues and enhancement requests.

For major changes, please open an issue first to discuss what you would like to change.

## Contact

If you have any questions or suggestions, please feel free to contact us at [your-email@example.com].

---

Thank you for using Matrob Education Finder! We hope it helps you in your educational endeavors.
```

