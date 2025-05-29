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

## System Architecture

The system architecture is documented in the following diagrams:
- [Data Flow Diagram (Text Version)](diagrams/data_flow_diagram.txt) - Shows how data flows through the system
- [System Architecture Diagram (Text Version)](diagrams/system_architecture_diagram.txt) - Shows the components and their relationships

### Visual Diagrams with Icons

Enhanced diagrams with visual icons are available in PlantUML format:
- [Data Flow Diagram with Icons](diagrams/data_flow_diagram.puml) - Shows how data flows through the system with visual icons
- [System Architecture Diagram with Icons](diagrams/system_architecture_diagram.puml) - Shows the components and their relationships with visual icons

#### Rendered Diagrams

**System Architecture Diagram:**

![System Architecture Diagram](https://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/username/Eucl_token_management/main/diagrams/system_architecture_diagram.puml)

**Data Flow Diagram:**

![Data Flow Diagram](https://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/username/Eucl_token_management/main/diagrams/data_flow_diagram.puml)

> Note: The rendered diagrams above will be visible once the repository is pushed to GitHub. If you're viewing this locally or the diagrams aren't visible, you can use one of the methods below.

#### How to View PlantUML Diagrams

1. **Use the included HTML viewer**: Open [diagrams/view_diagrams.html](diagrams/view_diagrams.html) in your web browser to see the diagrams rendered with icons
2. Use an online PlantUML renderer like [PlantUML Web Server](http://www.plantuml.com/plantuml/uml/)
3. Copy the content of the .puml file and paste it into the renderer
4. Or use a PlantUML plugin for your IDE (available for VS Code, IntelliJ, etc.)
5. Or install PlantUML locally (requires Java and Graphviz)

## Environment Variables

The following environment variables can be configured in the docker-compose.yml file:
- SPRING_DATASOURCE_URL: Database URL
- SPRING_DATASOURCE_USERNAME: Database username
- SPRING_DATASOURCE_PASSWORD: Database password
- SERVER_PORT: Application port
