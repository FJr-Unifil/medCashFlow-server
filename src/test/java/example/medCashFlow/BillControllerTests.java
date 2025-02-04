package example.medCashFlow;

import example.medCashFlow.dto.bill.BillRegisterDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BillControllerTests extends MedCashFlowApplicationTests {

    @Test
    void whenAnonymousListingBillsThenForbidden() throws Exception {
        mockMvc.perform(get("/bills/list"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeListingBillsThenSucceeds() throws Exception {
        mockMvc.perform(get("/bills/list")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
    }

    @Test
    void whenAdminListingBillsThenForbidden() throws Exception {
        mockMvc.perform(get("/bills/list")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousCreateBillThenForbidden() throws Exception {
        BillRegisterDTO billDTO = new BillRegisterDTO(
                "Test Bill",
                100.00,
                "INCOME",
                1L,
                1L,
                1L,
                LocalDateTime.now().plusDays(30),
                1
        );

        mockMvc.perform(post("/bills/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(billDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeCreateBillThenSucceeds() throws Exception {
        BillRegisterDTO billDTO = new BillRegisterDTO(
                "Test Bill",
                100.00,
                "INCOME",
                1L,
                1L,
                1L,
                LocalDateTime.now().plusDays(30),
                1
        );

        mockMvc.perform(post("/bills/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(billDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isCreated());
    }

    @Test
    void whenAdminCreateBillThenForbidden() throws Exception {
        BillRegisterDTO billDTO = new BillRegisterDTO(
                "Test Bill",
                100.00,
                "INCOME",
                1L,
                1L,
                1L,
                LocalDateTime.now().plusDays(30),
                1
        );

        mockMvc.perform(post("/bills/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(billDTO))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousUpdateBillThenForbidden() throws Exception {
        BillRegisterDTO billDTO = new BillRegisterDTO(
                "Updated Bill",
                200.00,
                "OUTCOME",
                1L,
                1L,
                1L,
                LocalDateTime.now().plusDays(30),
                1
        );

        mockMvc.perform(put("/bills/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(billDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeUpdateBillThenSucceeds() throws Exception {
        BillRegisterDTO updateDTO = new BillRegisterDTO(
                "Updated Bill",
                200.00,
                "OUTCOME",
                1L,
                1L,
                1L,
                LocalDateTime.now().plusDays(30),
                1
        );

        mockMvc.perform(put("/bills/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenAnonymousDeleteBillThenForbidden() throws Exception {
        mockMvc.perform(delete("/bills/delete/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeDeleteBillThenSucceeds() throws Exception {
        mockMvc.perform(delete("/bills/delete/1")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenAdminDeleteBillThenForbidden() throws Exception {
        mockMvc.perform(delete("/bills/delete/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeUpdateNonExistentBillThenNotFound() throws Exception {
        BillRegisterDTO billDTO = new BillRegisterDTO(
                "Non-existent Bill",
                100.00,
                "INCOME",
                1L,
                1L,
                1L,
                LocalDateTime.now().plusDays(30),
                1
        );

        mockMvc.perform(put("/bills/update/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(billDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenAllowedEmployeeDeleteNonExistentBillThenNotFound() throws Exception {
        mockMvc.perform(delete("/bills/delete/999999")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

}