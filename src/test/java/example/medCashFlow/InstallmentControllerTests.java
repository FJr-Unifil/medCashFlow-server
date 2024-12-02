package example.medCashFlow;

import example.medCashFlow.dto.bill.InstallmentUpdateDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InstallmentControllerTests extends MedCashFlowApplicationTests {

    @Test
    void whenAnonymousUpdateInstallment_thenForbidden() throws Exception {
        mockMvc.perform(put("/installments//update/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeUpdateInstallment_thenNoContent() throws Exception {
        InstallmentUpdateDTO installmentUpdateDTO = new InstallmentUpdateDTO(LocalDateTime.now().plusDays(7));

        mockMvc.perform(put("/installments/update/1")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(installmentUpdateDTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenAdminUpdateInstallment_thenForbidden() throws Exception {
        mockMvc.perform(put("/installments/update/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousMarksInstallmentAsPaid_thenForbidden() throws Exception {
        mockMvc.perform(put("/installments/mark-as-paid/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeMarksInstallmentAsPaid_thenNoContent() throws Exception {
        mockMvc.perform(put("/installments/mark-as-paid/1")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenAdminMarksInstallmentAsPaid_thenForbidden() throws Exception {
        mockMvc.perform(put("/installments/mark-as-paid/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousMarkInstallmentAsUnpaid_thenForbidden() throws Exception {
        mockMvc.perform(put("/installments/mark-as-unpaid/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeMarkInstallmentAsUnpaid_thenNoContent() throws Exception {
        mockMvc.perform(put("/installments/mark-as-paid/1")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(put("/installments/mark-as-unpaid/1")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenAdminMarkInstallmentAsUnpaid_thenForbidden() throws Exception {
        mockMvc.perform(put("/installments/mark-as-unpaid/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

}
