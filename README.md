# Online Shop API

> REST API for an online store built with Spring Boot 3 and Java 17. A portfolio project showcasing clean architecture, professional engineering practices, and conscious technical decisions.

![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker)

---

## Features

- Clean layered architecture (Controller → Service → Repository)
- Complete separation of concerns – no JPA entities exposed outside the service layer
- DTOs implemented as Java Records
- Mapping with MapStruct
- Database schema management with Flyway
- Integration tests with Testcontainers (real PostgreSQL, no H2)
- Price and product data snapshots in orders (historical consistency)
- Guest checkout support
- Global exception handling using RFC 7807 (Problem Detail)
- Spring Boot Actuator with health, info and metrics
- Comprehensive API documentation with Swagger UI
- Well-structured unit and integration tests

---

## Technologies

- Java 17
- Spring Boot 3.5
- Spring Data JPA & Hibernate 6
- PostgreSQL 16
- Flyway
- MapStruct
- Lombok
- Testcontainers
- Swagger / OpenAPI 3
- Docker & Docker Compose

---

## How to Run

```bash
# 1. Clone the repository
git clone https://github.com/marand85/online-shop.git
cd online-shop

# 2. Start the application with Docker Compose (PostgreSQL + Backend)
cd infra
docker compose up -d
```

The application will be available at:  
**http://localhost:8080**

Interactive API documentation:  
**http://localhost:8080/swagger-ui.html**

## API Endpoints

| Method | Endpoint                        | Description                                      |
|--------|---------------------------------|--------------------------------------------------|
| GET    | `/api/categories`               | Get all categories                               |
| GET    | `/api/products`                 | Get products (paginated, filtered, searchable)  |
| GET    | `/api/products/{id}`            | Get product details                              |
| POST   | `/api/orders`                   | Create new order (guest checkout)                |
| GET    | `/api/orders/{orderNumber}`     | Get order by order number                        |

## Testing

The project includes both unit and integration tests:

- **Unit tests** – `OrderServiceTest`
- **Integration tests** – `OrderControllerIT`, `CategoryRepositoryIT` (using Testcontainers with real PostgreSQL)

Run tests with:
```bash
./mvnw test
```

## Architecture Highlights

- Strict separation between domain models and API contracts
- No `@Data` on JPA entities (conscious decision to avoid `equals()`/`hashCode()` pitfalls)
- `open-in-view: false` to prevent lazy loading issues
- Price snapshots in order items to ensure historical consistency
- Deterministic test data with explicit IDs

## Author

**Mariusz Andrzejewski**

- GitHub: [https://github.com/marand85](https://github.com/marand85)

---

This project is part of a **three-project portfolio**, each demonstrating different skills:

- **This project** – Clean REST API architecture and testing
- Blog – Spring MVC + Thymeleaf (SSR)
- Banking System – Event-driven architecture with Kafka and microservices