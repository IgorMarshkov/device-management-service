# Device Management Service

REST API for managing device resources built with Spring Boot 3, Java 21, and PostgreSQL.

## Features

- **Complete CRUD Operations**: Create, read, update, and delete devices
- **Advanced Filtering**: Filter devices by brand and/or state
- **Domain Validations**: Business rule enforcement (e.g., in-use devices cannot be deleted)
- **API Documentation**: Interactive Swagger UI documentation
- **Database Integration**: PostgreSQL with Flyway migrations
- **Containerization**: Docker and Docker Compose support
- **Comprehensive Testing**: Unit and integration tests with 80%+ coverage
- **Production Ready**: Health checks, logging, error handling, and monitoring endpoints

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker and Docker Compose (for containerized deployment)

### Running with Docker Compose (Recommended)

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd device-management-service
   ```

2. Start the application and database:
   ```bash
   docker-compose up --build
   ```

3. The API will be available at:
    - **API Base URL**: http://localhost:8080/api/v1/devices
    - **Swagger UI**: http://localhost:8080/swagger-ui.html
    - **API Docs**: http://localhost:8080/api-docs
    - **Health Check**: http://localhost:8080/actuator/health

### Running Locally

1. Start PostgreSQL database:
   ```bash
   docker run -d \
     --name postgres \
     -e POSTGRES_DB=devices_db \
     -e POSTGRES_USER=devices_user \
     -e POSTGRES_PASSWORD=devices_pass \
     -p 5432:5432 \
     postgres:15-alpine
   ```

2. Build and run the application:
   ```bash
   mvn clean package
   mvn spring-boot:run
   ```

## API Documentation

### Device Domain Model

```json
{
  "id": 1,
  "name": "iPhone 15 Pro",
  "brand": "Apple",
  "state": "AVAILABLE",
  "creationTime": "2024-01-15T10:30:00"
}
```

### Device States

- `AVAILABLE`: Device is ready to be used
- `IN_USE`: Device is currently being used
- `INACTIVE`: Device is not available for use

### Endpoints

Use http://localhost:8080/swagger-ui.html to access the API documentation with a list of all endpoints and their details.

### Query Parameters

- `brand`: Filter devices by brand (case-insensitive)
- `state`: Filter devices by state (`AVAILABLE`, `IN_USE`, `INACTIVE`)

## Business Rules & Validations

1. **Creation Time**: Cannot be updated after device creation
2. **In-Use Restrictions**:
    - Name and brand cannot be updated when a device is in use
    - In-use devices cannot be deleted
3. **Required Fields**: Name, brand are mandatory for creation. A new device will be created in `AVAILABLE` state by default.
4. **State Transitions**: All state transitions are allowed via updates

## Architecture

### Project Structure
```
├── controller/     # REST controllers
├── service/        # Business logic layer
├── repository/     # Data access layer
├── entity/         # JPA entities
├── dto/           # Data Transfer Objects
├── mapper/        # MapStruct mappers
├── config/        # Configuration classes
├── exception/     # Custom exceptions
└── enums/         # Enumerations
```

## Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn test jacoco:report
```

### Test Coverage
The project maintains 80%+ test coverage across:
- Unit tests for service layer business logic
- Unit tests for DTO-entity conversion
- Integration tests for a repository layer
- Web layer tests for controllers


## Database

### Schema
The application uses PostgreSQL with Flyway for database migrations:

```sql
CREATE TABLE device (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    state VARCHAR(50) NOT NULL CHECK (state IN ('AVAILABLE', 'IN_USE', 'INACTIVE')),
    creation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Indexes
- `idx_device_lower_brand`: For brand-based filtering
- `idx_device_state`: For state-based filtering
- `idx_device_creation_time`: For time-based queries


## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | localhost | Database host |
| `DB_PORT` | 5432 | Database port |
| `DB_NAME` | devices_db | Database name |
| `DB_USERNAME` | devices_user | Database username |
| `DB_PASSWORD` | devices_pass | Database password |
| `SERVER_PORT` | 8080 | Application port |

### Profiles
- `default`: Standard configuration
- `test`: Test-specific configuration

## Monitoring & Operations

### Health Checks
- **Application**: `/actuator/health`
- **Database**: Automatic connection health checks
- **Docker**: Built-in container health checks

### Logging
- Structured logging with timestamps
- Configurable log levels
- Request/response logging for debugging

### Metrics
- Spring Boot Actuator metrics available at `/actuator/metrics`
- JVM metrics, HTTP request metrics, database connection metrics

## Security Considerations

- Input validation on all endpoints
- SQL injection prevention through JPA/Hibernate
- Docker security with non-root user
- Health check endpoints secured

## Future Improvements

1. **Authentication & Authorization**: JWT-based security
2. **UUID-based device IDs**: Use UUID for device IDs
3. **Duplicated Device Check**: Prevent duplicate devices
4. **Rate Limiting**: API rate limiting and throttling
5. **Caching**: Caching for frequently accessed devices
6. **Event Sourcing**: Device states change events
7. **Search Enhancement**: Full-text search capabilities
8. **Audit Trail**: Track all device changes
9. **Bulk Operations**: Batch create/update operations
10. **Advanced Filtering**: Complex query combinations
11. **Role-Based Access Control**: Role-based access control for endpoints
12. **Multi-Tenant Support**: Support for multiple tenants

## Known Limitations

1. **Combined Filters**: Brand and state filtering combination needs optimization
2. **Soft Delete**: Hard deletes may not be suitable for audit requirements
3. **Concurrency**: No optimistic locking for concurrent updates

## Development Setup

1. Install Java 21 and Maven 3.9+
2. Start PostgreSQL database
3. Run `mvn spring-boot:run`
4. Access Swagger UI at http://localhost:8080/swagger-ui.html