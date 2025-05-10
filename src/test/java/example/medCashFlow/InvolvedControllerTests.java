package example.medCashFlow;

import com.jayway.jsonpath.JsonPath;
import example.medCashFlow.dto.involved.InvolvedRegisterDTO;
import example.medCashFlow.exceptions.ApiError;
import example.medCashFlow.exceptions.ApiValidationError;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InvolvedControllerTests extends MedCashFlowApplicationTests {

    @Test
    void whenAnonymousGetInvolvedById_thenForbidden() throws Exception {
        mockMvc.perform(get("/involveds/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeGetInvolvedById_thenSucceeds() throws Exception {
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
    void whenAllowedEmployeeGetNonExistentInvolvedById_thenSucceeds() throws Exception {
        mockMvc.perform(get("/involveds/999")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenAdminGetInvolvedById_thenForbidden() throws Exception {
        mockMvc.perform(get("/involveds/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousListInvolveds_thenForbidden() throws Exception {
        mockMvc.perform(get("/involveds/list"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeListInvolveds_thenSucceeds() throws Exception {
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
    void whenAdminListInvolveds_thenForbidden() throws Exception {
        mockMvc.perform(get("/involveds/list")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeCreateInvolved_thenSucceeds() throws Exception {
        InvolvedRegisterDTO involvedDTO = new InvolvedRegisterDTO(
                "Test Involved",
                "12345678901",
                "1234567890",
                "involved@test.com"
        );

        mockMvc.perform(post("/involveds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(involvedDTO))
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
    void whenCreateInvolvedWithExistingDocument_thenConflict() throws Exception {
        InvolvedRegisterDTO firstInvolved = new InvolvedRegisterDTO(
                "First Involved",
                "12345678903",
                "1234567890",
                "first@test.com"
        );

        mockMvc.perform(post("/involveds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstInvolved))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());

        InvolvedRegisterDTO duplicateInvolved = new InvolvedRegisterDTO(
                "Duplicate Involved",
                firstInvolved.document(),
                "1234567891",
                "second@test.com"
        );

        MvcResult mvcResult = mockMvc.perform(post("/involveds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateInvolved))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isConflict())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        ApiError apiError = objectMapper.readValue(json, ApiError.class);

        assertEquals(409, apiError.status());
        assertEquals("Conflict", apiError.error());
        assertEquals("Data Conflict", apiError.message());
        assertEquals("/involveds/create", apiError.path());

        List<Object> rejectedValue = apiError.subErrors().stream()
                .map(ApiValidationError::rejectedValue)
                .toList();

        assertThat(rejectedValue).containsExactlyInAnyOrder(
                duplicateInvolved.document()
        );
    }

    @Test
    void whenCreateInvolvedWithExistingEmail_thenConflict() throws Exception {
        InvolvedRegisterDTO firstInvolved = new InvolvedRegisterDTO(
                "First Involved",
                "12345678909",
                "1234567890",
                "first@test.com"
        );

        mockMvc.perform(post("/involveds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstInvolved))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());

        InvolvedRegisterDTO duplicateInvolved = new InvolvedRegisterDTO(
                "Duplicate Involved",
                "12345678903",
                "1234567899",
                firstInvolved.email()
        );

        MvcResult mvcResult = mockMvc.perform(post("/involveds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateInvolved))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isConflict())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        ApiError apiError = objectMapper.readValue(json, ApiError.class);

        assertEquals(409, apiError.status());
        assertEquals("Conflict", apiError.error());
        assertEquals("Data Conflict", apiError.message());
        assertEquals("/involveds/create", apiError.path());

        List<Object> rejectedValue = apiError.subErrors().stream()
                .map(ApiValidationError::rejectedValue)
                .toList();

        assertThat(rejectedValue).containsExactlyInAnyOrder(
                duplicateInvolved.email()
        );
    }

    @Test
    void whenCreateInvolvedWithExistingPhone_thenConflict() throws Exception {
        InvolvedRegisterDTO firstInvolved = new InvolvedRegisterDTO(
                "First Involved",
                "12345678909",
                "1234567890",
                "first@test.com"
        );

        mockMvc.perform(post("/involveds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstInvolved))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());

        InvolvedRegisterDTO duplicateInvolved = new InvolvedRegisterDTO(
                "Duplicate Involved",
                "12345678903",
                firstInvolved.phone(),
                "first@test2.com"
        );

        MvcResult mvcResult = mockMvc.perform(post("/involveds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateInvolved))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isConflict())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        ApiError apiError = objectMapper.readValue(json, ApiError.class);

        assertEquals(409, apiError.status());
        assertEquals("Conflict", apiError.error());
        assertEquals("Data Conflict", apiError.message());
        assertEquals("/involveds/create", apiError.path());

        List<Object> rejectedValue = apiError.subErrors().stream()
                .map(ApiValidationError::rejectedValue)
                .toList();

        assertThat(rejectedValue).containsExactlyInAnyOrder(
                duplicateInvolved.phone()
        );
    }

    @Test
    void whenAllowedEmployeeUpdateInvolved_thenSucceeds() throws Exception {
        InvolvedRegisterDTO updateDTO = new InvolvedRegisterDTO(
                "Updated Name",
                "12345678904",
                "9876543210",
                "updated@test.com"
        );

        mockMvc.perform(put("/involveds/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
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
    void whenAllowedEmployeeUpdateInvolvedWithExistingDocument_thenConflict() throws Exception {
        InvolvedRegisterDTO involvedDTO = new InvolvedRegisterDTO(
                "Test Involved",
                "12345678901",
                "1234567890",
                "involved@test.com"
        );

        mockMvc.perform(post("/involveds/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(involvedDTO))
                .header("Authorization", "Bearer " + managerToken));

        InvolvedRegisterDTO updateDTO = new InvolvedRegisterDTO(
                "Updated Name",
                involvedDTO.document(),
                "9876543210",
                "updated@test.com"
        );

        MvcResult mvcResult = mockMvc.perform(put("/involveds/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isConflict())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        ApiError apiError = objectMapper.readValue(json, ApiError.class);

        assertEquals(409, apiError.status());
        assertEquals("Conflict", apiError.error());
        assertEquals("Data Conflict", apiError.message());
        assertEquals("/involveds/1", apiError.path());

        List<Object> rejectedValues = apiError.subErrors().stream()
                .map(ApiValidationError::rejectedValue)
                .toList();

        assertThat(rejectedValues).containsExactlyInAnyOrder(
                involvedDTO.document()
        );
    }

    @Test
    void whenAllowedEmployeeUpdateInvolvedWithExistingPhone_thenConflict() throws Exception {
        InvolvedRegisterDTO involvedDTO = new InvolvedRegisterDTO(
                "Test Involved",
                "12345678901",
                "1234567890",
                "involved@test.com"
        );

        mockMvc.perform(post("/involveds/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(involvedDTO))
                .header("Authorization", "Bearer " + managerToken));

        InvolvedRegisterDTO updateDTO = new InvolvedRegisterDTO(
                "Updated Name",
                "12345678902",
                involvedDTO.phone(),
                "updated@test.com"
        );

        MvcResult mvcResult = mockMvc.perform(put("/involveds/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isConflict())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        ApiError apiError = objectMapper.readValue(json, ApiError.class);

        assertEquals(409, apiError.status());
        assertEquals("Conflict", apiError.error());
        assertEquals("Data Conflict", apiError.message());
        assertEquals("/involveds/1", apiError.path());

        List<Object> rejectedValues = apiError.subErrors().stream()
                .map(ApiValidationError::rejectedValue)
                .toList();

        assertThat(rejectedValues).containsExactlyInAnyOrder(
                involvedDTO.phone()
        );
    }

    @Test
    void whenAllowedEmployeeUpdateInvolvedWithExistingEmail_thenConflict() throws Exception {
        InvolvedRegisterDTO involvedDTO = new InvolvedRegisterDTO(
                "Test Involved",
                "12345678901",
                "1234567890",
                "involved@test.com"
        );

        mockMvc.perform(post("/involveds/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(involvedDTO))
                .header("Authorization", "Bearer " + managerToken));

        InvolvedRegisterDTO updateDTO = new InvolvedRegisterDTO(
                "Updated Name",
                "12345678902",
                "1234567891",
                involvedDTO.email()
        );

        MvcResult mvcResult = mockMvc.perform(put("/involveds/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isConflict())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        ApiError apiError = objectMapper.readValue(json, ApiError.class);

        assertEquals(409, apiError.status());
        assertEquals("Conflict", apiError.error());
        assertEquals("Data Conflict", apiError.message());
        assertEquals("/involveds/1", apiError.path());

        List<Object> rejectedValues = apiError.subErrors().stream()
                .map(ApiValidationError::rejectedValue)
                .toList();

        assertThat(rejectedValues).containsExactlyInAnyOrder(
                involvedDTO.email()
        );
    }

    @Test
    void whenAllowedEmployeeUpdateNonExistentInvolved_thenNotFound() throws Exception {
        InvolvedRegisterDTO updateDTO = new InvolvedRegisterDTO(
                "Non Existent",
                "12345678909",
                "1234567890",
                "nonexistent@test.com"
        );

        mockMvc.perform(put("/involveds/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenAllowedEmployeeDeleteAnActivateInvolved_thenSucceeds() throws Exception {
        mockMvc.perform(delete("/involveds/1")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/involveds/1")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void whenDeleteNonExistentInvolved_thenNotFound() throws Exception {
        mockMvc.perform(delete("/involveds/999999")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenAllowedEmployeeActivateInvolved_thenSucceeds() throws Exception {
        InvolvedRegisterDTO createDTO = new InvolvedRegisterDTO(
                "To Activate",
                "12345678906",
                "1234567890",
                "toactivate@test.com"
        );

        MvcResult createResult = mockMvc.perform(post("/involveds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO))
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
    void whenActivateNonExistentInvolved_thenNotFound() throws Exception {
        mockMvc.perform(put("/involveds/activate/999999")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

}

