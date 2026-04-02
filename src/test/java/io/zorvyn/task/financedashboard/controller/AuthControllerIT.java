package io.zorvyn.task.financedashboard.controller;
import io.zorvyn.task.financedashboard.BaseIT;
import io.zorvyn.task.financedashboard.dto.LoginRequest;
import io.zorvyn.task.financedashboard.dto.RegisterRequest;
import io.zorvyn.task.financedashboard.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
class AuthControllerIT extends BaseIT {
    @BeforeEach
    void setup() {
        setUp();
        userRepository.deleteAll();
    }
    @Test
    void testRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("Password123!");
        request.setRole(Role.VIEWER);
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.role").value("VIEWER"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
    @Test
    void testRegisterWithDuplicateUsername() throws Exception {
        createTestUser(USER_USERNAME, "test@example.com", USER_PASSWORD, Role.VIEWER);
        RegisterRequest request = new RegisterRequest();
        request.setUsername(USER_USERNAME);
        request.setEmail("different@example.com");
        request.setPassword("Password123!");
        request.setRole(Role.VIEWER);
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
    @Test
    void testRegisterWithDuplicateEmail() throws Exception {
        createTestUser(USER_USERNAME, USER_EMAIL, USER_PASSWORD, Role.VIEWER);
        RegisterRequest request = new RegisterRequest();
        request.setUsername("different");
        request.setEmail(USER_EMAIL);
        request.setPassword("Password123!");
        request.setRole(Role.VIEWER);
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
    @Test
    void testLoginSuccessfully() throws Exception {
        createTestUser(USER_USERNAME, USER_EMAIL, USER_PASSWORD, Role.VIEWER);
        LoginRequest request = new LoginRequest();
        request.setUsername(USER_USERNAME);
        request.setPassword(USER_PASSWORD);
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USER_USERNAME))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
    @Test
    void testLoginWithInvalidUsername() throws Exception {
        createTestUser(USER_USERNAME, USER_EMAIL, USER_PASSWORD, Role.VIEWER);
        LoginRequest request = new LoginRequest();
        request.setUsername("wronguser");
        request.setPassword(USER_PASSWORD);
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void testLoginWithInvalidPassword() throws Exception {
        createTestUser(USER_USERNAME, USER_EMAIL, USER_PASSWORD, Role.VIEWER);
        LoginRequest request = new LoginRequest();
        request.setUsername(USER_USERNAME);
        request.setPassword("WrongPassword123!");
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
