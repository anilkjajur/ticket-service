# ticket-service

Ticket Service allows customer to select best available seats

Implementation of a simple ticket service that facilitates the discovery, temporary hold, and final reservation of seats within a high-demand performance venue.

This application is developed using Spring Boot, Spring JPA, Spring RESTful web services, Maven, PostgreSQL, Swagger2.

### Assumptions
---
1. Users are provided seats based on the availability.
2. Seat numbers, Holding Time and Expiration Time.
3. Holding time for the seats is 60 seconds. If the user does not reserve the seats before 60 seconds, then the holds are removed and user has to send a request again to hold the seats.
4. User can hold and reserve the seats based on hold id and customer email.

### Building Project
---
1.	Clone the project
	
        ```
        git clone https://github.com/anilkjajur/ticket-service.git
        ```
	
2.	Install Postgres Database and default postgres user to public DB connection
	
        ```
        psql -U postgres -h localhost
        ```
	
2.	Kindly make sure JAVA_HOME environment variable is configured and maven bin directory is added to PATH environment variable.
	Run the following commands

        ```
        cd ticket-service
        ```
        ```
        mvn package
        ```
        ```
        cd target
        ```
        ```
        java -jar ticket-service-0.0.1-SNAPSHOT.jar
        ```
        
        After running the above commands successfully, you should see the following messages.
        
        ```
        .... Tomcat started on port(s): 8080 (http)
        .... Started Application in 8.837 seconds (JVM running for 9.214)
        ```

2.	Use browser for testing API using Swagger UI
	
		```
		GET - http://localhost:8080/swagger-ui.html
		```
	    ![swaggerUi](https://github.com/anilkjajur/ticket-service/blob/master/images/Spring-Boot-Ticket-Service-Rest-API.png)

### RESTful Web Services
---

1.	Find the number of seats available within the venue, optionally by seating level
	Note: available seats are seats that are neither held nor reserved.
	* Total seats available in the venue:
	
		```
		GET - http://localhost:8080/api/v1/venue/seats
		```
		
        Response Body:
        ```
        {
          "id": 1,
          "customerEmail": "abx@xyz.com",
          "seats": [
            {
              "price": 10,
              "status": "ON_HOLD",
              "seatId": 1
            }
          ],
          "holdingTime": "2018-03-13T23:57:03.814Z",
          "expirationTime": "2018-03-13T23:58:03.814Z"
        }
        ```
				
		![numSeatsAvailable](https://github.com/anilkjajur/ticket-service/blob/master/images/ticket-service-all-available-seats.png)

2.	Find and hold the best available seats on behalf of a customer, potentially limited to specific levels
	Note: each ticket hold should expire within a set number of seconds.
	
        ```
        POST - http://localhost:8080/api/v1/venue/seats/hold
        ```
        
        Request Body:
        ```
        {
          "numSeats": 1,
          "customerEmail": "abx@xyz.com"
        }
        ```
        
        Response Body:
        ```
        {
          "id": 1,
          "customerEmail": "abx@xyz.com",
          "seats": [
            {
              "price": 10,
              "status": "ON_HOLD",
              "seatId": 1
            }
          ],
          "holdingTime": "2018-03-13T23:57:03.814Z",
          "expirationTime": "2018-03-13T23:58:03.814Z"
        }
        ```
	
	    ![holdSeats](https://github.com/anilkjajur/ticket-service/blob/master/images/ticket-service-seat-hold-for-customer-email.png)
	
	    This request will expire after 60 seconds. Before that, user has to reserve the seats using the web service in the following request.
	
3.	Reserve and commit a specific group of held seats for a customer

        ```
        POST - http://localhost:8080/api/v1/venue/seats/reserve
        ```
        
        Request Body:
        ```
        {
          "customerEmail": "x@test.com",
          "seatHoldId": 1
        }
        ```
        
        Response Body:
        ```
        {
          "bookingCode": "28ea6620-13f3-4a58-a417-8b23a1195400"
        }
        ```
        
        ![reserveSeats](https://github.com/anilkjajur/ticket-service/blob/master/images/ticket-service-reserve-holded-id-email.png)
    
    
 ### DB Data Model
 ---
 The application is designed using PostgreSQL DB and DBVisualizer for data model diagram.
 
 ![schemaDesign](https://github.com/vamshins/ticket-service/blob/master/imges/ticket-db-data-model.png)
