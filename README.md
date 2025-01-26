## About the Project  
This project demonstrates a backend system for an ecommerce platform that handles user authentication, item management, and order processing. It uses Spring Boot for backend development ,the application is secured with JWT (JSON Web Tokens) using the HS512 algorithm and supports interaction through REST API client tools like **Postman**.  

### Key Features  
- **Secure REST API**: All endpoints are secured using JWT-based authentication. JWTs are signed using the HS512 algorithm.
- **User Management**: Create users with unique usernames, validate passwords (minimum 8 characters), and ensure passwords and confirm-passwords match during registration.  
- **Item and Card Management**:  
  - Each user is automatically associated with a card upon registration.  
  - Each card can hold multiple items.  
- **Order Processing**: Users can place orders for items in their card.  
- **Error Handling and Logging**:  
  - Applies **org.slf4j.LoggerFactory** for logging actions.  
  - Logs include `log.info`, `log.warn`, and `log.error` for user actions and errors.  

## Built With  
- **Java 11**  
- **Spring Boot**  
- **JWT (JSON Web Tokens)** with HS512 algorithm for authentication
- **Postman** (or similar REST API client) for testing and interaction  
- **SLF4J LoggerFactory** for application logging
- **Hibernate** for database interaction 

## Usage  
### Technical Perspective  
- The application provides a set of RESTful APIs for managing users, items, cards, and orders.  
- **Controllers**: All APIs are implemented with `@RestController` annotations.  
- **Entities**:  
  - `User`: Represents the user and includes username, password, and associated card.  
  - `Card`: Each user is assigned a card to manage items.  
  - `Item`: Represents products available for purchase.  
  - `UserOrder`: Handles order creation and processing.
- **JWT Authentication**:
  - Users authenticate with their credentials, and upon successful login, a JWT signed with HS512 is issued.
  - The token must be included in the Authorization header (e.g., Bearer <token>) for accessing secured endpoints.
- **Validations**:  
  - Password must be at least 8 characters.  
  - Password and confirm-password must match during registration.  
  - Username must be unique.  
### User Perspective  
- Use tools like **Postman** or similar REST clients to interact with the APIs.  
- Key actions include:  
  - **User Registration**: Create a user by sending `POST` requests to the registration endpoint with a valid username, password, and confirm password.  
  - **Login**: Authenticate with JWT and receive a token for subsequent API requests.  
  - **Item Management**: Add items to the card, view items, and manage inventory.  
  - **Order Placement**: Submit an order for items in the card.  
- Logs provide real-time feedback:  
  - Example:  
    - **Successful User Creation**: `log.info("User created successfully: username")`  
    - **Invalid Password**: `log.error("Invalid password format")`  
    - **Order Submission Failure**: `log.warn("Order submission failed - no user found with username")`

## API Endpoints  

Here are some of the API endpoints provided by the application:  

### User Endpoints  
- `GET /api/user/id/{id}`  
  Find a user by their ID.  

- `GET /api/user/{username}`  
  Find a user by their username.  

- `POST /api/user/create`  
  Create a new user. A JSON request body is required with the following fields:  
  ```json
  {
    "username": "your-username",
    "password": "your-password",
    "confirmPassword": "your-password"
  }
  
### Item Endpoints
- `GET /api/item/{id}`
  Get an item by its ID

- `GET /api/item/name/{name}`
  Get an item by its name.

## Contact
 - Email: kpourd@gmail.com
 - GitHub Profile: https://github.com/kiannn/E-Commerce

