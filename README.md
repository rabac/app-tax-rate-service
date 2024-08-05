# Tax rate service application
Application for storing and providing different tax levels for municipalities based on the date of the year.

# OpenAPI contract
The contract for the API that provides methods for integrating with the Tax Rate rest service can be found here: http://localhost:8080/swagger-ui/index.html

# Running the application
The requirement for running the application locally is a running MongoDB server. 
MongoDB server can be started locally by running the following command in the 'docker' directory.

``
cd docker
docker compose up -d
``

This will start MongoDB locally. 
After this the Spring boot application can be started with the default profile and with this the rest service should be up and running.