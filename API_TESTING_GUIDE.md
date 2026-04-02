# API Testing Guide

Quick guide for testing the Finance Dashboard API using cURL commands.

## Prerequisites

Start the application:
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## Test Users

| Username | Password    | Role    |
|----------|-------------|---------|
| admin    | admin123    | ADMIN   |
| analyst  | analyst123  | ANALYST |
| viewer   | viewer123   | VIEWER  |

## Authentication Tests

### 1. Login as Admin
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@example.com",
  "role": "ADMIN"
}
```

### 2. Register New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "password123",
    "role": "VIEWER"
  }'
```

## Dashboard API Tests

### Get Dashboard Summary
```bash
# Replace TOKEN with your JWT token from login
curl -X GET http://localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer TOKEN"
```

**Expected Response:**
```json
{
  "totalIncome": 8000.0,
  "totalExpenses": 2525.0,
  "netBalance": 5475.0,
  "categoryTotals": {
    "Salary": 5000.0,
    "Rent": 1200.0,
    ...
  },
  "recentActivity": [...],
  "monthlyTrends": {
    "2026-03-INCOME": 8000.0,
    "2026-03-EXPENSE": 2525.0
  }
}
```

## Financial Records API Tests

### 1. Get All Records (All Roles)
```bash
curl -X GET http://localhost:8080/api/records \
  -H "Authorization: Bearer TOKEN"
```

### 2. Get Record by ID
```bash
curl -X GET http://localhost:8080/api/records/1 \
  -H "Authorization: Bearer TOKEN"
```

### 3. Create Record (ADMIN/ANALYST Only)
```bash
curl -X POST http://localhost:8080/api/records \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 2500.0,
    "type": "INCOME",
    "category": "Consulting",
    "date": "2026-04-02",
    "notes": "Consulting project payment"
  }'
```

### 4. Update Record (ADMIN/ANALYST Only)
```bash
curl -X PUT http://localhost:8080/api/records/1 \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 5500.0,
    "type": "INCOME",
    "category": "Salary",
    "date": "2026-03-03",
    "notes": "Updated monthly salary"
  }'
```

### 5. Delete Record (ADMIN Only)
```bash
curl -X DELETE http://localhost:8080/api/records/1 \
  -H "Authorization: Bearer TOKEN"
```

### 6. Get Records by Type
```bash
# Get all income records
curl -X GET http://localhost:8080/api/records/type/INCOME \
  -H "Authorization: Bearer TOKEN"

# Get all expense records
curl -X GET http://localhost:8080/api/records/type/EXPENSE \
  -H "Authorization: Bearer TOKEN"
```

### 7. Get Records by Category
```bash
curl -X GET http://localhost:8080/api/records/category/Salary \
  -H "Authorization: Bearer TOKEN"
```

### 8. Get Records by Date Range
```bash
curl -X GET "http://localhost:8080/api/records/date-range?startDate=2026-03-01&endDate=2026-03-31" \
  -H "Authorization: Bearer TOKEN"
```

### 9. Get Recent Records
```bash
curl -X GET http://localhost:8080/api/records/recent \
  -H "Authorization: Bearer TOKEN"
```

## User Management API Tests (ADMIN Only)

### 1. Get All Users
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

### 2. Get User by ID
```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

### 3. Update User
```bash
curl -X PUT http://localhost:8080/api/users/2 \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "role": "ADMIN",
    "active": true
  }'
```

### 4. Delete User
```bash
curl -X DELETE http://localhost:8080/api/users/2 \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

## Access Control Tests

### Test 1: Viewer Cannot Create Records
```bash
# Login as viewer
VIEWER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"viewer","password":"viewer123"}' | \
  python3 -c "import sys, json; print(json.load(sys.stdin)['token'])")

# Try to create a record (should fail with 403)
curl -X POST http://localhost:8080/api/records \
  -H "Authorization: Bearer $VIEWER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.0,
    "type": "INCOME",
    "category": "Test",
    "date": "2026-04-02",
    "notes": "This should fail"
  }'
```

**Expected Response:**
```json
{
  "timestamp": "2026-04-02T...",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied: You don't have permission to access this resource",
  "path": "/api/records"
}
```

### Test 2: Analyst Can Create But Not Delete
```bash
# Login as analyst
ANALYST_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"analyst","password":"analyst123"}' | \
  python3 -c "import sys, json; print(json.load(sys.stdin)['token'])")

# Create a record (should succeed)
curl -X POST http://localhost:8080/api/records \
  -H "Authorization: Bearer $ANALYST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 150.0,
    "type": "EXPENSE",
    "category": "Test",
    "date": "2026-04-02",
    "notes": "This should succeed"
  }'

# Try to delete a record (should fail with 403)
curl -X DELETE http://localhost:8080/api/records/1 \
  -H "Authorization: Bearer $ANALYST_TOKEN"
```

### Test 3: Only Admin Can Manage Users
```bash
# Login as analyst
ANALYST_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"analyst","password":"analyst123"}' | \
  python3 -c "import sys, json; print(json.load(sys.stdin)['token'])")

# Try to get all users (should fail with 403)
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $ANALYST_TOKEN"
```

## Validation Tests

### Test Invalid Input
```bash
# Test with missing required fields
curl -X POST http://localhost:8080/api/records \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.0,
    "type": "INCOME"
  }'
```

**Expected Response:**
```json
{
  "timestamp": "2026-04-02T...",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/records",
  "validationErrors": {
    "category": "Category is required",
    "date": "Date is required"
  }
}
```

### Test Invalid Amount
```bash
curl -X POST http://localhost:8080/api/records \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": -100.0,
    "type": "INCOME",
    "category": "Test",
    "date": "2026-04-02"
  }'
```

## Error Handling Tests

### Test Invalid Credentials
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"wrongpassword"}'
```

**Expected Response:**
```json
{
  "timestamp": "2026-04-02T...",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password",
  "path": "/api/auth/login"
}
```

### Test Non-Existent Resource
```bash
curl -X GET http://localhost:8080/api/records/999 \
  -H "Authorization: Bearer TOKEN"
```

**Expected Response:**
```json
{
  "timestamp": "2026-04-02T...",
  "status": 404,
  "error": "Not Found",
  "message": "Record not found with id: 999",
  "path": "/api/records/999"
}
```

### Test Without Authentication
```bash
curl -X GET http://localhost:8080/api/records
```

**Expected Response:**
```json
{
  "timestamp": "2026-04-02T...",
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication failed: Full authentication is required to access this resource",
  "path": "/api/records"
}
```

## Complete Workflow Example

```bash
# 1. Login as admin
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | \
  python3 -c "import sys, json; print(json.load(sys.stdin)['token'])")

# 2. View dashboard
curl -X GET http://localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer $ADMIN_TOKEN" | python3 -m json.tool

# 3. Create a new record
curl -X POST http://localhost:8080/api/records \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 3000.0,
    "type": "INCOME",
    "category": "Bonus",
    "date": "2026-04-02",
    "notes": "Year-end bonus"
  }' | python3 -m json.tool

# 4. Get all records
curl -X GET http://localhost:8080/api/records \
  -H "Authorization: Bearer $ADMIN_TOKEN" | python3 -m json.tool

# 5. Get updated dashboard
curl -X GET http://localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer $ADMIN_TOKEN" | python3 -m json.tool
```

## H2 Database Console

Access the H2 console at: `http://localhost:8080/h2-console`

- **JDBC URL**: `jdbc:h2:mem:financedb`
- **Username**: `sa`
- **Password**: (leave empty)

You can run SQL queries directly:
```sql
SELECT * FROM users;
SELECT * FROM financial_records;
SELECT COUNT(*) FROM financial_records WHERE type = 'INCOME';
```

## Notes

- All timestamps are in ISO-8601 format
- JWT tokens expire after 24 hours (configurable in application.properties)
- The H2 database is in-memory, so data is lost on application restart
- Sample data is automatically loaded on startup
