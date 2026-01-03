# Project Title

## Requirements

- Java 21
- Gradle
- PostgreSQL (running and accessible), preferably on docker

## Setup

1. Ensure PostgreSQL is running on your system.
2. Clone this repository.
3. Configure your database connection settings in the project as needed.

   a. Update `application.yml` and `gradle.properties` with your PostgreSQL credentials.

4. Initialize the database schema using liquibase:
   ```bash
   ./gradlew update
   ```
5. Build the project:
6. Run the application using Gradle:
   ```bash
   ./gradlew bootRun
   ```
   

API will be accessible at `http://localhost:8080`
OpenAPI documentation available at http://localhost:8080/swagger-ui.html


## Further Improvements
- Add unit and integration tests.
- Implement error handling and logging.
- Enable security if the service is exposed publicly.