# SkillSync API

Spring Boot 3 RESTful API with JWT authentication, PostgreSQL, Redis, and Swagger documentation.

## Features

- Spring Boot 3
- Spring Security with JWT Authentication
- Spring Data JPA with PostgreSQL
- Redis for caching
- OpenAPI/Swagger Documentation
- Proper project structure following best practices

## Prerequisites

- Java 17 or higher
- Maven
- PostgreSQL
- Redis

## Setup

1. Clone the repository
2. Configure your PostgreSQL database in `application.properties`
3. Configure your Redis connection in `application.properties`
4. Update the JWT secret in `application.properties`

## Running the Application

```bash
mvn clean install
mvn spring-boot:run
```

The application will start on port 8080.

## API Documentation

Once the application is running, you can access the Swagger UI at:
http://localhost:8080/swagger-ui.html

## Endpoints

### Authentication

- POST `/api/v1/auth/register` - Register a new user
- POST `/api/v1/auth/login` - Authenticate a user

### Health Check

- GET `/health` - Check application health

## Project Structure

- `controller`: REST API controllers
- `service`: Business logic
- `repository`: Data access layer
- `model`: Entity classes
- `dto`: Data Transfer Objects
- `security`: Security configuration and JWT implementation
- `config`: Configuration classes

## Security

The application uses JWT tokens for authentication. Tokens are valid for 24 hours by default.

## Database

The application uses PostgreSQL for data storage. The schema is automatically created when the application starts using JPA/Hibernate. 