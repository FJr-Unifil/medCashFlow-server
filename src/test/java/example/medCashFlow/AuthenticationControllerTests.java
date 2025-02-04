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

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationControllerTests extends MedCashFlowApplicationTests {

    @Test
    void whenAnonymousGetBillByIdThenForbidden() throws Exception {
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
    void whenRegisteringNonExistingClinicThenSucceeds() throws Exception {
        RegisterDTO registerDto = new RegisterDTO(
                new ClinicRegisterDTO("Clinic 2", "12345678901236", "1234567892"),
                new ManagerRegisterDTO("John", "Doe", "34567890123", "clinicateste@manager.com", "manager")
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.cpf").value("34567890123"))
                .andExpect(jsonPath("$.email").value("clinicateste@manager.com"))
                .andExpect(jsonPath("$.role").value("MANAGER"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void whenRegisteringExistingClinicThenConflict() throws Exception {
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
    void whenRegisteringExistingEmployeeThenConflict() throws Exception {
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
    void whenLoginExistingEmployeeThenSucceeds() throws Exception {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO(
                "manager@manager.com",
                "manager"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authenticationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").value(matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]+$")));
    }

    @Test
    void whenLoggingInactiveClinicThenNotFound() throws Exception {
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
    void whenLoginInvalidEmailEmployeeThenBadCredentials() throws Exception {
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
    void whenLoginExistingInvalidPasswordEmployeeThenBadCredentials() throws Exception {
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
    void whenLoginExistingAdminThenSucceeds() throws Exception {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO(
                "admin@admin.com",
                "admin123"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authenticationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").value(matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]+$")));
    }

    @Test
    void whenLoginInvalidEmailAdminThenBadCredentials() throws Exception {
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
    void whenLoginInvalidPasswordAdminThenBadCredentials() throws Exception {
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
