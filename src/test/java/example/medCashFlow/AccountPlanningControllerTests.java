package example.medCashFlow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import example.medCashFlow.dto.accountPlanning.AccountPlanningRegisterDTO;
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
public class AccountPlanningControllerTests extends MedCashFlowApplicationTests {

    @Test
    void whenAnonymousGettingAccountPlanningById_thenForbidden() throws Exception {
        mockMvc.perform(get("/account-plannings/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeGettingAccountPlanningById_thenSucceeds() throws Exception {
        mockMvc.perform(get("/account-plannings/1")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Planning"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.emoji").value("💰"))
                .andExpect(jsonPath("$.color").value("green"));
    }

    @Test
    void whenAllowedEmployeeGettingNonExistentAccountPlanningById_thenNotFound() throws Exception {
        mockMvc.perform(get("/account-plannings/2")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenAdminGettingAccountPlanningById_thenForbidden() throws Exception {
        mockMvc.perform(get("/account-plannings/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousListingAccountPlannings_thenForbidden() throws Exception {
        mockMvc.perform(get("/account-plannings/list"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeListingAccountPlannings_thenSucceeds() throws Exception {
        mockMvc.perform(get("/account-plannings/list")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
    }

    @Test
    void whenAdminListingAccountPlannings_thenForbidden() throws Exception {
        mockMvc.perform(get("/account-plannings/list")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousCreateAccountPlanning_thenForbidden() throws Exception {
        AccountPlanningRegisterDTO planningDTO = new AccountPlanningRegisterDTO(
                "Test Planning",
                "Test Description",
                "💰",
                "green"
        );

        mockMvc.perform(post("/account-plannings/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(planningDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeCreateAccountPlanning_thenSucceeds() throws Exception {
        AccountPlanningRegisterDTO planningDTO = new AccountPlanningRegisterDTO(
                "Test Planning 2",
                "Test Description 2",
                "\uD83D\uDC8A",
                "yellow"
        );

        mockMvc.perform(post("/account-plannings/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(planningDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Planning 2"))
                .andExpect(jsonPath("$.description").value("Test Description 2"))
                .andExpect(jsonPath("$.emoji").value("\uD83D\uDC8A"));
    }

    @Test
    void whenAdminCreateAccountPlanning_thenForbidden() throws Exception {
        AccountPlanningRegisterDTO planningDTO = new AccountPlanningRegisterDTO(
                "Test Planning",
                "Test Description",
                "💰",
                "green"
        );

        mockMvc.perform(post("/account-plannings/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(planningDTO))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousUpdateAccountPlanning_thenForbidden() throws Exception {
        AccountPlanningRegisterDTO planningDTO = new AccountPlanningRegisterDTO(
                "Updated Planning",
                "Updated Description",
                "🏦",
                "green"
        );

        mockMvc.perform(put("/account-plannings/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(planningDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeUpdateAccountPlanning_thenSucceeds() throws Exception {
        AccountPlanningRegisterDTO createDTO = new AccountPlanningRegisterDTO(
                "Original Planning",
                "Original Description",
                "💰",
                "green"
        );

        MvcResult createResult = mockMvc.perform(post("/account-plannings/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Integer planningId = JsonPath.parse(response).read("$.id");

        AccountPlanningRegisterDTO updateDTO = new AccountPlanningRegisterDTO(
                "Updated Planning",
                "Updated Description",
                "🏦",
                "green"
        );

        mockMvc.perform(put("/account-plannings/update/" + planningId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Planning"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.emoji").value("🏦"));
    }

    @Test
    void whenAllowedEmployeeUpdateNonExistentAccountPlanning_thenNotFound() throws Exception {
        AccountPlanningRegisterDTO planningDTO = new AccountPlanningRegisterDTO(
                "Updated Planning",
                "Updated Description",
                "🏦",
                "green"
        );

        mockMvc.perform(put("/account-plannings/update/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(planningDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenAdminUpdateAccountPlanning_thenForbidden() throws Exception {
        AccountPlanningRegisterDTO planningDTO = new AccountPlanningRegisterDTO(
                "Updated Planning",
                "Updated Description",
                "🏦",
                "green"
        );

        mockMvc.perform(put("/account-plannings/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(planningDTO))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousDeleteAccountPlanning_thenForbidden() throws Exception {
        mockMvc.perform(delete("/account-plannings/delete/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeDeleteAccountPlanning_thenSucceeds() throws Exception {
        AccountPlanningRegisterDTO createDTO = new AccountPlanningRegisterDTO(
                "To Delete",
                "Will be deleted",
                "❌",
                "green"
        );

        MvcResult createResult = mockMvc.perform(post("/account-plannings/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Integer planningId = JsonPath.parse(response).read("$.id");

        mockMvc.perform(delete("/account-plannings/delete/" + planningId)
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenAllowedEmployeeDeleteNonExistentAccountPlanning_thenNotFound() throws Exception {
        mockMvc.perform(delete("/account-plannings/delete/999999")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenAdminDeleteAccountPlanning_thenForbidden() throws Exception {
        mockMvc.perform(delete("/account-plannings/delete/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

}

