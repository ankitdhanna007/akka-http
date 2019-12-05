Requirements to run the application:

 Java 8
 Maven
 mySQL

===============================================

Step 1

Clone the repository and edit /src/main/resources/flyway.properties to fill database credentials and schema

Step 2 

Run the following commands:

> mvn initialize flyway:migrate
  (generates schema in the database)
  
> mvn jooq-codegen:generate
  (generates Active record classes for db tables)

> mvn clean install && exec:java
  (installs the dependencies and plugins and start the http server)
  
  
Step3

To test, trigger the following curl commands:

curl -H "Content-type: application/json" -X POST -d '{"user_id": 12,"product_id": 319890}' http://localhost:8080/orders

curl -H "Content-type: application/json" -X PUT -d '{"user_id": 12,"product_id": 31}' http://localhost:8080/orders/{ordr_id}
