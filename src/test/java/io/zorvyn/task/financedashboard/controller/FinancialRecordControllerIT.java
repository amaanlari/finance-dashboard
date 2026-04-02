package io.zorvyn.task.financedashboard.controller;
import io.zorvyn.task.financedashboard.BaseIT;
import io.zorvyn.task.financedashboard.dto.FinancialRecordRequest;
import io.zorvyn.task.financedashboard.model.Role;
import io.zorvyn.task.financedashboard.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
class FinancialRecordControllerIT extends BaseIT {
    @BeforeEach
    void setup() {
        super.setUp();
        userRepository.deleteAll();
    }
    @Test
    void testCreateFinancialRecordAsAnalyst() throws Exception {
        createTestUser(ANALYST_USERNAME, ANALYST_EMAIL, ANALYST_PASSWORD, Role.ANALYST);
        String analystToken = getAnalystAuthToken();
        FinancialRecordRequest request = new FinancialRecordRequest();
        request.setAmount(new BigDecimal("1000.50"));
        request.setType(TransactionType.INCOME);
        request.setCategory("Salary");
        request.setDate(LocalDate.now());
        request.setNotes("Monthly salary");
        mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + analystToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(1000.50))
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.category").value("Salary"));
    }
    @Test
    void testCreateFinancialRecordAsAdmin() throws Exception {
        createTestUser(ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_PASSWORD, Role.ADMIN);
        String adminToken = getAdminAuthToken();
        FinancialRecordRequest request = new FinancialRecordRequest();
        request.setAmount(new BigDecimal("500.00"));
        request.setType(TransactionType.EXPENSE);
        request.setCategory("Utilities");
        request.setDate(LocalDate.now());
        request.setNotes("Monthly utilities");
        mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(500.00))
                .andExpect(jsonPath("$.type").value("EXPENSE"));
    }
    @Test
    void testCreateFinancialRecordAsViewer() throws Exception {
        createTestUser(VIEWER_USERNAME, VIEWER_EMAIL, VIEWER_PASSWORD, Role.VIEWER);
        String viewerToken = getViewerAuthToken();
        FinancialRecordRequest request = new FinancialRecordRequest();
        request.setAmount(new BigDecimal("1000.00"));
        request.setType(TransactionType.INCOME);
        request.setCategory("Bonus");
        request.setDate(LocalDate.now());
        mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + viewerToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
    @Test
    void testCreateRecordWithoutAuthentication() throws Exception {
        FinancialRecordRequest request = new FinancialRecordRequest();
        request.setAmount(new BigDecimal("1000.00"));
        request.setType(TransactionType.INCOME);
        request.setCategory("Salary");
        request.setDate(LocalDate.now());
        mockMvc.perform(post("/api/records")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void testCreateRecordWithInvalidAmount() throws Exception {
        createTestUser(ANALYST_USERNAME, ANALYST_EMAIL, ANALYST_PASSWORD, Role.ANALYST);
        String analystToken = getAnalystAuthToken();
        FinancialRecordRequest request = new FinancialRecordRequest();
        request.setAmount(new BigDecimal("-100.00")); // Invalid negative amount
        request.setType(TransactionType.INCOME);
        request.setCategory("Salary");
        request.setDate(LocalDate.now());
        mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + analystToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testGetAllRecords() throws Exception {
        createTestUser(ANALYST_USERNAME, ANALYST_EMAIL, ANALYST_PASSWORD, Role.ANALYST);
        String analystToken = getAnalystAuthToken();
        FinancialRecordRequest request1 = new FinancialRecordRequest();
        request1.setAmount(new BigDecimal("1000.00"));
        request1.setType(TransactionType.INCOME);
        request1.setCategory("Salary");
        request1.setDate(LocalDate.now());
        FinancialRecordRequest request2 = new FinancialRecordRequest();
        request2.setAmount(new BigDecimal("500.00"));
        request2.setType(TransactionType.EXPENSE);
        request2.setCategory("Food");
        request2.setDate(LocalDate.now());
        mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + analystToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request1)));
        mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + analystToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request2)));
        mockMvc.perform(get("/api/records")
                .header("Authorization", "Bearer " + analystToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
    @Test
    void testGetRecordsByType() throws Exception {
        createTestUser(ANALYST_USERNAME, ANALYST_EMAIL, ANALYST_PASSWORD, Role.ANALYST);
        String analystToken = getAnalystAuthToken();
        FinancialRecordRequest request1 = new FinancialRecordRequest();
        request1.setAmount(new BigDecimal("1000.00"));
        request1.setType(TransactionType.INCOME);
        request1.setCategory("Salary");
        request1.setDate(LocalDate.now());
        FinancialRecordRequest request2 = new FinancialRecordRequest();
        request2.setAmount(new BigDecimal("500.00"));
        request2.setType(TransactionType.EXPENSE);
        request2.setCategory("Food");
        request2.setDate(LocalDate.now());
        mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + analystToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request1)));
        mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + analystToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request2)));
        mockMvc.perform(get("/api/records?type=INCOME")
                .header("Authorization", "Bearer " + analystToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("INCOME"));
    }
    @Test
    void testGetRecordsByCategory() throws Exception {
        createTestUser(ANALYST_USERNAME, ANALYST_EMAIL, ANALYST_PASSWORD, Role.ANALYST);
        String analystToken = getAnalystAuthToken();
        FinancialRecordRequest request1 = new FinancialRecordRequest();
        request1.setAmount(new BigDecimal("1000.00"));
        request1.setType(TransactionType.INCOME);
        request1.setCategory("Salary");
        request1.setDate(LocalDate.now());
        FinancialRecordRequest request2 = new FinancialRecordRequest();
        request2.setAmount(new BigDecimal("500.00"));
        request2.setType(TransactionType.EXPENSE);
        request2.setCategory("Food");
        request2.setDate(LocalDate.now());
        mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + analystToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request1)));
        mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + analystToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request2)));
        mockMvc.perform(get("/api/records?category=Salary")
                .header("Authorization", "Bearer " + analystToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category").value("Salary"));
    }
    @Test
    void testUpdateRecord() throws Exception {
        createTestUser(ANALYST_USERNAME, ANALYST_EMAIL, ANALYST_PASSWORD, Role.ANALYST);
        String analystToken = getAnalystAuthToken();
        FinancialRecordRequest createRequest = new FinancialRecordRequest();
        createRequest.setAmount(new BigDecimal("1000.00"));
        createRequest.setType(TransactionType.INCOME);
        createRequest.setCategory("Salary");
        createRequest.setDate(LocalDate.now());
        String response = mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + analystToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long recordId = objectMapper.readTree(response).get("id").asLong();
        FinancialRecordRequest updateRequest = new FinancialRecordRequest();
        updateRequest.setAmount(new BigDecimal("1500.00"));
        updateRequest.setType(TransactionType.INCOME);
        updateRequest.setCategory("Updated Salary");
        updateRequest.setDate(LocalDate.now());
        updateRequest.setNotes("Updated notes");
        mockMvc.perform(put("/api/records/" + recordId)
                .header("Authorization", "Bearer " + analystToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(1500.00))
                .andExpect(jsonPath("$.category").value("Updated Salary"));
    }
    @Test
    void testDeleteRecord() throws Exception {
        createTestUser(ANALYST_USERNAME, ANALYST_EMAIL, ANALYST_PASSWORD, Role.ANALYST);
        String analystToken = getAnalystAuthToken();
        FinancialRecordRequest request = new FinancialRecordRequest();
        request.setAmount(new BigDecimal("1000.00"));
        request.setType(TransactionType.INCOME);
        request.setCategory("Salary");
        request.setDate(LocalDate.now());
        String response = mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + analystToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long recordId = objectMapper.readTree(response).get("id").asLong();
        mockMvc.perform(delete("/api/records/" + recordId)
                .header("Authorization", "Bearer " + analystToken))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/records/" + recordId)
                .header("Authorization", "Bearer " + analystToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSoftDeletePreservesDataInDatabase() throws Exception {
        createTestUser(ANALYST_USERNAME, ANALYST_EMAIL, ANALYST_PASSWORD, Role.ANALYST);
        String analystToken = getAnalystAuthToken();

        // Create two records
        FinancialRecordRequest request1 = new FinancialRecordRequest();
        request1.setAmount(new BigDecimal("1000.00"));
        request1.setType(TransactionType.INCOME);
        request1.setCategory("Salary");
        request1.setDate(LocalDate.now());

        String response1 = mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + analystToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long recordId1 = objectMapper.readTree(response1).get("id").asLong();

        FinancialRecordRequest request2 = new FinancialRecordRequest();
        request2.setAmount(new BigDecimal("500.00"));
        request2.setType(TransactionType.EXPENSE);
        request2.setCategory("Food");
        request2.setDate(LocalDate.now());

        mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + analystToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        // Verify both records are in the list
        mockMvc.perform(get("/api/records")
                .header("Authorization", "Bearer " + analystToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Delete first record (soft delete)
        mockMvc.perform(delete("/api/records/" + recordId1)
                .header("Authorization", "Bearer " + analystToken))
                .andExpect(status().isNoContent());

        // Verify only active record is in the list
        mockMvc.perform(get("/api/records")
                .header("Authorization", "Bearer " + analystToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].amount").value(500.00));

        // Verify deleted record cannot be accessed
        mockMvc.perform(get("/api/records/" + recordId1)
                .header("Authorization", "Bearer " + analystToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCannotUpdateSoftDeletedRecord() throws Exception {
        createTestUser(ANALYST_USERNAME, ANALYST_EMAIL, ANALYST_PASSWORD, Role.ANALYST);
        String analystToken = getAnalystAuthToken();

        FinancialRecordRequest request = new FinancialRecordRequest();
        request.setAmount(new BigDecimal("1000.00"));
        request.setType(TransactionType.INCOME);
        request.setCategory("Salary");
        request.setDate(LocalDate.now());

        String response = mockMvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + analystToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long recordId = objectMapper.readTree(response).get("id").asLong();

        // Delete the record (soft delete)
        mockMvc.perform(delete("/api/records/" + recordId)
                .header("Authorization", "Bearer " + analystToken))
                .andExpect(status().isNoContent());

        // Try to update deleted record
        FinancialRecordRequest updateRequest = new FinancialRecordRequest();
        updateRequest.setAmount(new BigDecimal("2000.00"));
        updateRequest.setType(TransactionType.INCOME);
        updateRequest.setCategory("Updated");
        updateRequest.setDate(LocalDate.now());

        mockMvc.perform(put("/api/records/" + recordId)
                .header("Authorization", "Bearer " + analystToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }
}
