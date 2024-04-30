# Users API Server 👨‍✈

# Table of Contents

- [Users API Server 👨‍✈](#users-api-serve-r)
- [Table of Contents](#table-of-contents)
- [Java practical test assignment](#java-practical-test-assignment)
  - [The task has two parts:](#the-task-has-two-parts)
    - [Resources:](#resources)
  - [Requirements:](#requirements)
    - [Please note:](#please-note)
  - [Task completion description:](#task-completion-description)
    - [The application implements the following requirements:](#the-application-implements-the-following-requirements)
    - [Used technologies:](#used-technologies)
    - [Simplified Project structure:](#simplified-project-structure)
    - [Components](#components)
    - [Routes](#routes)
    - [Tests](#tests)
  - [Run the application](#run-the-application)
  - [Test the application](#test-the-application)

# Java practical test assignment

## The task has two parts:
1. Using the resources listed below learn what is `RESTful API` and what are the best practices to implement it
2. According to the requirements implement the `RESTful API` based on the web Spring Boot application: controller, responsible for the resource named Users.

### Resources:
[RESTful API Design. Best Practices in a Nutshell](https://phauer.com/2015/restful-api-design-best-practices/).  
[Error Handling for REST with Spring | Baeldung](https://www.baeldung.com/exception-handling-for-rest-with-spring).  
[Testing in Spring Boot | Baeldung](https://www.baeldung.com/spring-boot-testing#unit-testing-with-webmvctest).  
[Testing | Spring](https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#spring-mvc-test-server).

## Requirements:
1. It has the following fields:
   - 1.1. Email (required). Add validation against email pattern
   - 1.2. First name (required)
   - 1.3. Last name (required)
   - 1.4. Birth date (required). Value must be earlier than current date
   - 1.5. Address (optional)
   - 1.6. Phone number (optional)
2. It has the following functionality:
   - 2.1. Create user. It allows to register users who are more than `18` years old. The value `18` should be taken from properties file.
   - 2.2. Update one/some user fields
   - 2.3. Update all user fields
   - 2.4. Delete user
   - 2.5. Search for users by birth date range. Add the validation which checks that `From` is less than `To`.  Should return a list of objects
3. Code is covered by unit tests using Spring
4. Code has error handling for REST
5. API responses are in JSON format
6. Use of database is not necessary. The data persistence layer is not required.
7. Any version of Spring Boot. Java version of your choice
8. You can use Spring Initializer utility to create the project: Spring Initializr

### Please note:
**we assess only those assignments where all requirements are implemented**

## Task completion description:
In this project I use **OpenAPI specification as _source of true_**.
Based on this I use openapi-generator to generate contract for RESTfull controller and DTOs.
Using this approach all team members can be sure that we place on the same point, and docks is 100% compatible with our API.

### The application implements the following requirements:

1. User registration:
   - 1.1. Email validation against email pattern
   - 1.2. First name (required)
   - 1.3. Last name (required)
   - 1.4. Birth date (required and earlier than current date)
2. User functionality:
   - 2.1. Create user (registration for users over 18 years old)
   - 2.2. Update one/some user fields
   - 2.3. Update all user fields
   - 2.4. Delete user
   - 2.5. Search for users by birth date range (with validation that "From" date is less than "To" date)
3. The code is covered by unit tests using Spring.
4. The code has error handling for REST API responses wia `ExceptionHandler` implemented in `ApplicationExceptionHandler` class.
5. The API responses are in JSON format.
6. Implement persistent layer using `Spring Data JPA` and `PostgreSQL` database.
7. The project uses `Spring Boot 3.2.5` and `Java 21`.

### Used technologies:

- **java 21**: The latest LTS version of Java at the time, providing the latest language features and performance improvements.
- **spring boot 3.2.5**: The latest version of the popular Spring Boot framework, which simplifies the development of Spring-based applications.
- **spring-boot-web:** Provides the necessary dependencies for building web applications.
- **spring-boot-data-jpa:** Provides support for the Java Persistence API (JPA) and integration with various data sources.
- **spring-boot-validation:** Provides support for validating user input using annotations and custom validation rules.
- **flyway:** A database migration tool that helps manage and version control database schema changes.
- **postgresql:** A popular open-source relational database management system used for data persistence.
- **jackson-nullable:** A library that helps handle null values in JSON serialization and deserialization.
- **lombok:** A library that generates boilerplate code, such as getters, setters, and constructors, reducing code verbosity.
- **mapstruct:** A code generator that simplifies the implementation of mappings between Java bean types.
- **spring-boot-test:** Provides utilities and annotations for testing Spring Boot applications.
- **junit-jupiter:** The latest version of the popular JUnit testing framework.
- **testcontainers:** A library that provides lightweight, throwaway instances of common databases, or anything else that can run in a Docker container.
- **jacoco**: A code coverage library that helps measure the code coverage of tests.
- **openapi-generator-maven-plugin from openapitools**: A plugin that generates server stubs, and documentation from an OpenAPI specification.

### Simplified Project structure:

```
├── src.main.java.../
│   ├── configs/            -- Main application configuration (datasources, constraints, etc.)
│   ├── controllers/        -- RESTfull API controllers and API error handler
│   ├── domain/             -- DTOs, models and models mapper
│   ├── services/           -- Business logic
│   ├── repositories        -- Data access layer implemented using Spring Data JPA
│   └── exceptions/         -- Application level exceptions
├── users-api-script        -- Script to generate and send to server random users.
├── pom.xml                 -- Project dependencies
├── Dockerfile              -- Docker image build file
├── compose.yaml            -- Docker Compose file for development. It runs by docker-compose starter
├── docker-compose-prod.yaml-- Docker Compose file for deployment.
│                              It use builded image from ghcr.io/malyshevhen/user-api:latest
│                              that created in CI/CD pipeline.
└── README.md               -- Project documentation
```

### Components

1. The `UserController` class is a Spring REST controller that provides a RESTful API for managing user-related operations, including registration, retrieval, update, and deletion. It implements the `UsersApi` interface, which defines the API contract.
The controller class exposes various methods that handle different user-related operations. These methods accept request data in the form of DTOs, interact with the `UserService` to perform the requested operation, and return a `ResponseEntity` with the appropriate HTTP status and response data.

2. The `ApplicationExceptionHandler` class is a centralized exception handling mechanism for the application. It is annotated with `@RestControllerAdvice`, which allows it to handle exceptions thrown by controllers and other components in the application.<br>
The class provides the following exception handling methods:
   - `handleCustomException`: Handles custom application exceptions of type `BaseApplicationException`. It creates an `ErrorResponse` object with the exception message and returns it with the HTTP status code specified in the exception.
   - `handleBadRequest`: Handles various types of bad request exceptions, such as `ConstraintViolationException`, `IllegalArgumentException`, etc. It returns a 400 Bad Request response with the exception message.
   - `handleServerException`: Handles general server exceptions (`Exception` class). It logs the error and returns an internal server error response (500) with the exception message.
This class provides a centralized way to handle exceptions in the application, ensuring consistent and meaningful error responses are returned to the client.

3. The `ApplicationConfig` class is a Spring configuration class that provides beans for the application's configuration. It is annotated with `@Configuration` and `@EnableConfigurationProperties` to enable the use of configuration properties.<br>
The class defines the following beans:
   - `userConstraints()`: Returns an instance of the `UserConstraints` class, which likely contains constraints or rules related to user data.
   - `datasourceProperties()`: Returns an instance of the `DatasourceProperties` class, which likely contains properties related to the data source configuration.
   - `getDataSource(DatasourceProperties datasourceProperties)`: Returns a `DataSource` instance built using the provided `DatasourceProperties`. This bean is responsible for configuring the data source used by the application.
This configuration class is active in all profiles accept `test`, meaning it will be used in non-test environments.

4. The `TestApplicationConfig` class is a Spring configuration class specifically designed for testing purposes.<br>
The class defines the following beans:
   - `userConstraints()`: Returns an instance of the `UserConstraints` class, similar to the `ApplicationConfig` class.
   - `datasourceProperties()`: Returns an instance of the `DatasourceProperties` class, similar to the `ApplicationConfig` class.
   - `getDataSource(DatasourceProperties datasourceProperties)`: Returns a `DataSource` instance built using the provided `DatasourceProperties`, similar to the `ApplicationConfig` class. However, this bean is specifically designed for testing purposes and may use different configuration properties or a different data source implementation.
This configuration class is active when the "test" profile is enabled, meaning it will be used in test environments.<br>
**The purpose of having separate configuration classes for production and test environments is to allow for different configurations and settings to be used in each environment. This separation of concerns helps maintain a clean and organized codebase, making it easier to manage and test the application.**

5. The `DateRange` class is a record that represents a range of dates with a start date (`from`) and an end date (`to`). It is used to filter or query data based on a specific date range.<br>
Here are the key points about this class:
   - It is a record, which is a new feature introduced in Java 16 that provides a compact syntax for defining immutable data classes.
   - The `from` and `to` fields are of type `LocalDate` and are annotated with `@DateTimeFormat` to specify the expected date format (ISO format).
- The constructor of the record performs a validation check to ensure that the `from` date is not after the `to` date. If the condition is violated, it throws an `IllegalArgumentException`.
   - The `getFrom()` and `getTo()` methods are annotated with `@Valid`, `@Past`, and `@PastOrPresent` annotations, respectively. These annotations are used for validation purposes and ensure that the `from` date is in the past, and the `to` date is either in the past or the present.
   - The `@JsonProperty` annotations are used to specify the JSON property names when serializing or deserializing the `DateRange` object.
   - The `isSet()` method checks if either the `from` or `to` date is set, indicating that the `DateRange` object is being used for filtering or querying.
This class is likely used in the application's domain layer, specifically in DTOs (Data Transfer Objects) or request/response models, to represent date range filters or criteria for querying or filtering data.

6. The `exceptions` package contains custom exception classes for the application. These exceptions are used to handle specific error scenarios and provide meaningful error messages to the client.<br>
Here's a brief description of the classes in this package:
   1. `BaseApplicationException`: This is an abstract base class for all custom application exceptions. It extends the `RuntimeException` class and provides a constructor that accepts an error message and an HTTP status code. This class serves as a parent for other application-specific exceptions, allowing them to inherit common behavior and properties.
   2. `EntityAlreadyExistsException`: This exception is thrown when attempting to create new entity with that already exists in the system. It extends the `BaseApplicationException` class and is typically thrown by the service layer when validating user data during registration.
   3. `EntityNotFoundException`: This exception is thrown when attempting to retrieve or update an entity that does not exist in the system. It extends the `BaseApplicationException` class and is typically thrown by the service layer when performing operations on non-existent users.
   4. `UserValidationException`: This exception is thrown when the provided user properties is invalid or does not conform to the expected format. It extends the `BaseApplicationException` class and is typically thrown by the service layer or utility classes when validating email inputs.
These custom exceptions are typically used in the service layer and propagated up to the controller layer. The `ApplicationExceptionHandler` class, which is a centralized exception handling mechanism, catches these exceptions and returns appropriate error responses to the client with the corresponding HTTP status codes and error messages.<br>
By using custom exceptions, the application can provide more specific and meaningful error messages to the client, making it easier to understand and handle different error scenarios. Additionally, these exceptions can be extended or modified as needed to accommodate new error cases or requirements in the future.

7. The `UserRepository` interface is a Spring Data JPA repository interface for managing `User` entities. It extends the `JpaRepository` and `JpaSpecificationExecutor` interfaces, which provide a set of pre-defined methods for performing CRUD (Create, Read, Update, Delete) operations and querying data using specifications.<br>
Additionally, by extending the `JpaSpecificationExecutor` interface, the `UserRepository` can leverage the power of Spring Data JPA specifications to create complex queries using a fluent API. Specifications allow you to define reusable query criteria and combine them using logical operations (AND, OR, NOT) to build more sophisticated queries.

8. The `services` package contains the service layer of the application, which is responsible for implementing the business logic and orchestrating the interactions between the different components of the application.<br>
   1. `UserService` interface: This interface defines the contract for the user service, which includes methods for creating, retrieving, updating, and deleting users. It serves as a blueprint for the implementation of the user service.
   2. `UserServiceImpl` class: This class implements the `UserService` interface and provides the actual implementation of the user service methods. It likely interacts with the `UserRepository` to perform database operations and may also utilize other components or services as needed.
The `UserService` and `UserServiceImpl` classes are responsible for handling the business logic related to user management, such as validating user data, enforcing business rules, and performing any necessary transformations or calculations. They act as an intermediary between the controller layer and the repository layer, ensuring that the application's business requirements are met.<br>
Some common responsibilities of the `UserService` may include:
   - Validating user input data, such as email, phone number, and address formats.
   - Checking for duplicate email addresses during user registration.
   - Handling user profile updates, including updating user information and managing related entities (e.g., addresses, phone numbers).
   - Implementing business rules related to user management, such as enforcing age restrictions or applying specific policies.
By separating the business logic from the controller and repository layers, the `UserService` promotes code reusability, maintainability, and testability. It encapsulates the application's business rules and provides a clear separation of concerns, making it easier to modify or extend the application's functionality in the future.

9. The file `src/main/resources/db/migration/V1__shema.sql` contains the SQL schema for creating the initial database tables for the application. It defines two tables: `addresses` and `users`.<br>
   The `addresses` table stores address information, including country, city, street, and number. It has an auto-incrementing primary key (`id`) and timestamps for tracking when the record was created and last updated.
   The `users` table stores user information, including email, first name, last name, birth date, phone number, and a foreign key reference to the `addresses` table (`address_id`). The `email` field is marked as unique, ensuring that no two users can have the same email address. The table also has an auto-incrementing primary key (`id`) and timestamps for tracking when the record was created and last updated.
This SQL file is likely used in conjunction with Flyway, a database migration tool for Java applications.

10. The `application.yaml` file is the main configuration file for the application.<br>
   - `validation-constraints.user.required-age`: Sets a custom validation constraint for the user's age, requiring it to be at least 18.
The `application-dev.yaml` file is a configuration file specifically for the development environment.
The `application-test.yaml` file is a configuration file specifically for the test environment.<br>
Here are the key configurations:
   - `spring.datasource`: Configures the datasource to use an in-memory PostgreSQL database (`jdbc:tc:postgresql:16-alpine:///test_db`) with the username "postgres" and password "postgres". This is a lightweight database suitable for testing purposes.
   - `spring.jpa.show-sql`: Enables showing SQL queries in the logs for debugging and testing purposes.
   - `spring.flyway.locations`: Specifies the locations for Flyway database migrations, including the main migration scripts (`classpath:db/migration`) and test-specific migration scripts (`classpath:db/testmigration`).
These configuration files allow for easy management of different settings and properties across different environments, promoting separation of concerns and enabling efficient development, testing, and deployment workflows.

### Routes

```
POST /api/users                -- Save new user with required fields: 
                                  email, firstName, lastName, birthDate
                                  and returns created user in JSON format.
                                  Phone number and address are not validated
                                  but can be passed as an optional fields.


GET /api/users                 -- Retrieves a page of users based on pagination 
                                  parameters (page number and size) and optional
                                  query parameters for sorting and filtering by birth date range.

GET /api/users/{id}            -- Retrieves a single user by id. If the user does not exist,
                                  it returns a 404 Not Found error.

PUT /api/users/{id}            -- Updates an existing user with the given id.
                                  It accepts a JSON body containing the fields to be updated
                                  and returns the updated user in JSON format.

PATCH /api/users/{id}/email    -- Updates only email field of an existing user with the given id.
                                  It accepts a JSON body containing the new email value and
                                  returns the updated user in JSON format.

PATCH /api/users/{id}/address  -- Updates only address field of an existing user with the given id.
                                  It accepts a JSON body containing the new address value
                                  and returns the updated user in JSON format.

PATCH /api/users/{id}/phone    -- Updates only phone number field of an existing user with the given id.
                                  It accepts a JSON body containing the new phone value
                                  and returns the updated user in JSON format.

DELETE /api/users/{id}         -- Deletes a user by id. If the user does not exist,
                                  it returns a 404 Not Found error.

DELETE /api/users/{id}/address -- Deletes only address field of an existing user with the given id.
                                  It returns No Content status (204).
```

### Tests

The project includes comprehensive **test coverage**, with various types of tests such as unit tests, integration tests, and end-to-end tests. The test coverage report can be found at [docs/index.html](docs/index.html).

1. The `UserControllerTest` class is a unit test suite for the `UserController` class in a Spring Boot application. It tests various scenarios related to user management, including user registration, retrieval, updating, and deletion.<br>
The test class is annotated with `@WebMvcTest` to load only the web layer and mock the service layer. It also imports the `UserMapperImpl` class for mapping between domain models and DTOs.<br>
The class contains several test methods that cover different aspects of the `UserController`.
Overall, the `UserControllerTest` class provides comprehensive testing for the `UserController` class, covering various scenarios and edge cases to ensure the correct behavior of the user management functionality.

2. This is a unit test class for the `UserServiceImpl` class. It uses `Mockito` to mock the dependencies (`UserRepository` and `UserConstraints`) and tests various scenarios related to user management, such as creating a new user, retrieving users, updating users, and deleting users. The tests cover different cases, including valid and invalid inputs, and verify the expected behavior and interactions with the mocked dependencies.

3. This is an `integration test` class for the `UserService` implementation. It uses the @SpringBootTest annotation to load the entire application context and perform integration tests with a real database. The tests cover various scenarios related to user management, such as creating, retrieving, updating, and deleting users. The class uses `TestContainers` to spin up a temporary database container for testing purposes. The tests cover different cases, including valid and invalid inputs, and verify the expected behavior and interactions with the database.

4. Also test include unit test for mapper, DTOs, some test configurations and utility classes.

## Run the application

To run the application using docker-compose-prod.yaml, follow these steps:

   1. Make sure you have Docker and Docker Compose installed on your machine.
   2. Open a terminal and navigate to the project directory where the docker-compose-prod.yaml file is located.
   3. Set the required environment variables for the database:
   ```bash
      export DATABASE_NAME=your_database_name
      export DATABASE_USERNAME=your_database_username
      export DATABASE_PASSWORD=your_database_password
   ```
   4. Run the following command to start the containers:
   ```bash
   docker-compose -f docker-compose-prod.yaml up -d

   # This command will build and start the containers defined in the docker-compose-prod.yaml file in detached mode.
   ```
   5. Wait for the containers to start up. You can check the status of the containers using the following command:
   ```bash
   docker-compose -f docker-compose-prod.yaml ps
   ```
   6. Once the containers are up and running, you can access the application at http://localhost:8080.
   7. To stop the containers, run the following command:
   ```bash
   docker-compose -f docker-compose-prod.yaml down

   # This will stop and remove the containers.
   ```

## Test the application

To test the application using the provided resources, you can follow these steps:

1. Start the application following [Run the application].
2. To test the endpoints, you can use the Swagger UI interface or a tool like Postman. You can easily import OpenAPI docs as a collection.
3. For testing the `POST /api/users` endpoint, you can use the `/users-api-script/data.json` file as the request body. This file contains a list of user objects that you can send to the endpoint for creating new users.
4. Also, you can use the `/users-api-script/main.go` script to send the user data from the `/users-api-script/data.json` file to the `POST /api/users` endpoint. This script reads the `data.json` file, unmarshals the JSON data into a slice of `User` structs, and then sends a POST request for each user to the specified endpoint.
5. To run the `main.go` script, make sure you have Go installed on your machine. Then, navigate to the directory containing the `main.go` file and run the following command:
   ```bash
   go run main.go
   ```
This script will send a POST request for each user in the `data.json` file to the `http://localhost:8080/api/users` endpoint and print the response from the server.
By following these steps, you can test the functionality of the application using the provided resources, including the Swagger documentation, sample user data, and the Go script for sending requests.
