# Tax rate service application

Application for storing and providing different tax levels for municipalities based on the date of the year.

Further requirements description:

Create a small application to manage taxes applied in different municipalities. The application should
handle tax records that are valid for specific periods and allow users to retrieve tax rates for a
specific municipality on a given date.

Example Scenario

Municipality Copenhagen has its taxes scheduled as follows:

- Yearly tax = 0.2 (for the period between Jan 1, 2024 and Dec 31, 2024),
- Monthly tax = 0.4 (for the period between May 1, 2024 and May 31, 2024),
- No weekly taxes scheduled,
- Two daily taxes = 0.1 (on specific days Jan 1, 2024 and Dec 25, 2024).

The expected results for given inputs are:

| Municipality (Input) | Date (Input)    | Result |
|----------------------|-----------------|--------|
| Copenhagen           | January 1, 2024 | 0.1    |
| Copenhagen           | March 16, 2024  | 0.2    |
| Copenhagen           | May 2, 2024     | 0.4    |
| Copenhagen           | July 10, 2024   | 0.2    |

Full Requirements

Prioritize the implementation of requirements to ensure submission of a functioning application by
the deadline, even if not all requirements are implemented.

The application should:
- Have its own database where municipality taxes and their schedules are stored.
- Allow adding new tax records for municipalities individually.
- Enable users to query specific municipality taxes by municipality name and date.
- Expose functionality via APIs (there is no need for a user interface).
- Handle errors gracefully; internal errors should not be exposed to the end user.
- Include either unit or integration tests.

Bonus Tasks
- Dockerize the application.
- Expose functionality to update tax records via an API.

# OpenAPI contract

The contract for the API that provides methods for integrating with the Tax Rate rest service can be found
here: http://localhost:8080/swagger-ui/index.html

# Building and running the application through Docker

Pre-requisite is that Docker Desktop is installed on the machine.

1. Build the jar file by running the following command in the project home directory.

```
mvn clean package -DskipTests
```

2. Build the docker image for the application by running the following commands

```
cd docker\tax-rate-service
docker build --tag=app-tax-rate-service:latest --file docker-tax-rate-service ../..
```

3. Run docker compose through the following commands

```
cd docker
docker compose up -d
```

4. Verify that the application is running by invoking a http get request from the file `get_TaxRateRestEndpoint.http`.
