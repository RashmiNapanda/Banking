# AccountDetails API

## Frameworks and Tools Used

Java 17, Spring Boot 3, Maven 3.8: For building and running the application.
Lombok: To reduce boilerplate code.
MapStruct: For object mapping.
OpenAPI 3.0.3: For API documentation.
Docker: For containerization.
JUnit Jupiter, Mockito, MockMvc: For testing.
Note: No Checkstyle or code quality tools were configured in this project.

## Considerations and assumptions
1. Hazelcast for In-Memory Storage: Since the requirement didn’t specify an external database, Hazelcast was chosen over an in-memory H2 database.
2. API-First Approach: The API-first development methodology was followed.
3. Configuration Properties: Spring Cloud Config Server was not configured to externalize properties due to time constraints.
4. No Code Generation Plugins: No plugins were used to auto-generate classes or methods.
5. Currency Handling: Currency was omitted to keep the application simple.
6. Internal Transaction API: The transaction API accepts entities as input since it’s not exposed externally.

## Code setup and run(if docker is installed)
1. Clone the project from gitHub
2. Navigate to project root folder and execute 
       docker compose build
       docker compose up
3. This will build and run all 3 APIs as separate Docker containers.

## Code setup and run(if docker is NOT installed)
1. Clone the project from gitHub
2. Navigate to the project root folder and run:
       mvn clean install
3. Run the following commands to start the services:
Service Registry:
       java -jar <cloned-path>/banking/service-registry/target/<module>-<version>.jar
Accounts Service:
       java -jar <cloned-path>/banking/accounts/target/<module>-<version>.jar
Transactions Service:
       java -jar <cloned-path>/banking/transactions/target/<module>-<version>.jar
4. Security: Usernames and passwords for API access are defined in application.properties.

## API End points to test
1. Service registry- http://localhost:8761/login
2. Open New Account- http://localhost:8082/api/v1/accounts/open/1?initialCredit=50
3. Get customerAccountDetails- http://localhost:8082/api/v1/accounts/customer/1


## Packaging structure

Considering microservice architecture functional packaging is considered so that it can be deployed as seperate artifacts

## Production ready considerations:

1. Active Profiles: The Spring Boot application uses active profiles for different environments. Properties for each environment (e.g., dev, prod) are placed accordingly.
2. Spring Security:
          Security is enabled with different credentials for production.
          Roles for different endpoints can be configured in the database and loaded at runtime, instead of hardcoding them.
3. Monitoring with Actuator:
          Actuator endpoints are enabled to capture health and metrics data.
4. Security Vault Integration:
          Currently, passwords are hardcoded in the properties file. In a production environment, these should be fetched from secure vaults.
