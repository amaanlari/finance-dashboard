package io.zorvyn.task.financedashboard;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zorvyn.task.financedashboard.dto.LoginRequest;
import io.zorvyn.task.financedashboard.model.Role;
import io.zorvyn.task.financedashboard.model.User;
import io.zorvyn.task.financedashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import java.time.LocalDateTime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
@Transactional
public abstract class BaseIT {
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_EMAIL = "admin@example.com";
    public static final String ADMIN_PASSWORD = "AdminPass123!";
    public static final String USER_USERNAME = "user";
    public static final String USER_EMAIL = "user@example.com";
    public static final String USER_PASSWORD = "UserPass123!";
    public static final String ANALYST_USERNAME = "analyst";
    public static final String ANALYST_EMAIL = "analyst@example.com";
    public static final String ANALYST_PASSWORD = "AnalystPass123!";
    public static final String VIEWER_USERNAME = "viewer";
    public static final String VIEWER_EMAIL = "viewer@example.com";
    public static final String VIEWER_PASSWORD = "ViewerPass123!";
    @Autowired
    protected WebApplicationContext webApplicationContext;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;
    protected MockMvc mockMvc;
    protected void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    protected User createTestUser(String username, String email, String password, Role role) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }
    protected String getAuthToken(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }
    protected String getAdminAuthToken() throws Exception {
        return getAuthToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    }
    protected String getUserAuthToken() throws Exception {
        return getAuthToken(USER_USERNAME, USER_PASSWORD);
    }
    protected String getAnalystAuthToken() throws Exception {
        return getAuthToken(ANALYST_USERNAME, ANALYST_PASSWORD);
    }
    protected String getViewerAuthToken() throws Exception {
        return getAuthToken(VIEWER_USERNAME, VIEWER_PASSWORD);
    }
}
