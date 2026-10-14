[Français](README.md) | [English](README.en.md)

# Users API - User Management Microservice

Spring Boot application dedicated to user management for the Square Games platform.

---

## Features

- **RESTful CRUD API** for users (`/users`).
- **Validation Endpoint** `/users/{id}/valid` enabling downstream services (such as the Game API) to verify player identity and existence.
- **Multi-tier Architecture**: Controller -> Service -> DAO -> JPA Repository.
- **Persistence**:
  - `h2` profile by default (file-based persistent database in `./data/usersdb`).
  - `mysql` profile configurable via `application-mysql.properties`.
- **Integrated OpenAPI / Swagger** documentation.

---

## Endpoints

| Method | URL | Description | Success Status |
|---|---|---|---|
| `POST` | `/users` | Create user (`username`, `email`) | `201 Created` |
| `GET` | `/users/{id}` | Retrieve user details | `200 OK` |
| `DELETE` | `/users/{id}` | Delete user | `204 No Content` |
| `GET` | `/users/{id}/valid` | Verify whether a user ID exists | `200 OK` (`true`) or `404 Not Found` |
| `GET` | `/users` | List all users | `200 OK` |

---

## Getting Started

### Prerequisites
- Java 21 or higher

### Launch with H2 (default on port 8081)
```bash
./mvnw spring-boot:run
```

The application starts on port `8081`:
- API: `http://localhost:8081/users`
- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- H2 Console: `http://localhost:8081/h2-console` (JDBC URL: `jdbc:h2:file:./data/usersdb`, User: `sa`, Password: empty)

### Launch with MySQL Profile
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

### Run Tests
```bash
./mvnw test
```
