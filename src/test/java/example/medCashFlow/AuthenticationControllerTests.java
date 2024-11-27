package example.medCashFlow;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.medCashFlow.dto.auth.AuthenticationDTO;
import example.medCashFlow.dto.auth.RegisterDTO;
import example.medCashFlow.dto.clinic.ClinicRegisterDTO;
import example.medCashFlow.dto.employee.ManagerRegisterDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationControllerTests extends MedCashFlowApplicationTests {


    @Test
    void whenRegisteringNonExistingClinic_thenSucceeds() throws Exception {
        RegisterDTO registerDto = new RegisterDTO(
                new ClinicRegisterDTO("Clinic 2", "12345678901236", "1234567892"),
                new ManagerRegisterDTO("John", "Doe", "34567890123", "clinicateste@manager.com", "manager")
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDto)))
                .andExpect(status().isOk());
    }

    @Test
    void whenRegisteringExistingClinic_thenConflict() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO(
                new ClinicRegisterDTO("Clinic1", "12345678901234", "1234567890"),
                new ManagerRegisterDTO("João", "Lucas", "12345678903", "manager3@manager.com", "manager3")
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void whenRegisteringExistingEmployee_thenConflict() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO(
                new ClinicRegisterDTO("Clinic3", "12345678901236", "1234567892"),
                new ManagerRegisterDTO("Pedro", "Arthur", "12345678901", "manager@manager.com", "manager")
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void whenLoginExistingEmployee_thenSucceeds() throws Exception {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO(
                "manager@manager.com",
                "manager"
        );
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authenticationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

    }

    @Test
    void whenLoggingInactiveClinic_thenNotFound() throws Exception {
        AuthenticationDTO data = new AuthenticationDTO(
                "manager@manager.com.in",
                "manager"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(data)))
                .andExpect(status().isNotFound());

    }

    @Test
    void whenLoginInvalidEmailEmployee_thenBadCredentials() throws Exception {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO(
                "manager@manager.com.br",
                "manager"
        );
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authenticationDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenLoginExistingInvalidPasswordEmployee_thenBadCredentials() throws Exception {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO(
                "manager@manager.com",
                "manager2"
        );
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authenticationDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenLoginExistingAdmin_thenSucceeds() throws Exception {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO(
                "admin@admin.com",
                "admin123"
        );
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authenticationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void whenLoginInvalidEmailAdmin_thenBadCredentials() throws Exception {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO(
                "admin@admin.com.br",
                "admin123"
        );
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authenticationDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenLoginInvalidPasswordAdmin_thenBadCredentials() throws Exception {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO(
                "admin@admin.com",
                "admin"
        );
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authenticationDTO)))
                .andExpect(status().isUnauthorized());
    }
}
