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
    void whenAnonymousUpdateInstallmentThenForbidden() throws Exception {
        mockMvc.perform(put("/installments//update/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeUpdateInstallmentThenNoContent() throws Exception {
        InstallmentUpdateDTO installmentUpdateDTO = new InstallmentUpdateDTO(LocalDateTime.now().plusDays(7));

        mockMvc.perform(put("/installments/update/1")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(installmentUpdateDTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenAdminUpdateInstallmentThenForbidden() throws Exception {
        mockMvc.perform(put("/installments/update/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousMarksInstallmentAsPaidThenForbidden() throws Exception {
        mockMvc.perform(put("/installments/mark-as-paid/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeMarksInstallmentAsPaidThenNoContent() throws Exception {
        mockMvc.perform(put("/installments/mark-as-paid/1")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenAdminMarksInstallmentAsPaidThenForbidden() throws Exception {
        mockMvc.perform(put("/installments/mark-as-paid/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousMarkInstallmentAsUnpaidThenForbidden() throws Exception {
        mockMvc.perform(put("/installments/mark-as-unpaid/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeMarkInstallmentAsUnpaidThenNoContent() throws Exception {
        mockMvc.perform(put("/installments/mark-as-paid/1")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(put("/installments/mark-as-unpaid/1")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenAdminMarkInstallmentAsUnpaidThenForbidden() throws Exception {
        mockMvc.perform(put("/installments/mark-as-unpaid/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

}
