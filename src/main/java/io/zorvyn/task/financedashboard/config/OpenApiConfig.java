package io.zorvyn.task.financedashboard.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Finance Dashboard API",
                version = "1.0.0",
                description = """
                        A comprehensive Finance Dashboard Backend API with role-based access control.

                        ## Features
                        - User authentication and authorization (JWT)
                        - Role-based access control (VIEWER, ANALYST, ADMIN)
                        - Financial records management (CRUD operations)
                        - Dashboard analytics and summaries
                        - Advanced filtering and querying

                        ## Authentication
                        1. Register a new user or use test credentials
                        2. Login to receive a JWT token
                        3. Click 'Authorize' button and enter: `Bearer <your-token>`
                        4. All authenticated endpoints will now work

                        ## Test Credentials
                        - Admin: username=`admin`, password=`admin123`
                        - Analyst: username=`analyst`, password=`analyst123`
                        - Viewer: username=`viewer`, password=`viewer123`
                        """,
                contact = @Contact(
                        name = "Finance Dashboard Team",
                        email = "support@financedashboard.com",
                        url = "https://github.com/financedashboard"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Development Server"
                ),
                @Server(
                        url = "https://finance-dashboard-uwib.onrender.com/",
                        description = "Production Server"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT authentication token. Login to get your token, then click 'Authorize' and enter: Bearer <token>",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
