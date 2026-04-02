package io.zorvyn.task.financedashboard.controller;
import io.zorvyn.task.financedashboard.BaseIT;
import io.zorvyn.task.financedashboard.dto.UpdateUserRequest;
import io.zorvyn.task.financedashboard.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
class UserControllerIT extends BaseIT {
    @BeforeEach
    void setup() {
        super.setUp();
        setUp();
        userRepository.deleteAll();
    }
    @Test
    void testGetAllUsersAsAdmin() throws Exception {
        createTestUser(ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_PASSWORD, Role.ADMIN);
        createTestUser(USER_USERNAME, USER_EMAIL, USER_PASSWORD, Role.VIEWER);
        createTestUser(ANALYST_USERNAME, ANALYST_EMAIL, ANALYST_PASSWORD, Role.ANALYST);
        String adminToken = getAdminAuthToken();
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }
    @Test
    void testGetAllUsersWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void testGetAllUsersAsNonAdmin() throws Exception {
        createTestUser(ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_PASSWORD, Role.ADMIN);
        createTestUser(USER_USERNAME, USER_EMAIL, USER_PASSWORD, Role.VIEWER);
        String userToken = getUserAuthToken();
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
    @Test
    void testGetUserByIdAsAdmin() throws Exception {
        var admin = createTestUser(ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_PASSWORD, Role.ADMIN);
        var user = createTestUser(USER_USERNAME, USER_EMAIL, USER_PASSWORD, Role.VIEWER);
        String adminToken = getAdminAuthToken();
        mockMvc.perform(get("/api/users/" + user.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USER_USERNAME))
                .andExpect(jsonPath("$.email").value(USER_EMAIL));
    }
    @Test
    void testGetNonExistentUser() throws Exception {
        createTestUser(ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_PASSWORD, Role.ADMIN);
        String adminToken = getAdminAuthToken();
        mockMvc.perform(get("/api/users/999")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
    @Test
    void testUpdateUserAsAdmin() throws Exception {
        createTestUser(ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_PASSWORD, Role.ADMIN);
        var user = createTestUser(USER_USERNAME, USER_EMAIL, USER_PASSWORD, Role.VIEWER);
        String adminToken = getAdminAuthToken();
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setUsername("updateduser");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setRole(Role.ANALYST);
        mockMvc.perform(put("/api/users/" + user.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.role").value("ANALYST"));
    }
    @Test
    void testDeleteUserAsAdmin() throws Exception {
        createTestUser(ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_PASSWORD, Role.ADMIN);
        var user = createTestUser(USER_USERNAME, USER_EMAIL, USER_PASSWORD, Role.VIEWER);
        String adminToken = getAdminAuthToken();
        mockMvc.perform(delete("/api/users/" + user.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/users/" + user.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
    @Test
    void testUpdateUserWithDuplicateUsername() throws Exception {
        createTestUser(ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_PASSWORD, Role.ADMIN);
        var user1 = createTestUser(USER_USERNAME, USER_EMAIL, USER_PASSWORD, Role.VIEWER);
        var user2 = createTestUser("anotheruser", "another@example.com", USER_PASSWORD, Role.VIEWER);
        String adminToken = getAdminAuthToken();
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setUsername(USER_USERNAME); // Try to use existing username
        mockMvc.perform(put("/api/users/" + user2.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict());
    }
}
