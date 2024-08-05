# Tax rate service application
Application for storing and providing different tax levels for municipalities based on the date of the year.

# OpenAPI contract
The contract for the API that provides methods for integrating with the Tax Rate rest service can be found here: http://localhost:8080/swagger-ui/index.html

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
