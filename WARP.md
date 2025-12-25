# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

MedCashFlow is a Spring Boot-based financial management system for medical clinics. The application manages bills, installments, employees, and account planning with a multi-tenant architecture where each clinic operates independently.

## Development Commands

### Build and Run
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Clean build artifacts
./gradlew clean
```

### Database Management
```bash
# Start development database (PostgreSQL on port 5432)
docker compose --profile dev up -d

# Start test database (PostgreSQL on port 5433)
docker compose --profile test up -d

# Stop databases
docker compose --profile dev down
docker compose --profile test down
```

### Testing
```bash
# Run all tests (requires test database running)
./gradlew test

# Run a specific test class
./gradlew test --tests "example.medCashFlow.BillControllerTests"

# Run a specific test method
./gradlew test --tests "example.medCashFlow.BillControllerTests.whenAllowedEmployeeCreateBill_thenSucceeds"

# Run tests with verbose output
./gradlew test --info
```

## Architecture

### Security & Authentication

**Multi-level Authentication System:**
- **Admin Authentication**: In-memory authentication using credentials from `.env` (ADMIN_USERNAME, ADMIN_PASSWORD). Admin users have limited scope - they manage clinics but not clinic-specific resources.
- **Database Authentication**: Custom `DatabaseAuthenticationProvider` for employee authentication. Employees belong to clinics and have role-based access.
- **JWT Tokens**: Generated via `TokenService` using Auth0's java-jwt library. Tokens expire after 2 hours (timezone: GMT-3).
- **Security Filter**: `SecurityFilter` intercepts requests before `UsernamePasswordAuthenticationFilter` to validate JWT tokens.

**Role Hierarchy** (defined in `Employee.getAuthorities()`):
- `MANAGER`: Inherits all permissions from FINANCIAL_ANALYST and DOCTOR
- `FINANCIAL_ANALYST`: Inherits all permissions from DOCTOR
- `DOCTOR`: Base level permissions

### Multi-tenant Architecture

The system is multi-tenant at the clinic level:
- `Clinic` entity uses UUID as primary key
- Most entities (Bill, AccountPlanning, Involved, Employee) have a `clinic_id` foreign key
- Authentication automatically scopes operations to the logged-in employee's clinic
- Soft deletes via `isActive` flag on Clinic, Employee, and Involved entities

### Core Domain Model

**Financial Flow:**
```
Bill (parent entity)
├── name, pricing, type (INCOME/OUTCOME)
├── dueDate, installmentsAmount
├── References: Employee, Clinic, Involved, AccountPlanning, PaymentMethod
└── Installments (child entities)
    └── Each has: installmentNumber, pricing, dueDate, isPaid
```

**Key Relationships:**
- Bill → Employee (who created it)
- Bill → Clinic (which clinic it belongs to)
- Bill → Involved (third party: supplier/client)
- Bill → AccountPlanning (optional categorization)
- Bill → PaymentMethod (how it's paid)
- Bill → Installments (one-to-many, generated based on installmentsAmount)

### Data Layer

**Flyway Migrations** (`src/main/resources/db/migration`):
- Migrations run automatically on application startup
- Naming: `V{number}__{description}.sql`
- Includes data initialization (roles, payment methods)

**MapStruct for DTOs:**
- Mappers in `src/main/java/example/medCashFlow/mappers/`
- Automatically handle entity ↔ DTO conversions
- Uses Spring component model (`componentModel = "spring"`)
- Example: `BillMapper` handles complex mappings including enum conversion and nested object mapping

### Controller-Service-Repository Pattern

Standard layering:
- **Controllers** (`controller/`): Handle HTTP requests, validate authorization via `@PreAuthorize` or SecurityConfiguration
- **Services** (`services/`): Business logic and transaction management
- **Repositories** (`repository/`): Spring Data JPA repositories extending `JpaRepository`
- **DTOs** (`dto/`): Separate request/response objects organized by domain

### Testing Strategy

**Test Infrastructure** (`MedCashFlowApplicationTests`):
- Uses `@SpringBootTest` with `@AutoConfigureMockMvc`
- Active profiles: `test` and `docker-test`
- Database reset before each test: `DROP SCHEMA public CASCADE; CREATE SCHEMA public;`
- Flyway re-migration on every test
- Pre-creates test data: clinics, employees (manager, financial analyst, doctor), bills, involveds
- Generates JWT tokens for different roles (adminToken, managerToken, financialAnalystToken, doctorToken)

**Test Database:**
- Uses PostgreSQL on port 5433 (vs dev on 5432)
- Configured via `application-test.properties`
- Docker container: `test-db` profile

## Important Technical Details

### Environment Variables
Required in `.env` file (see `.env.example`):
- `SECRET_KEY`: JWT signing secret
- `ADMIN_USERNAME`: Admin username
- `ADMIN_PASSWORD`: Admin password

### CORS Configuration
Handled by `CorsConfiguration` class in `infra/security/`

### Soft Deletes
When "deleting" clinics, employees, or involveds, set `isActive = false` instead of actual deletion. Endpoints follow pattern: `PUT /{resource}/activate/{id}` and `DELETE /{resource}/delete/{id}`

### Date/Time Handling
- Uses `LocalDateTime` throughout
- Jackson configured with `JavaTimeModule` and `WRITE_DATES_AS_TIMESTAMPS = false`
- Timezone for JWT expiration: GMT-3 (Brazil)

### Bill & Installment Creation
When creating a Bill, the service automatically generates the corresponding Installment entities based on `installmentsAmount`. Each installment has its own due date calculated from the Bill's due date.

## Code Style & Patterns

### Lombok Usage
Extensive use of Lombok annotations:
- `@Data`: Generates getters, setters, toString, equals, hashCode
- `@NoArgsConstructor`, `@AllArgsConstructor`: Constructor generation
- `@RequiredArgsConstructor`: For Spring dependency injection
- `@EqualsAndHashCode(of = "id")`: Custom equals/hashCode based on ID only

### Exception Handling
Custom exceptions in `exceptions/` package (e.g., `ClinicNotFoundException`)

### Package Structure
```
example.medCashFlow/
├── controller/          # REST endpoints
├── dto/                 # Data transfer objects (organized by domain)
├── exceptions/          # Custom exceptions
├── infra/
│   └── security/       # Security configuration
├── mappers/            # MapStruct interfaces
├── model/              # JPA entities
├── repository/         # Spring Data repositories
└── services/           # Business logic
```
