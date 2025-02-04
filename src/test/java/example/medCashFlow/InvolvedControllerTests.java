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
    void whenAnonymousGetInvolvedByIdThenForbidden() throws Exception {
        mockMvc.perform(get("/involveds/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeGetInvolvedByIdThenSucceeds() throws Exception {
        mockMvc.perform(get("/involveds/1")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Saúde Equipamentos LTDA"))
                .andExpect(jsonPath("$.document").value("99999999999999"))
                .andExpect(jsonPath("$.phone").value("9999999999"))
                .andExpect(jsonPath("$.email").value("saudeequipamentos@gmail.com"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void whenAllowedEmployeeGetNonExistentInvolvedByIdThenSucceeds() throws Exception {
        mockMvc.perform(get("/involveds/999")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenAdminGetInvolvedByIdThenForbidden() throws Exception {
        mockMvc.perform(get("/involveds/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousListInvolvedsThenForbidden() throws Exception {
        mockMvc.perform(get("/involveds/list"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeListInvolvedsThenSucceeds() throws Exception {
        mockMvc.perform(get("/involveds/list")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].document").exists())
                .andExpect(jsonPath("$[0].phone").exists())
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[0].isActive").exists());
    }

    @Test
    void whenAdminListInvolvedsThenForbidden() throws Exception {
        mockMvc.perform(get("/involveds/list")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeCreateInvolvedThenSucceeds() throws Exception {
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
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Involved"))
                .andExpect(jsonPath("$.document").value("12345678901"))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.email").value("involved@test.com"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void whenCreateInvolvedWithExistingDocumentThenConflict() throws Exception {
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
    void whenAllowedEmployeeUpdateInvolvedThenSucceeds() throws Exception {
        InvolvedRegisterDTO updateDTO = new InvolvedRegisterDTO(
                "Updated Name",
                "12345678904",
                "9876543210",
                "updated@test.com"
        );

        mockMvc.perform(put("/involveds/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.document").value("12345678904"))
                .andExpect(jsonPath("$.phone").value("9876543210"))
                .andExpect(jsonPath("$.email").value("updated@test.com"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void whenUpdateNonExistentInvolvedThenNotFound() throws Exception {
        InvolvedRegisterDTO updateDTO = new InvolvedRegisterDTO(
                "Non Existent",
                "12345678909",
                "1234567890",
                "nonexistent@test.com"
        );

        mockMvc.perform(put("/involveds/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenAllowedEmployeeDeleteAnActivateInvolvedThenSucceeds() throws Exception {
        mockMvc.perform(delete("/involveds/1")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/involveds/1")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void whenDeleteNonExistentInvolvedThenNotFound() throws Exception {
        mockMvc.perform(delete("/involveds/999999")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenAllowedEmployeeActivateInvolvedThenSucceeds() throws Exception {
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
    void whenActivateNonExistentInvolvedThenNotFound() throws Exception {
        mockMvc.perform(put("/involveds/activate/999999")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

}

