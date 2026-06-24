# Job Tracker API (Spring Boot)

A REST API for tracking job applications with JWT authentication and dynamic RBAC (roles + privileges).

## Tech Stack
- Java 21
- Spring Boot 3.5.x
- Spring Security 6
- Spring Data JPA (Hibernate)
- Flyway (DB migrations)
- MySQL
- JWT (jwt)

## Features Implemented
- Database schema via Flyway migration (`V1__init_rbac_schema.sql`)
- RBAC tables:
    - `users`
    - `roles`
    - `privileges`
    - `user_roles`
    - `role_privileges`
    - `job_applications`
- Auth:
    - `POST /api/auth/register`
    - `POST /api/auth/login`
- JWT-based stateless security
- Protected test endpoint:
    - `GET /api/me`
- Method security enabled (`@EnableMethodSecurity`)
- Optimized auth loading with `@EntityGraph` for roles/privileges

## Project Structure (high level)
- `auth/` → authentication logic and DTOs
- `security/` (or `filter/`) → JWT service + JWT filter + user details service
- `config/` → Spring Security config
- `user/` → entities (`User`, `Role`, `Privilege`)
- `repository/` → JPA repositories
- `resources/db/migration/` → Flyway SQL migrations

## Configuration

Create your own local config file.

`src/main/resources/application.yml` (example):
```yaml
jwt:
  secret: ${JWT_SECRET}
  expiration-ms: 3600000

spring:
  profiles:
    active: ${ACTIVE_PROFILE:dev}
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true
    open-in-view: false

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

  application:
    name: job-tracker

logging:
  level:
    org.flywaydb: DEBUG
```

### Environment variables
- `JWT_SECRET` (Base64, at least 32-byte key)
- `DB_USERNAME`
- `DB_PASSWORD`
- `DB_URL` (e.g., `jdbc:mysql://localhost:3306/job_tracker_db`)
- `ACTIVE_PROFILE` (e.g., `dev`)

Generate JWT secret:
```bash
openssl rand -base64 32
```

## Run Locally

1. Start MySQL
2. Create env vars
3. Run:

```bash
./mvnw clean spring-boot:run
```

Flyway will run migrations automatically.

## API Test

### Register
![Register Screen](src/main/resources/images/app-register.png)

### Login
![Login Screen](src/main/resources/images/app-login.png)

### Me (protected)
![Me Screen](src/main/resources/images/app-me.png)