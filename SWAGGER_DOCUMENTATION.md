# Swagger / OpenAPI Documentation

This project includes comprehensive API documentation using **SpringDoc OpenAPI 3** and **Swagger UI**.

## Accessing Swagger UI

Once the application is running, you can access the interactive API documentation at:

### Swagger UI (Interactive Documentation)
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON Specification
```
http://localhost:8080/v3/api-docs
```

## Features

### 1. **Interactive API Testing**
- Test all API endpoints directly from the browser
- No need for external tools like Postman or cURL
- Real-time request/response visualization

### 2. **Comprehensive Documentation**
- Detailed descriptions for all endpoints
- Request/response schemas
- Example values
- HTTP status codes and their meanings
- Security requirements

### 3. **JWT Authentication Integration**
- Built-in authorization mechanism
- Easy token management
- Automatic inclusion of bearer tokens in requests

## How to Use Swagger UI

### Step 1: Start the Application
```bash
./mvnw spring-boot:run
```

### Step 2: Open Swagger UI
Navigate to: `http://localhost:8080/swagger-ui.html`

### Step 3: Authenticate

1. **Register or Login** to get a JWT token:
   - Expand the "Authentication" section
   - Click on `POST /api/auth/login`
   - Click "Try it out"
   - Enter test credentials:
     ```json
     {
       "username": "admin",
       "password": "admin123"
     }
     ```
   - Click "Execute"
   - Copy the `token` value from the response

2. **Authorize Swagger UI**:
   - Click the **"Authorize"** button at the top right (🔓 icon)
   - Enter: `Bearer <your-token>` (replace `<your-token>` with the actual token)
   - Example: `Bearer eyJhbGciOiJIUzUxMiJ9...`
   - Click "Authorize"
   - Click "Close"

3. **Test Protected Endpoints**:
   - All secured endpoints now include your authentication token
   - Try the dashboard summary: `GET /api/dashboard/summary`

### Step 4: Explore API Endpoints

The API is organized into 4 main sections:

#### 1. Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT token

#### 2. Dashboard
- `GET /api/dashboard/summary` - Get comprehensive financial analytics

#### 3. Financial Records
- `POST /api/records` - Create a new record (ADMIN/ANALYST)
- `GET /api/records` - Get all records
- `GET /api/records/{id}` - Get specific record
- `GET /api/records/type/{type}` - Filter by INCOME or EXPENSE
- `GET /api/records/category/{category}` - Filter by category
- `GET /api/records/date-range` - Filter by date range
- `GET /api/records/recent` - Get 10 most recent records
- `PUT /api/records/{id}` - Update a record (ADMIN/ANALYST)
- `DELETE /api/records/{id}` - Delete a record (ADMIN only)

#### 4. User Management (ADMIN only)
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get specific user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

## Testing Different Roles

### As ADMIN (Full Access)
```bash
# Login as admin
{
  "username": "admin",
  "password": "admin123"
}
```
- Can access ALL endpoints
- Can create, read, update, and delete records
- Can manage users

### As ANALYST (Limited Access)
```bash
# Login as analyst
{
  "username": "analyst",
  "password": "analyst123"
}
```
- Can view dashboard and records
- Can create and update records
- CANNOT delete records
- CANNOT manage users

### As VIEWER (Read-Only)
```bash
# Login as viewer
{
  "username": "viewer",
  "password": "viewer123"
}
```
- Can view dashboard and records only
- CANNOT create, update, or delete records
- CANNOT manage users

## API Request Examples

### Create a Financial Record
```json
POST /api/records
{
  "amount": 2500.00,
  "type": "INCOME",
  "category": "Freelance",
  "date": "2026-04-02",
  "notes": "Web development project"
}
```

### Filter Records by Date Range
```
GET /api/records/date-range?startDate=2026-03-01&endDate=2026-03-31
```

### Update User Role
```json
PUT /api/users/2
{
  "role": "ADMIN",
  "active": true
}
```

## Response Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 204 | No Content (successful deletion) |
| 400 | Bad Request (validation error) |
| 401 | Unauthorized (missing/invalid token) |
| 403 | Forbidden (insufficient permissions) |
| 404 | Not Found |
| 409 | Conflict (duplicate resource) |
| 500 | Internal Server Error |

## Schema Information

All request and response schemas are fully documented in Swagger UI with:
- Field names and types
- Validation rules
- Required vs optional fields
- Example values
- Descriptions

Click on any schema in Swagger UI to view:
- `Model` tab: Full schema definition
- `Example Value` tab: Sample JSON

## Advanced Features

### 1. Try It Out
- Each endpoint has a "Try it out" button
- Edit request parameters directly
- Execute requests with one click
- View formatted responses

### 2. Download OpenAPI Spec
- Download the complete API specification
- Use with code generators
- Import into other API tools

### 3. Request/Response Samples
- Pre-filled example requests
- See expected response formats
- Understand data structures

## Customization

### Swagger UI Configuration
Located in `application.properties`:

```properties
# SpringDoc OpenAPI Configuration
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operations-sorter=method
springdoc.swagger-ui.tags-sorter=alpha
springdoc.swagger-ui.try-it-out-enabled=true
springdoc.swagger-ui.display-request-duration=true
```

### OpenAPI Annotations
The API documentation is generated from annotations in controller classes:

- `@Tag` - Group endpoints
- `@Operation` - Endpoint details
- `@ApiResponses` - Response documentation
- `@Parameter` - Parameter details
- `@Schema` - Data model documentation
- `@SecurityRequirement` - Authentication requirements

## Troubleshooting

### Swagger UI Not Loading
1. Ensure the application is running: `./mvnw spring-boot:run`
2. Check the correct URL: `http://localhost:8080/swagger-ui.html`
3. Verify SpringDoc dependency in `pom.xml`

### 401 Unauthorized Errors
1. Make sure you've logged in and received a token
2. Click "Authorize" and enter `Bearer <token>`
3. Check if token has expired (24 hours validity)
4. Refresh your token by logging in again

### 403 Forbidden Errors
- You don't have permission for this endpoint
- Try logging in as a different role:
  - VIEWER → ANALYST → ADMIN (increasing permissions)

### Request Validation Errors (400)
- Check the "Schemas" section for required fields
- Ensure all required fields are provided
- Verify data types match the schema
- Check validation constraints (min/max values, patterns)

## Benefits of Using Swagger UI

1. **No Manual Documentation**: Auto-generated from code
2. **Always Up-to-Date**: Synced with actual API implementation
3. **Interactive Testing**: Test APIs without writing code
4. **Team Collaboration**: Share API docs easily
5. **Client SDK Generation**: Generate client code in multiple languages
6. **API Contract**: Clear contract between frontend and backend
7. **Onboarding**: New team members can understand APIs quickly

## Production Considerations

For production deployments:

1. **Disable Swagger in Production** (optional):
   ```properties
   springdoc.swagger-ui.enabled=false
   springdoc.api-docs.enabled=false
   ```

2. **Secure Swagger UI**:
   - Add authentication for Swagger endpoints
   - Restrict access to internal network only

3. **Use HTTPS**:
   - Update server URLs in OpenAPI config
   - Ensure all API calls use HTTPS

## Additional Resources

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)

## Summary

Swagger UI provides a powerful, interactive way to explore and test the Finance Dashboard API. With comprehensive documentation, real-time testing capabilities, and JWT authentication integration, it's the perfect tool for API development and testing.

**Quick Start:**
1. Run application: `./mvnw spring-boot:run`
2. Open: `http://localhost:8080/swagger-ui.html`
3. Login as `admin` / `admin123`
4. Authorize with the returned JWT token
5. Start testing endpoints!
