# Finance Dashboard - Test Suite Documentation

## Overview

This document describes the comprehensive test suite added to the Finance Dashboard application. The test suite includes both unit tests and integration tests covering all major components of the application.

## Test Structure

### Directory Layout
```
src/test/java/io/zorvyn/task/financedashboard/
├── BaseIT.java                          # Base integration test class
├── TestContainersConfig.java            # Testcontainers configuration
└── controller/
│   ├── AuthControllerIT.java           # Auth endpoint integration tests
│   ├── UserControllerIT.java           # User management integration tests
│   └── FinancialRecordControllerIT.java # Financial record integration tests
└── service/
    ├── AuthServiceTest.java             # Auth service unit tests
    ├── UserServiceTest.java             # User service unit tests
    └── FinancialRecordServiceTest.java  # Financial record service unit tests

src/test/resources/
└── application-test.properties          # Test configuration
```

## Test Configuration

### Dependencies Added
The following test dependencies were added to `pom.xml`:

- **spring-boot-starter-test**: Core Spring Boot testing support
- **spring-security-test**: Spring Security testing utilities
- **spring-boot-testcontainers**: Testcontainers integration with Spring Boot
- **testcontainers-junit-jupiter**: Testcontainers JUnit 5 support
- **mockito-core**: Mocking framework for unit tests
- **mockito-junit-jupiter**: Mockito JUnit 5 integration

### Test Profile
A dedicated test profile is configured in `application-test.properties` with H2 in-memory database.

## Integration Tests

Integration tests use the `BaseIT` abstract class which provides:

- MockMvc instance for testing HTTP endpoints
- ObjectMapper for JSON serialization/deserialization
- Repository access for test data setup
- Password encoder for creating test users
- Authentication token generation helpers

### Test User Credentials
Four predefined test users with different roles:

1. **Admin**: admin / admin@example.com / AdminPass123!
2. **Regular User**: user / user@example.com / UserPass123!
3. **Analyst**: analyst / analyst@example.com / AnalystPass123!
4. **Viewer**: viewer / viewer@example.com / ViewerPass123!

### AuthControllerIT
Tests authentication endpoints:
- ✅ User registration with valid data
- ✅ Registration with duplicate username
- ✅ Registration with duplicate email
- ✅ User login with valid credentials
- ✅ Login with invalid username
- ✅ Login with invalid password
- ✅ Validation of required fields

**Test Count**: 7 tests

### UserControllerIT
Tests user management endpoints (Admin only):
- ✅ Get all users (Admin access)
- ✅ Get users without authentication
- ✅ Prevent non-admin users from accessing user management
- ✅ Get user by ID
- ✅ Retrieve non-existent user (404 error)
- ✅ Update user details
- ✅ Delete user
- ✅ Prevent duplicate usernames on update
- ✅ Prevent duplicate emails on update

**Test Count**: 9 tests

### FinancialRecordControllerIT
Tests financial record CRUD operations:
- ✅ Create financial record as Analyst
- ✅ Create financial record as Admin
- ✅ Prevent Viewer from creating records (403 error)
- ✅ Require authentication for creation
- ✅ Validate record amounts (must be positive)
- ✅ Retrieve all financial records
- ✅ Filter records by transaction type (INCOME/EXPENSE)
- ✅ Filter records by category
- ✅ Update financial record
- ✅ Delete financial record
- ✅ Verify deleted record is not retrievable

**Test Count**: 11 tests

## Unit Tests

Unit tests use Mockito for mocking dependencies and test business logic in isolation.

### AuthServiceTest
Tests authentication service logic:
- ✅ Register user successfully
- ✅ Prevent registration with duplicate username
- ✅ Prevent registration with duplicate email
- ✅ Login user successfully
- ✅ Handle login with non-existent user

**Test Count**: 5 tests

### UserServiceTest
Tests user management service:
- ✅ Retrieve all users
- ✅ Get user by ID
- ✅ Handle non-existent user (ResourceNotFoundException)
- ✅ Update user successfully
- ✅ Prevent duplicate username on update
- ✅ Prevent duplicate email on update
- ✅ Delete user
- ✅ Handle deletion of non-existent user
- ✅ Partial user updates (update only specific fields)

**Test Count**: 9 tests

### FinancialRecordServiceTest
Tests financial record business logic:
- ✅ Create financial record
- ✅ Retrieve record by ID
- ✅ Handle non-existent record
- ✅ Retrieve all records with filtering
- ✅ Validate date range (startDate must not be after endDate)
- ✅ Update financial record
- ✅ Handle update of non-existent record
- ✅ Delete financial record
- ✅ Handle deletion of non-existent record
- ✅ Filter by transaction type
- ✅ Filter by category

**Test Count**: 11 tests

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Integration Tests Only
```bash
mvn test -Dtest="*IT"
```

### Run Unit Tests Only
```bash
mvn test -Dtest="*Test"
```

### Run Specific Test Class
```bash
mvn test -Dtest=AuthControllerIT
```

### Run with Coverage
```bash
mvn test jacoco:report
```

## Test Coverage Summary

**Total Tests**: 52
- **Integration Tests**: 27
- **Unit Tests**: 25

### Coverage by Module
1. **Authentication**: 12 tests
2. **User Management**: 18 tests
3. **Financial Records**: 22 tests

### Key Features Tested
- ✅ User registration and login
- ✅ Role-based access control (ADMIN, ANALYST, VIEWER)
- ✅ CRUD operations for users and financial records
- ✅ Data validation and error handling
- ✅ Exception handling
- ✅ Business logic with edge cases
- ✅ Security and authorization
- ✅ Input validation

## Best Practices Applied

1. **Separation of Concerns**: Unit tests focus on business logic; integration tests focus on API endpoints
2. **Reusable Base Class**: BaseIT provides common functionality to all integration tests
3. **Clear Test Names**: Test methods use descriptive names explaining what is being tested
4. **Arrange-Act-Assert Pattern**: Tests follow AAA pattern for clarity
5. **Proper Cleanup**: Tests use @Transactional and @BeforeEach to ensure clean state
6. **Error Handling**: Tests verify both success and failure scenarios
7. **Role-Based Testing**: Tests cover different user roles and permissions
8. **Mocking**: Unit tests mock external dependencies appropriately

## Troubleshooting

### Tests Fail with Database Issues
- Ensure H2 in-memory database is properly configured in `application-test.properties`
- Check that `spring.jpa.hibernate.ddl-auto=create-drop` is set for test profile

### Authentication Tests Fail
- Verify that the JWT token generation is working correctly
- Check that security configuration allows access to test endpoints
- Ensure authentication manager is properly configured in security config

### Mock-Related Errors
- Ensure mockito and mockito-junit-jupiter are in classpath
- Verify @ExtendWith(MockitoExtension.class) is present on unit tests
- Check that @Mock and @InjectMocks are used correctly

## Future Enhancements

1. Add performance/load tests using JMH
2. Add contract tests for REST APIs using Spring Cloud Contract
3. Add database integration tests with Testcontainers for PostgreSQL
4. Add security tests using Spring Security Test
5. Add test fixtures and builders for common test data
6. Add parameterized tests using @ParameterizedTest
7. Add BDD-style tests using Cucumber/Gherkin
8. Add mutation testing using PITest

## References

- [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web/)
- [Testcontainers Documentation](https://www.testcontainers.org/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Spring Security Testing](https://spring.io/guides/topical/spring-security/)

