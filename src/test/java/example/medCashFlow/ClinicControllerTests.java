package example.medCashFlow;

import example.medCashFlow.dto.clinic.ClinicResponseDTO;
import example.medCashFlow.services.ClinicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ClinicControllerTests extends MedCashFlowApplicationTests {

    @Autowired
    private ClinicService clinicService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenAnonymousAccessClinics_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/clinics"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAdminAccessClinics_thenSucceeds() throws Exception {
        mockMvc.perform(get("/clinics/list")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].cnpj").exists())
                .andExpect(jsonPath("$[0].phone").exists())
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].isActive").exists());
    }

    @Test
    void whenEmployeeAccessClinics_thenForbidden() throws Exception {
        mockMvc.perform(get("/clinics/list")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousDeleteClinics_thenUnauthorized() throws Exception {
        List<ClinicResponseDTO> clinics = clinicService.getAllClinics();

        UUID id = clinics.get(0).id();

        mockMvc.perform(delete("/clinics/delete/" + id)).andExpect(status().isForbidden());
    }

    @Test
    void whenAdminDeleteClinics_thenSucceeds() throws Exception {
        List<ClinicResponseDTO> clinics = clinicService.getAllClinics();
        UUID id = clinics.get(0).id();

        mockMvc.perform(get("/clinics/list")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + id + "')].isActive").value(true));

        mockMvc.perform(delete("/clinics/delete/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/clinics/list")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + id + "')].isActive").value(false));
    }

    @Test
    void whenEmployeeDeleteClinics_thenForbidden() throws Exception {
        List<ClinicResponseDTO> clinics = clinicService.getAllClinics();

        UUID id = clinics.get(0).id();

        mockMvc.perform(delete("/clinics/delete/" + id)
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousActivateClinics_thenUnauthorized() throws Exception {
        List<ClinicResponseDTO> clinics = clinicService.getAllClinics();

        UUID id = clinics.get(0).id();

        mockMvc.perform(put("/clinics/activate/" + id)).andExpect(status().isForbidden());
    }

    @Test
    void whenAdminActivateClinics_thenSucceeds() throws Exception {
        List<ClinicResponseDTO> clinics = clinicService.getAllClinics();
        UUID id = clinics.get(1).id();

        mockMvc.perform(put("/clinics/activate/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/clinics/list")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + id + "')].isActive").value(true));
    }

    @Test
    void whenEmployeeActivateClinics_thenForbidden() throws Exception {
        List<ClinicResponseDTO> clinics = clinicService.getAllClinics();

        UUID id = clinics.get(0).id();

        mockMvc.perform(put("/clinics/activate/" + id)
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isForbidden());
    }

}
