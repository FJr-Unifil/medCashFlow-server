package example.medCashFlow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import example.medCashFlow.dto.involved.InvolvedRegisterDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InvolvedControllerTests extends MedCashFlowApplicationTests {

    @Test
    void whenAnonymousListInvolveds_thenForbidden() throws Exception {
        mockMvc.perform(get("/involveds/list"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenManagerListInvolveds_thenSucceeds() throws Exception {
        mockMvc.perform(get("/involveds/list")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
    }

    @Test
    void whenFinancialAnalystListInvolveds_thenSucceeds() throws Exception {
        mockMvc.perform(get("/involveds/list")
                        .header("Authorization", "Bearer " + financialAnalystToken))
                .andExpect(status().isOk());
    }

    @Test
    void whenAdminListInvolveds_thenForbidden() throws Exception {
        mockMvc.perform(get("/involveds/list")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenManagerCreateInvolved_thenSucceeds() throws Exception {
        InvolvedRegisterDTO involvedDTO = new InvolvedRegisterDTO(
                "Test Involved",
                "12345678901",
                "1234567890",
                "involved@test.com"
        );

        mockMvc.perform(post("/involveds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(involvedDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Involved"))
                .andExpect(jsonPath("$.document").value("12345678901"));
    }

    @Test
    void whenFinancialAnalystCreateInvolved_thenSucceeds() throws Exception {
        InvolvedRegisterDTO involvedDTO = new InvolvedRegisterDTO(
                "Financial Test",
                "12345678902",
                "1234567890",
                "financial.involved@test.com"
        );

        mockMvc.perform(post("/involveds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(involvedDTO))
                        .header("Authorization", "Bearer " + financialAnalystToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Financial Test"));
    }

    @Test
    void whenCreateInvolvedWithExistingDocument_thenConflict() throws Exception {
        InvolvedRegisterDTO firstInvolved = new InvolvedRegisterDTO(
                "First Involved",
                "12345678903",
                "1234567890",
                "first@test.com"
        );

        mockMvc.perform(post("/involveds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(firstInvolved))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());

        InvolvedRegisterDTO duplicateInvolved = new InvolvedRegisterDTO(
                "Duplicate Involved",
                "12345678903",
                "1234567890",
                "second@test.com"
        );

        mockMvc.perform(post("/involveds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(duplicateInvolved))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isConflict());
    }

    @Test
    void whenManagerUpdateInvolved_thenSucceeds() throws Exception {
        InvolvedRegisterDTO createDTO = new InvolvedRegisterDTO(
                "To Update",
                "12345678904",
                "1234567890",
                "toupdate@test.com"
        );

        MvcResult createResult = mockMvc.perform(post("/involveds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Integer involvedId = JsonPath.parse(response).read("$.id");

        InvolvedRegisterDTO updateDTO = new InvolvedRegisterDTO(
                "Updated Name",
                "12345678904",
                "1234567890",
                "toupdate@test.com"
        );

        mockMvc.perform(put("/involveds/" + involvedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void whenManagerDeleteInvolved_thenSucceeds() throws Exception {
        InvolvedRegisterDTO createDTO = new InvolvedRegisterDTO(
                "To Delete",
                "12345678905",
                "1234567890",
                "todelete@test.com"
        );

        MvcResult createResult = mockMvc.perform(post("/involveds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Integer involvedId = JsonPath.parse(response).read("$.id");

        mockMvc.perform(delete("/involveds/" + involvedId)
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenManagerActivateInvolved_thenSucceeds() throws Exception {
        InvolvedRegisterDTO createDTO = new InvolvedRegisterDTO(
                "To Activate",
                "12345678906",
                "1234567890",
                "toactivate@test.com"
        );

        MvcResult createResult = mockMvc.perform(post("/involveds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Integer involvedId = JsonPath.parse(response).read("$.id");

        mockMvc.perform(delete("/involveds/" + involvedId)
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(put("/involveds/activate/" + involvedId)
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenFinancialAnalystActivateInvolved_thenSucceeds() throws Exception {
        InvolvedRegisterDTO createDTO = new InvolvedRegisterDTO(
                "Financial To Activate",
                "12345678907",
                "1234567890",
                "financial.activate@test.com"
        );

        MvcResult createResult = mockMvc.perform(post("/involveds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO))
                        .header("Authorization", "Bearer " + financialAnalystToken))
                .andExpect(status().isOk())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Integer involvedId = JsonPath.parse(response).read("$.id");

        mockMvc.perform(delete("/involveds/" + involvedId)
                        .header("Authorization", "Bearer " + financialAnalystToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(put("/involveds/activate/" + involvedId)
                        .header("Authorization", "Bearer " + financialAnalystToken))
                .andExpect(status().isNoContent());
    }
}

