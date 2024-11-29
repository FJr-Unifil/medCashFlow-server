package example.medCashFlow;

import example.medCashFlow.dto.bill.BillRegisterDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BillControllerTests extends MedCashFlowApplicationTests {

    @Test
    void whenAnonymousListingBills_thenForbidden() throws Exception {
        mockMvc.perform(get("/bills/list"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenManagerListingBills_thenSucceeds() throws Exception {
        mockMvc.perform(get("/bills/list")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
    }

    @Test
    void whenAdminListingBills_thenForbidden() throws Exception {
        mockMvc.perform(get("/bills/list")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousCreateBill_thenForbidden() throws Exception {
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
    void whenManagerCreateBill_thenSucceeds() throws Exception {
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Bill"))
                .andExpect(jsonPath("$.pricing").value(100.00))
                .andExpect(jsonPath("$.type").value("INCOME"));
    }

    @Test
    void whenAdminCreateBill_thenForbidden() throws Exception {
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
    void whenAnonymousUpdateBill_thenForbidden() throws Exception {
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
    void whenManagerUpdateBill_thenSucceeds() throws Exception {
        BillRegisterDTO createDTO = new BillRegisterDTO(
                "Original Bill",
                100.00,
                "INCOME",
                1L,
                1L,
                1L,
                LocalDateTime.now().plusDays(30),
                1
        );

        String createResponse = mockMvc.perform(post("/bills/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        long billId = objectMapper.readTree(createResponse).get("id").asLong();

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

        mockMvc.perform(put("/bills/update/" + billId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Bill"))
                .andExpect(jsonPath("$.pricing").value(200.00))
                .andExpect(jsonPath("$.type").value("OUTCOME"));
    }

    @Test
    void whenAnonymousDeleteBill_thenForbidden() throws Exception {
        mockMvc.perform(delete("/bills/delete/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenManagerDeleteBill_thenSucceeds() throws Exception {
        BillRegisterDTO createDTO = new BillRegisterDTO(
                "Bill to Delete",
                100.00,
                "INCOME",
                1L,
                1L,
                1L,
                LocalDateTime.now().plusDays(30),
                1
        );

        String createResponse = mockMvc.perform(post("/bills/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        long billId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(delete("/bills/delete/" + billId)
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenAdminDeleteBill_thenForbidden() throws Exception {
        mockMvc.perform(delete("/bills/delete/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenManagerUpdateNonExistentBill_thenNotFound() throws Exception {
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
    void whenManagerDeleteNonExistentBill_thenNotFound() throws Exception {
        mockMvc.perform(delete("/bills/delete/999999")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }
}

