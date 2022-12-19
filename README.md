# api-rate-limiter
API RATE LIMITER

The source repository for API Rate Limiter project.

It provides APIs for throttling API requests using Leaky Bucket and Sliding Window Algorithm.

If leaky bucket algorithm is used, it creates a background thread per user and polls the requests at very 15 seconds from the queue.

If sliding window algorithm is used, the default length of sliding window is 5 requests. The default time for consideration of a window is 1 min.

These properties can be modified in application.properties.


All data structures used are thread-safe.

The application performs input validations.

The application has centralized exception handling using @ControllerAdvice.

The application uses SLF4J and logback for logging.

The application is configured with graceful shutdown.


# Prerequisites to run the application:
Java 11

Gradle


# Command to build application:
**./gradlew build**


# Command to run application:
**./gradlew bootRun**


# Command to run automated unit tests:
**./gradlew test**


# After test execution, code coverage report can be found at:
build/reports/jacoco/test/html/index.html


# To test APIs manually import the Postman collection located at:
src/main/resources/API Rate Limiter.postman_collection.json


# Logging levels:
Change the desired levels in logback.xml and application.properties.


# Graceful shutdown:
Server is configured with graceful shutdown period of 1 minute. To change, modify "spring.lifecycle.timeout-per-shutdown-phase=1m" property in applications.properties.


# OpenAPI Documentation for REST APIs:
While application is running, open below links in the browser:


http://localhost:8080/projects/ratelimiter/swagger-ui/index.html

http://localhost:8080/projects/ratelimiter/v3/api-docs/
