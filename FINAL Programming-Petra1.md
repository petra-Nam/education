# Welcome to the Learning Outcomes Evaluation

Dear students,

Welcome to this Learning Outcomes Evaluation session. The goal is to assess your understanding and mastery of the learning outcomes for this semester as evidenced by your work that was submitted on your personal git account. Remember to answer each question thoroughly by referencing **Java** code and provide clear explanations where necessary.

Best regards,
Kay Berkling

## Ethics Section regarding generative and other forms of AI

The student acknowledges and agrees that the use of AI is strictly prohibited during this evaluation. By submitting this report, the student affirms that they have completed the form independently and without the assistance of any AI technologies. This agreement serves to ensure the integrity and authenticity of the students work, as well as their understanding of the learning outcomes.


## Checklist before handing in your work

* [ ] Review the assignment requirements to ensure you have completed all the necessary tasks.
* [ ] Double-check your links and make sure that links lead to where you intended. Each answer should have links to work done by you in your own git repository. (Exception is Speed Coding)
* [ ] Make sure you have at least 10 references to your project code (This is important evidence to prove that your project is substantial enough to support the learning outcome of object oriented design and coding within a larger piece of code.)
* [ ] Include comments to explain your referenced code and why it supports the learning outcome.
* [ ] Commit and push this markup file to your personal git repository and hand in the link and a soft-copy via email at the end of the designated time period.

Remember, this checklist is not exhaustive, but it should help you ensure that your work is complete, well-structured, and meets the required standards.

Good luck with your evaluation!

# Project Description (70%)


## Link
https://github.com/petra-Nam/education
https://github.com/petra-Nam/learn-java/discussions

The Matrob Education Finder is a Java Swing desktop application designed to assist students in finding various educational opportunities. It provides a comprehensive platform with the following key functionalities:

Scholarship Search: Users can search for scholarships based on specific degrees and locations. The application also includes a feature to sort scholarships by amount (highest to lowest) and save search results as favorites.

University Search: Students can search for universities in different countries, with results fetched from an external API. This helps users discover institutions that match their desired study destinations.

Udemy Course Search: The application allows users to browse and filter Udemy courses by subject, providing access to a wide range of online learning opportunities.

Student Community: A dedicated section where students can create and manage their profiles, including their name, email, country of origin, and current country. It enables users to connect with other students by filtering profiles based on current country, fostering a sense of community.


Helpful Videos: The Student Community section also includes a list of curated YouTube videos offering advice and guidance for international students, covering topics like moving abroad, finding accommodation, and budgeting.

Data Persistence: Student profiles are saved to a CSV file, ensuring that user-added data is retained across application sessions.

Overall, the Matrob Education Finder aims to be a centralized and user-friendly tool for students navigating the complexities of international education and professional development.
## Link

https://github.com/petra-Nam/education


## TECH STACK

The application is built using the following technologies and libraries:

Core Language: Java

GUI Framework: Java Swing

Build Automation Tool: Apache Maven

CSV Processing: OpenCSV (com.opencsv) for reading and writing CSV files.

JSON Parsing: org.json library for handling JSON data from the University API.

Look and Feel: FlatLaf (com.formdev.flatlaf) for a modern and flat UI design.

Networking: Standard Java java.net package for making HTTP requests to external APIs.

File I/O: Standard Java java.io package for local file operations (e.g., student_profiles.csv).

Desktop Integration: Standard Java java.awt.Desktop for opening web links in the default browser.


## What did you achieve? 

Through the Matrob Education Finder project, i successfully developed a comprehensive and intuitive desktop application designed to significantly streamline the process for students seeking educational opportunities worldwide.

My key achievements include:

Centralized Opportunity Discovery: I created a single platform where users can efficiently search for scholarships, explore universities globally via an external API, and discover relevant online courses from Udemy, eliminating the need to navigate multiple disparate sources.

Community Building: I established a functional student community section, allowing users to create profiles, connect with peers based on location, and foster a supportive network for international students.

Practical Support: I integrated a valuable video resource library within the community tab, providing easily accessible, curated advice and guidance on common challenges faced by international students.

Robust Data Management: I ensured data persistence for student profiles and efficient handling of external data sources (CSV files and web APIs), making the application reliable and continuously updated.

Ultimately, I achieved a user-friendly tool that empowers students with targeted information and a supportive community, simplifying their journey towards international education and skill development.





## Learning Outcomes

| Exam Question | Total Achievable Points | Points Reached During Grading |
|---------------|------------------------|-------------------------------|
| Q1. Algorithms    |           4            |                               |
| Q2.Data types    |           4            |                               |
| Q3.Complex Data Structures |  4            |                               |
| Q4.Concepts of OOP |          6            |                               |
| Q5.OO Design     |           6            |                               |
| Q6.Testing       |           3            |                               |
| Q7.Operator/Method Overloading | 6 |                               |
| Q8.Templates/Generics |       4            |                               |
| Q9.Class libraries |          4            |                               |


## Evaluation Questions

Please answer the following questions to the best of your ability to show your understanding of the learning outcomes. Please provide examples from your project code to support your answers.


## Evaluation Material


### Q1. Algorithms

Algorithms are manyfold and Java can be used to program these. Examples are sorting or search strategies but also mathematical calculations. Please refer to **two** areas in either your regular coding practice (for example from Semester 1) or within your project, where you have coded an algorithm. Do not make reference to code written for other classes, like theoretical informatics.


 Algorithms:**

* **Searching (Filtering/Lookup):**
    * **Explanation:** I used  Searching algorithms  to retrieve specific data elements from a collection. This included direct lookups and linear iterations.

    * **Code Example 1 ( Matrob):** The `filterScholarships()` method implements a linear search to find scholarships matching user-specified criteria. Similarly, `filterCoursesBySubject` performs a linear scan through Udemy CSV data.
    *  (Scholarship Filtering):** `[Scholarship Filtering Logic](https://github.com/petra-Nam/education/blob/main/src/main/java/com/example/ScholarshipClient.java#L63-76)`

    * **Permalink 2 (Udemy Course Filtering):** `[Course Subject Filtering](https://github.com/petra-Nam/education/blob/main/src/main/java/com/example/UdemyCourseFilter.java#L73-98)`
    * **Code Example 3 (Pair Programming with Daniela - Makeup Inventory System):** The `searchProducts()` method in the makeup inventory system performs a linear search through the `products` `ArrayList` to find a product by name.
    * **Permalink 3 (Makeup Product Search):** `[Search Products in Makeup Main](https://github.com/danii-07/Programming-I/blob/main/inventory/makeup/Main.java#L76-L90)`
    https://github.com/petra-Nam/learn-java/commit/4a4098e5a6ce55c82c026c65fd7356c93f4a0c46


| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|           4            |                               |


### Q2. Data types

Please explain the concept of data types and provide examples of different data types in Java.
typical data types in java are int, double, float, char, boolean, long, short, byte, String, and arrays. Please provide one example for each of the **four** following data types in your code.
* arrays
* Strings
* boolean
* your choice


In Java, data types are used to define what kind of values a variable can hold. They help the program know how much memory to use and what kind of operations can be done with that data.

There are two main kinds of data types:
	•	Primitive Data Types like int, double, char, boolean, etc., which hold simple values directly.
	•	Non-Primitive (Reference) Data Types like String, arrays, and objects, which store references to memory locations instead of the actual value.

Below are some examples of different data types I’ve used in my Java projects:


# String

String is used to store text. In Java, Strings are immutable, which means once they’re created, their value can’t be changed directly.

# How I used it (Matrob Education Finder ):

public static class StudentProfile {
    private String name;
    private String email;
    private String originCountry;
    private String currentCountry;
}

In this case, I used String for storing student information like name, email, and countries. Since this kind of data is always text-based, String was the perfect choice.


# boolean

boolean is a data type that can only be true or false. It’s mostly used for making decisions in the code, like controlling if statements or setting conditions.

# #How I used it (Matrob Education Finder):

boolean foundCourse = false;
if (!foundCourse && primaryRecommendations.isEmpty() && secondaryMatches.isEmpty()) {
    results.append("No courses found for the selected subject.<br>");
}

I used boolean here to track whether a course had been found or not, so I could show the right message to the user if nothing matched their search.



# int (my own choice)

int is used for storing whole numbers. I used it often when I needed to keep count of something or store a numeric response.

# Example from my code:

final int responseCode = conn.getResponseCode();
if (responseCode != HttpURLConnection.HTTP_OK) {
    // Handle error
}

This was part of my API call logic. I used int to check if the HTTP response was successful or if something went wrong.



# Array (or Arrays)

An array stores multiple values of the same type. The size is fixed once it’s created. It’s useful when you need to hold a collection of items and know how many there are.

# Example (Matrob Education Finder):

String[] recommendedSubjects = {"Math", "Computer Science", "Engineering"};
for (String subject : recommendedSubjects) {
    System.out.println("Recommended subject: " + subject);
}

In this case, I used an array to store a list of subjects. Then I looped through it to show the user all the recommended subjects.



| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|           4             |                               |



### Q3. Complex Data Structures

Examples of complex data structures in java are ArrayList, HashMap, HashSet, LinkedList, and TreeMap. Please provide an example of how you have used **two** of these complex data structures in your code and explain why you have chosen these data structures.

`ArrayList`:**
    * **Explanation:** `ArrayList` is a dynamic array implementation that allows for resizable arrays. It's suitable for lists where elements can be accessed by index and the size needs to change dynamically.
    * **Code Example 1 (Personal Project - Matrob):** `List<Scholarship> scholarships = new ArrayList<>();` is used in methods like `readCSV()` and `filterScholarships()` to store and manipulate collections of `Scholarship` objects.
    * **Permalink 1:** `[ArrayList Usage in readCSV](https://github.com/petra-Nam/education/blob/main/MainClient.java#L254)`
    * **Code Example 2 (Pair Programming - Makeup Inventory System):** In the `Amor:) makeup store inventory` system, `public static List<Product> products = new ArrayList<>();` and `public static List<User> users = new ArrayList<>();` are used in the `Main` class to dynamically store products and users.
    * **Permalink 2:** `[ArrayLists in Makeup Main](https://github.com/danii-07/Programming-I/blob/main/inventory/makeup/Main.java#L6-L7)`
    * **Justification:** `ArrayList` was chosen because the number of items (scholarships, products, users) is not fixed, and `ArrayList` efficiently handles dynamic resizing as more elements are added or removed.

* **`HashMap`:**
    * **Explanation:** `HashMap` is a data structure that stores key-value pairs, providing fast (average O(1)) lookups, insertions, and deletions based on the key.
    * **Code Example 1 (Personal Project - Matrob):** The `subjects` `HashMap` in `createUdemyCoursesPanel()` maps integer IDs to subject names for efficient category management.
    * **Permalink 1:** `[HashMap for Subjects](https://github.com/petra-Nam/education/blob/main/MainClient.java#L182-L186)`
    * **Code Example 2 (Pair Programming - Student Grades System):** The `StudentGradesSystem` class uses a `HashMap<String, Student> students` to store student objects, enabling efficient retrieval, addition, updating, and removal of students by their unique `studentId`.
    * **Permalink 2:** `[HashMap in StudentGradesSystem](https://github.com/petra-Nam/petra/blob/main/StudentGradesSystem.java#L17)`
    * **Justification:** `HashMap` provides excellent performance for key-based operations, making it ideal for managing collections where quick access and modification by a unique identifier (like a student ID or subject ID) are crucial.


| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|           4             |                               |


### Q4. Concepts of OOP
Concepts of OOP are the basic building blocks of object-oriented programming, such as classes, objects, methods, and attributes. 
Explain HOW and WHY your **project** demonstrates the use of OOP by using all of the following concepts:
* Classes/Objects
* Methods
* Attributes 
Link to the code in your project that demonstrates what you have explained above.
https://github.com/petra-Nam/education/blob/main/src/main/java/com/example/MainClient.java#L26,L43

My project, Matrob Education Finder, is a great example of how I applied Object-Oriented Programming (OOP) in a real-world scenario. I made use of core OOP concepts—classes, objects, attributes, and methods—to structure my code in a clean, organized, and scalable way.

# Classes and Objects
	•	How I used them:
I created several classes to represent different parts of the system. For example, I have:
	•	MainClient: handles the main user interface.
	•	StudentProfile: represents a student’s information.
	•	Scholarship: represents the details of each scholarship.
	•	ScholarshipService: handles all logic related to filtering and recommending scholarships.
From these classes, I create objects.

# Attributes
	•	How I used them:
Inside each class, I added attributes to store data specific to that object. For example:
private String name;
private String email;
private String originCountry;
private String currentCountry;
These belong to the StudentProfile class. The Scholarship class has similar ones like degree, amount, and location.

	•	Why they’re important:
Attributes let each object store its own data. For instance, every student object has its own name and country. I kept these attributes private and used getters and setters to access them, which follows the OOP principle of encapsulation—hiding the internal data and only exposing what’s necessary.



# Methods
	•	How I used them:
Methods define what an object can do. In my project, I wrote:
	•	filterAndRecommendScholarships() in the ScholarshipService class
	•	getName() and setEmail() in the StudentProfile class
	•	addProfile() in StudentCommunity to add a new student
These methods help objects perform tasks and interact with each other.
	•	Why they’re useful:
They make the behavior of each class clear and manageable. Instead of writing the same code multiple times, I just call a method like isValidEmail() wherever I need to validate an email. This keeps my code organized, reusable, and easier to debug.


| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|             6           |                               |

### Q5. OO Design
Please showcase **two** areas where you have used object orientation and explain the advantage that object oriented code brings to the application or the problem that your code is addressing.
Examples in java of good oo design are encapsulation, inheritance, polymorphism, and abstraction. 


In my Matrob Education Finder project, I intentionally applied Object-Oriented Programming (OOP) principles to keep the design clean, maintainable, and easy to scale. Here are two areas where I made good use of OOP, along with the specific benefits that came with it:



# Area 1: Encapsulation and Modularity with Service Classes
	•	OOP Concepts Used: Encapsulation and Modularity
	•	How I applied it:
I created separate service classes inside MainClient.java like ScholarshipService, UniversityService, and UdemyCourseService—each focused on a specific responsibility.
For example, ScholarshipService handles everything related to scholarships:
	•	Reading data from CSV files (readScholarshipsFromCSV)
	•	Filtering and recommending (filterAndRecommendScholarships)
	•	Sorting by amount (sortScholarshipsByAmount)
All the internal data (like SCHOLARSHIPS_CSV_PATH) is private, so it’s not directly accessed from outside. Instead, everything is handled through public methods.
	•	Why it was useful:
This made my code much easier to manage and scale. For example, if I decide to switch from reading scholarships from a CSV to using a database, I only need to update the code in ScholarshipService. Other parts of the app won’t break because they don’t know (or care) how the data is fetched—they just use the public methods. This kind of encapsulation keeps responsibilities clear and makes the app more robust in the long run.


# Area 2: Abstraction and Polymorphism with Custom Cell Renderers
	•	OOP Concepts Used: Abstraction and Polymorphism
	•	How I applied it:
In the StudentCommunity class, I needed a clean way to display different types of objects in a JList. So I created custom classes—StudentProfileCellRenderer and VideoCellRenderer—that implement the ListCellRenderer interface from Java Swing.
	•	StudentProfileCellRenderer customizes how student info is shown (like name, email, countries).
	•	VideoCellRenderer displays video content differently, focusing on title and description.
Because both classes implement the same interface (ListCellRenderer), I could plug them into the same list component, and Java would know which getListCellRendererComponent() to call depending on the type.
	•	Why it was useful:
This gave me a lot of flexibility. The JList itself didn’t have to care whether it was rendering a student or a video—it just called the same method, and polymorphism handled the rest. I can easily swap out or add new renderers without touching the rest of the UI logic. This keeps the code both extendable and decoupled, which is exactly what you want in a large application.

| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|              6          |                               |



### Q6. Testing
Java code is tested by using JUnit. Please explain how you have used JUnit in your project and provide a link to the code where you have used JUnit. Links do not have to refer to your project and can refer to your practice code. If you tested without JUnit, please explain how you tested your code.
Be detailed about what you are testing and how you argue for your test cases. 

Feel free to refer to the vibe coding session where you explored testing. (pair programming - you may link to your partner git and name him/her.)

Test cases usually cover the following areas:
* boundary cases
* normal cases
* error cases / catching exceptions 

In my Matrob Education Finder project, I didn’t use JUnit for automated testing. Instead, I tested the application through a combination of manual in-app testing, debugging with System.out.println, and collaborative testing during pair programming sessions.



# How I Tested the Code

Manual Testing + Debugging

Most of my testing happened while running the application and interacting with the actual user interface.
	•	I would start the app, go through different tabs, enter data (like student info or degree names), click buttons, and watch how the results updated in the GUI.
	•	I also used System.out.println statements throughout the code to check internal values, follow logic flow, and troubleshoot unexpected behavior—especially useful when tracking down things like NullPointerExceptions.

Example:

When filtering Udemy courses or scholarships, I’d print out the course lists before and after filtering to confirm the logic worked as expected.



🧪 What I Tested

#  Normal Cases
	•	Adding valid student profiles
	•	Searching for scholarships or universities with valid input
	•	Filtering courses by subject

These tested whether the core features worked as intended.

#  Boundary Cases
	•	Leaving fields empty (e.g., trying to add a profile without a name)
	•	Searching with “all” to check if full lists appear
	•	Trying searches that give no results to see if messages like “No results found” appear

This helped me catch edge-case behavior and ensure helpful feedback was shown.

# Error Cases
	•	Entering an invalid email
	•	Adding duplicate student emails
	•	Simulating file-not-found errors by renaming CSVs
	•	Testing API failure responses (e.g., entering a made-up country for university search)

These tests were essential to make sure the app didn’t crash and gave useful error messages instead.


 # 👯‍♀️ Pair Programming Testing

While working on other related practice projects—like the Makeup Inventory System—with my programming partner Danii-07, we tested each feature together as we built it.
	•	One of us would write the logic while the other tested it in real-time.
	•	We constantly asked each other “what if the user does this?”, which helped us cover a wider range of use cases.
	•	We didn’t write formal tests, but we discussed and justified each test case to ensure we weren’t missing anything.

| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|         3               |                               |

### Q7. Operator/Method Overloading
An example of operator overloading is the "+" operator that can be used to add two numbers or concatenate two strings. An example of method overloading is having two methods with the same name but different parameters. Please provide an example of how you have used operator or method overloading in your code and explain why you have chosen this method of coding.
The link does not have to be to your project and can be to your practice code.

Concept: Operator Overloading

Explanation: Operator overloading allows a single operator to perform different actions based on the data types it's used with. In Java, the most common example is the + operator: it adds numbers, but it also concatenates (joins) strings.

Code Example from My Project :

Java

// In MainClient.java, within Scholarship class's getAmountAsDouble() method
public double getAmountAsDouble() {
    try {
        return Double.parseDouble(amount.replaceAll("[^0-9.]", ""));
    } catch (final NumberFormatException e) {
        // The '+' operator here concatenates strings for the error message
        System.err.println("Warning: Could not parse amount '" + amount + "'. Defaulting to 0.0. Error: " + e.getMessage());
        return 0.0;
    }
}

Justification for Choosing This Method of Coding:
I used the + operator for string concatenation because it's highly intuitive and makes the code very readable. For combining simple strings, it's the most concise and natural way to express the operation in Java, aligning with common programming practices for clear error messages and UI text.

| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|          6              |                               |



### Q8. Templates/Generics
Generics in java are used to create classes, interfaces, and methods that operate on objects of specified types. Please provide an example of how you have used generics in your code and explain why you have chosen to use generics. The link does not have to be to your project and can be to your practice code.


**Concept:** **Generics in Java**

  * **Explanation:** Generics in Java allow you to create classes, interfaces, and methods that operate on objects of specified types. This means you can write code that works with different types of data while maintaining type safety at compile time. Instead of working with raw types (like `List` without a type parameter), you specify the type of objects the collection will hold (e.g., `List<String>`, `List<Integer>`, `List<Scholarship>`). This helps prevent `ClassCastException` errors at runtime.

  * **Code Example from My Project (Matrob Education Finder):**

    ```java
    // This snippet is from the ScholarshipService inner class in MainClient.java

    public List<Scholarship> readScholarshipsFromCSV() {
        // Declaring a List that specifically holds 'Scholarship' objects
        final List<Scholarship> scholarships = new ArrayList<>(); // <-- Usage of Generics: List<Scholarship> and ArrayList<Scholarship>
        try (final InputStream is = getClass().getResourceAsStream(SCHOLARSHIPS_CSV_PATH);
             final InputStreamReader isr = new InputStreamReader(is);
             final CSVReader reader = new CSVReader(isr)) {

            final List<String[]> records = reader.readAll();
            if (!records.isEmpty()) {
                records.remove(0);
            }

            for (final String[] record : records) {
                if (record.length >= 6) {
                    final String name = record[1];
                    final String degree = record[2];
                    final String amount = record[3];
                    final String location = record[5];
                    // Adding a Scholarship object to the list
                    scholarships.add(new Scholarship(name, degree, amount, location));
                }
            }
        } catch (final IOException | CsvException e) {
            System.err.println("Error reading scholarships CSV: " + e.getMessage());
            throw new RuntimeException("Failed to load scholarships data.", e);
        } catch (final NullPointerException e) {
            throw new RuntimeException("Scholarships CSV file not found in resources: " + SCHOLARSHIPS_CSV_PATH, e);
        }
        return scholarships;
    }
    ```

  * **Justification for Choosing Generics:**
    I chose to use generics in my project, specifically with `List<Scholarship>` and `ArrayList<Scholarship>` (and similarly with `JList<StudentProfile>` and `DefaultListModel<StudentProfile>`), for the following crucial benefits:

    1.  **Type Safety:** By specifying `List<Scholarship>`, I tell the compiler that this list will *only* contain `Scholarship` objects. If I accidentally try to add a `String` or any other type to this list, the compiler will catch the error immediately. This prevents `ClassCastException` errors that would otherwise occur at runtime, making the code much more robust and reliable.
    2.  **Improved Code Readability:** When I see `List<Scholarship>`, I instantly know what kind of data that list is intended to hold. This makes the code easier to understand and maintain, both for myself and for anyone else looking at the codebase.
    
In essence, generics allowed me to write more robust, readable, and type-safe code, which is essential for developing a reliable application like the Matrob Education Finder.

| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|           6             |                               |

### Q9. Class Libraries
Examples of class libraries in java are the Java Standard Library, JavaFX, Apache Commons, JUnit, Log4j, Jackson, Guava, Joda-Time, Hibernate, Spring, Maven, and many more. Please provide an example of how you have used a class library in your **project** code and explain why you have chosen to use this class library. 



**Class Library Used:** **OpenCSV (`com.opencsv`)**

  * **Explanation:** OpenCSV is a powerful, open-source Java library specifically designed to simplify the process of reading data from and writing data to CSV (Comma Separated Values) files. It handles the complexities of CSV formatting, such as dealing with commas within data fields, quoted values, and different line endings, allowing developers to focus on the data itself rather than the parsing mechanics.

  * **Code Example from My Project (Matrob Education Finder):**

    ```java

    public List<Scholarship> readScholarshipsFromCSV() {
        final List<Scholarship> scholarships = new ArrayList<>();
        try (final InputStream is = getClass().getResourceAsStream(SCHOLARSHIPS_CSV_PATH);
             final InputStreamReader isr = new InputStreamReader(is);
             final CSVReader reader = new CSVReader(isr)) { // <-- Instantiating OpenCSV's CSVReader

            final List<String[]> records = reader.readAll(); // <-- Using OpenCSV's readAll() method
            if (!records.isEmpty()) {
                records.remove(0); // Skips the header row
            }
            // The rest of the method then processes these 'records' (String arrays)
            // into Scholarship objects.
        } catch (final IOException | CsvException e) {
            // Error handling for file reading or CSV parsing issues
            System.err.println("Error reading scholarships CSV: " + e.getMessage());
            throw new RuntimeException("Failed to load scholarships data.", e);
        }
        return scholarships;
    }


  * **Justification for Choosing OpenCSV:**
    I chose to integrate OpenCSV into the Matrob Education Finder for several critical reasons that directly benefited the project's development and functionality:

    1.  **Simplified Data Handling:** My application relies heavily on data stored in CSV format for scholarships and Udemy courses, and for persisting student profiles. OpenCSV provided a high-level, intuitive API that abstracted away the complexities of manual CSV parsing. This meant we didn't have to write tedious and error-prone code to handle commas, quotes, and line breaks, significantly speeding up development.
    2.  **Robustness and Reliability:** CSV files can sometimes be inconsistently formatted. OpenCSV is a mature and well-tested library that handles various CSV nuances gracefully, ensuring that our data is read and written accurately and reliably, which is crucial for the integrity of the information presented to users.
    3.  **Efficiency:** For managing the datasets within our application, CSV files are a lightweight and efficient storage format. OpenCSV allowed us to interact with these files efficiently, ensuring that data loading and saving operations were performant.
    4.  **Focus on Core Features:** By leveraging a specialized library for CSV processing, i were able to dedicate more of our development effort to implementing the core features of the Matrob Education Finder, such as the search algorithms, recommendation logic, and the user interface, rather than reinventing a robust CSV parser from scratch.

| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|            6            |                               |


# Creativity (10%)
Which one did you choose: 

* [yes] Web Interface with Design- Imprroved my UI
* [ ] Database Connected
* [ ] Multithreading
* [yes] File I/O-I added a video section and started marking videos about studying abroads
* [ ] API
* [ ] Deployment



My project demonstrates significant creativity through its enhanced user interface design and the innovative integration of a video resource section. Instead of relying on the standard, often plain, aesthetics of Java Swing, I leveraged FlatLaf to craft a modern, visually appealing, and intuitive interface, employing thoughtful layouts and custom list renderers to present complex information clearly and engagingly. Furthermore, my creativity extended beyond core search functionality with the addition of a "Helpful Videos" section within the Student Community tab. This feature goes beyond basic File I/O by including videos I personally recorded and produced, featuring interviews that offer practical advice on studying abroad. This thoughtful inclusion of original, curated content enriches the user experience by providing readily accessible support and demonstrates an innovative and personal approach to delivering comprehensive value to students.
https://youtu.be/_3pfqU6hEkU?si=zKNkwetb8fxw3R2Y


| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|            10          |                               |



# Speed Coding (20%)
Please enter **three** Links to your speed coding session GITs and name your partner. 
Experience 1 (Partner Name: Roic Zone):**
        * **Project Link:** `[StudentGradesSystem.java](https://github.com/petra-Nam/petra/blob/main/StudentGradesSystem.java)`
    * **Experience 2 (Partner Name: Benni Muita):**
        * **Project Link:** `[Booking System Project](https://github.com/bennixm/javaxprogramming/tree/main/week3)`
    * **Experience 3 (Partner Name: Daniela):**
        * **Project Link:** `[Makeup Inventory Project](https://github.com/danii-07/Programming-I/tree/main/inventory/makeup)`




Paste your class diagram for your project that you developed during the peer review class here: 

[*your image*](https://github.com/petra-Nam/education/upload/main)

It can be done very simply by just copying any image and pasting it while editing Readme.md.


| Total Achievable Points | Points Reached During Grading |
|------------------------|-------------------------------|
|                        |                               |
|            16            |                               |





