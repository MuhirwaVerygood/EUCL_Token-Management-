# EUCL Token Management System

A Spring Boot application for managing electricity tokens for EUCL (Electricity Utility Company).

## Features

### User Management
- User registration and authentication
- JWT-based authentication
- Role-based access control (Admin and Customer)
- Complete CRUD operations for users

### Token Management
- Purchase electricity tokens
- Validate tokens
- View token history
- Complete CRUD operations for tokens

### Meter Management
- Register meters
- Associate meters with users
- Complete CRUD operations for meters

## Technologies Used

- Spring Boot 3.5.0
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Docker and Docker Compose
- Nginx for Load Balancing

## Running with Docker

The application is containerized and can be run using Docker Compose, which sets up:
- PostgreSQL database
- Multiple application instances
- Nginx load balancer

### Prerequisites

- Docker and Docker Compose installed

### Steps to Run

1. Clone the repository
2. Navigate to the project directory
3. Run the following command:

```bash
docker-compose up -d
```

This will start:
- PostgreSQL database on port 5432
- Two application instances
- Nginx load balancer on port 80

### Accessing the Application

The application can be accessed at:
- http://localhost (through Nginx load balancer)
- http://localhost:8091 (direct access to instance 1)
- http://localhost:8092 (direct access to instance 2)

## API Documentation

The API documentation is available at:
- http://localhost/swagger-ui.html

## Environment Variables

The following environment variables can be configured in the docker-compose.yml file:
- SPRING_DATASOURCE_URL: Database URL
- SPRING_DATASOURCE_USERNAME: Database username
- SPRING_DATASOURCE_PASSWORD: Database password
- SERVER_PORT: Application port