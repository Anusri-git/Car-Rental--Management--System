# Car Rental Management System

A full-stack car rental management project built with Java 21, Spring Boot, Spring Data JPA, Spring Security, and MySQL. The frontend is served as static HTML, CSS, and JavaScript files.

## Requirements
- Java 21
- Maven
- MySQL

## Run locally
1. Create a MySQL database named `carrental_db` (or allow the app to create it).
2. Set database credentials using environment variables:
   - `DB_USERNAME`
   - `DB_PASSWORD`
3. Run the project:
   ```bash
   mvn spring-boot:run
   ```
4. Open `http://localhost:8080`.

Do not commit local database passwords or other secrets.
