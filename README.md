# Finance Dashboard Backend

A comprehensive backend system for managing financial records with role-based access control, built with Spring Boot, Spring Security, JWT authentication, Spring Data JPA, and interactive **Swagger/OpenAPI documentation**.

---

## 📚 Documentation

- **[API_TESTING_EXAMPLES.md](./API_TESTING_EXAMPLES.md)** - API Testing Guide with 8 complete test cases
- **[TESTING_GUIDE.md](./TESTING_GUIDE.md)** - Testing procedures and best practices
- **[PROJECT_SUMMARY.md](./PROJECT_SUMMARY.md)** - Detailed project overview
- **[SWAGGER_DOCUMENTATION.md](./SWAGGER_DOCUMENTATION.md)** - API documentation
- **[SOFT_DELETE_IMPLEMENTATION.md](./SOFT_DELETE_IMPLEMENTATION.md)** - Soft delete implementation details
- **[TESTS_ADDED.md](./TESTS_ADDED.md)** - Test suite information
- **[HELP.md](./HELP.md)** - Help and support

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Access Control](#access-control)
- [Database Schema](#database-schema)
- [Testing](#testing)
- [Design Decisions](#design-decisions)

## Overview

This project implements a Finance Dashboard backend system that allows users with different roles to interact with financial records. The system provides secure authentication, role-based authorization, and comprehensive financial data management capabilities.

## Features

### 1. User and Role Management
- User registration and authentication using JWT tokens
- Three distinct user roles:
  - **VIEWER**: Read-only access to dashboard data
  - **ANALYST**: View and create financial records, access analytics
  - **ADMIN**: Full access including user management and record deletion
- User status management (active/inactive)
- Secure password encryption using BCrypt

### 2. Financial Records Management
- Create, read, update, and delete financial records
- Record attributes:
  - Amount (BigDecimal for precision)
  - Type (INCOME/EXPENSE)
  - Category
  - Date
  - Notes
  - Audit fields (created by, created at, updated at)
- Filter records by:
  - Transaction type
  - Category
  - Date range
- View recent activity

### 3. Dashboard Summary APIs
- Total income calculation
- Total expenses calculation
- Net balance computation
- Category-wise totals
- Monthly trends analysis
- Recent activity feed

### 4. Access Control
- JWT-based stateless authentication
- Method-level security using `@PreAuthorize` annotations
- Role-based permissions enforced at the service and controller layers
- Comprehensive error handling for unauthorized access

### 5. Validation and Error Handling
- Input validation using Jakarta Bean Validation
- Custom exception handling with meaningful error messages
- Standardized error response format
- HTTP status codes used appropriately

### 6. Data Persistence
- H2 in-memory database for development and testing
- JPA/Hibernate for object-relational mapping
- Transactional integrity
- Automatic schema generation
- Sample data initialization for testing

## Technology Stack

- **Java 21**: Latest LTS version
- **Spring Boot 4.0.5**: Application framework
- **Spring Security**: Authentication and authorization
- **JWT (JJWT 0.12.6)**: Token-based authentication
- **Spring Data JPA**: Data persistence
- **H2 Database**: In-memory database
- **Lombok**: Boilerplate code reduction
- **Maven**: Build and dependency management

## Architecture

The project follows a layered architecture:

```
├── Controller Layer (REST endpoints)
├── Service Layer (Business logic)
├── Repository Layer (Data access)
├── Model Layer (Domain entities)
├── DTO Layer (Data transfer objects)
├── Security Layer (Authentication & Authorization)
└── Exception Layer (Error handling)
```

### Package Structure

```
io.zorvyn.task.financedashboard
├── config/                 # Configuration classes
├── controller/             # REST controllers
├── dto/                    # Data Transfer Objects
├── exception/              # Custom exceptions and handlers
├── model/                  # JPA entities
├── repository/             # Data repositories
├── security/               # Security configuration and JWT utilities
└── service/                # Business logic services
```

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.8 or higher

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd finance-dashboard
```

2. Build the project:
```bash
./mvnw clean install
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### Accessing the Application

- **API Base URL**: `http://localhost:8080/api`
- **Swagger UI (Interactive API Docs)**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Specification**: `http://localhost:8080/v3/api-docs`
- **H2 Database Console**: `http://localhost:8080/h2-console`

### Default Test Users

Three test users are automatically created on startup:

| Username | Password    | Role    |
|----------|-------------|---------|
| admin    | admin123    | ADMIN   |
| analyst  | analyst123  | ANALYST |
| viewer   | viewer123   | VIEWER  |

### H2 Console Access

Access the H2 database console at: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:financedb`
- Username: `sa`
- Password: (leave empty)

## API Documentation

### Interactive API Documentation (Swagger UI)

**The easiest way to explore and test the API is through Swagger UI:**

🔗 **Open**: `http://localhost:8080/swagger-ui.html`

Swagger UI provides:
- ✅ **Interactive API testing** - Test endpoints directly from your browser
- ✅ **Complete documentation** - All endpoints, parameters, and responses
- ✅ **JWT Authentication** - Built-in token management
- ✅ **Request/Response examples** - See exactly what to send and expect
- ✅ **Try it out** - Execute real API calls with one click

**Quick Start with Swagger:**
1. Run the application
2. Open `http://localhost:8080/swagger-ui.html`
3. Use `POST /api/auth/login` with credentials: `admin` / `admin123`
4. Click **"Authorize"** button (🔓) and enter: `Bearer <your-token>`
5. Now you can test all secured endpoints!

📖 **For detailed Swagger documentation, see**: [SWAGGER_DOCUMENTATION.md](SWAGGER_DOCUMENTATION.md)

---

### Manual API Testing (cURL Examples)

If you prefer command-line testing, here are some cURL examples:

#### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "role": "VIEWER"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "role": "VIEWER"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response:** Same as registration response

### User Management Endpoints (ADMIN only)

#### Get All Users
```http
GET /api/users
Authorization: Bearer <token>
```

#### Get User by ID
```http
GET /api/users/{id}
Authorization: Bearer <token>
```

#### Update User
```http
PUT /api/users/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "newusername",
  "email": "newemail@example.com",
  "role": "ANALYST",
  "active": true
}
```

#### Delete User
```http
DELETE /api/users/{id}
Authorization: Bearer <token>
```

### Financial Records Endpoints

#### Create Record (ADMIN, ANALYST)
```http
POST /api/records
Authorization: Bearer <token>
Content-Type: application/json

{
  "amount": 1500.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2024-01-15",
  "notes": "Monthly salary payment"
}
```

#### Get All Records (All authenticated users)
```http
GET /api/records
Authorization: Bearer <token>
```

#### Get Record by ID
```http
GET /api/records/{id}
Authorization: Bearer <token>
```

#### Get Records by Type
```http
GET /api/records/type/{INCOME|EXPENSE}
Authorization: Bearer <token>
```

#### Get Records by Category
```http
GET /api/records/category/{category}
Authorization: Bearer <token>
```

#### Get Records by Date Range
```http
GET /api/records/date-range?startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer <token>
```

#### Get Recent Records
```http
GET /api/records/recent
Authorization: Bearer <token>
```

#### Update Record (ADMIN, ANALYST)
```http
PUT /api/records/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "amount": 1600.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2024-01-15",
  "notes": "Updated salary payment"
}
```

#### Delete Record (ADMIN only)
```http
DELETE /api/records/{id}
Authorization: Bearer <token>
```

### Dashboard Endpoints

#### Get Dashboard Summary (All authenticated users)
```http
GET /api/dashboard/summary
Authorization: Bearer <token>
```

**Response:**
```json
{
  "totalIncome": 8000.00,
  "totalExpenses": 2525.00,
  "netBalance": 5475.00,
  "categoryTotals": {
    "Salary": 5000.00,
    "Freelance": 2000.00,
    "Investment": 1000.00,
    "Rent": 1200.00,
    "Groceries": 300.00,
    "Utilities": 150.00,
    "Entertainment": 75.00,
    "Healthcare": 500.00,
    "Transportation": 200.00,
    "Education": 100.00
  },
  "monthlyTrends": {
    "2024-01-INCOME": 8000.00,
    "2024-01-EXPENSE": 2525.00
  },
  "recentActivity": [...]
}
```

## Access Control

### Role-Based Permissions Matrix

| Action | VIEWER | ANALYST | ADMIN |
|--------|--------|---------|-------|
| View Dashboard Summary | ✓ | ✓ | ✓ |
| View Financial Records | ✓ | ✓ | ✓ |
| Create Financial Records | ✗ | ✓ | ✓ |
| Update Financial Records | ✗ | ✓ | ✓ |
| Delete Financial Records | ✗ | ✗ | ✓ |
| View Users | ✗ | ✗ | ✓ |
| Manage Users | ✗ | ✗ | ✓ |

### Implementation

Access control is implemented using:
1. **JWT Authentication**: Stateless token-based authentication
2. **Spring Security**: Security filter chain configuration
3. **Method Security**: `@PreAuthorize` annotations on controller methods
4. **Custom Filters**: JWT authentication filter for token validation

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### Financial Records Table
```sql
CREATE TABLE financial_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    amount DECIMAL(19,2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    category VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    notes VARCHAR(500),
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id)
);
```

## Testing

### Manual Testing with cURL

1. **Login as Admin:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

2. **Get Dashboard Summary:**
```bash
curl -X GET http://localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer <token>"
```

3. **Create Financial Record:**
```bash
curl -X POST http://localhost:8080/api/records \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 2000.00,
    "type": "INCOME",
    "category": "Bonus",
    "date": "2024-01-20",
    "notes": "Year-end bonus"
  }'
```

### Testing Access Control

1. Login as VIEWER
2. Try to create a record (should fail with 403 Forbidden)
3. Try to view records (should succeed)

## Design Decisions

### 1. Technology Choices

**Spring Boot 4.0.5**: Latest stable version providing modern features and security updates.

**JWT for Authentication**: Chosen for stateless authentication, scalability, and mobile-friendly architecture.

**H2 Database**: In-memory database for simplified setup and testing. Can be easily replaced with PostgreSQL or MySQL for production.

**BCrypt Password Encoding**: Industry-standard password hashing algorithm.

### 2. Architectural Decisions

**Layered Architecture**: Clear separation of concerns with distinct layers for presentation, business logic, and data access.

**DTO Pattern**: Separation between internal domain models and external API contracts, providing flexibility and security.

**Repository Pattern**: Abstraction of data access logic, making it easy to change data sources.

**Global Exception Handling**: Centralized error handling with consistent error response format.

### 3. Security Decisions

**Method-Level Security**: Fine-grained access control at the method level using `@PreAuthorize`.

**Role-Based Access Control (RBAC)**: Simple and effective authorization model suitable for the use case.

**Stateless Authentication**: JWT tokens eliminate the need for server-side session storage.

### 4. Data Modeling Decisions

**BigDecimal for Amounts**: Ensures precision in financial calculations, avoiding floating-point errors.

**Audit Fields**: Tracking creation and update timestamps for accountability.

**Soft Delete Consideration**: While not implemented, the `active` flag on users demonstrates consideration for soft deletes.

### 5. API Design Decisions

**RESTful Design**: Standard HTTP methods and status codes for intuitive API usage.

**Consistent Response Format**: Standardized error responses and success responses.

**Validation at Multiple Levels**: Input validation at DTO level, business validation at service level.

### Assumptions and Trade-offs

**Assumptions:**
- Single-tenant application (no multi-tenancy support)
- Financial records are not meant to be soft-deleted (hard delete is used)
- Users can view all financial records regardless of who created them
- Categories are free-text rather than predefined enums for flexibility

**Trade-offs:**
- H2 in-memory database: Simple setup but data is lost on restart (easily configurable to use persistent database)
- No pagination: Implemented basic list endpoints; pagination can be added using Spring Data's Pageable
- Basic JWT implementation: No refresh tokens or token revocation (can be enhanced for production)
- No audit logging: Could be added using Spring AOP or database triggers

## Future Enhancements

1. **Pagination and Sorting**: Add pagination support for large datasets
2. **Advanced Filtering**: More sophisticated query capabilities
3. **File Uploads**: Support for receipt/invoice attachments
4. **Export Functionality**: Export financial data to CSV/PDF
5. **Budgeting Features**: Budget tracking and alerts
6. **Multi-currency Support**: Handle multiple currencies
7. **Recurring Transactions**: Support for recurring income/expenses
8. **Reporting**: Advanced analytics and reporting features
9. **Email Notifications**: Alerts and notifications via email
10. **API Rate Limiting**: Prevent abuse

## License

This project is developed as an assessment submission.

## Contact

For questions or clarifications, please contact the development team.
