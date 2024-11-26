package example.medCashFlow;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.medCashFlow.dto.auth.AuthenticationDTO;
import example.medCashFlow.dto.auth.RegisterDTO;
import example.medCashFlow.dto.clinic.ClinicRegisterDTO;
import example.medCashFlow.dto.clinic.ClinicResponseDTO;
import example.medCashFlow.dto.employee.ManagerRegisterDTO;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.model.Employee;
import example.medCashFlow.services.ClinicService;
import example.medCashFlow.services.EmployeeService;
import example.medCashFlow.services.TokenService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MedCashFlowApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployeeService employeeService;

    @Value("${api.security.admin.username}")
    private String adminUsername;

    @Value("${api.security.admin.password}")
    private String adminPassword;

    private String adminToken;

    private String managerToken;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Flyway flyway;

    @Autowired
    private ClinicService clinicService;

    private void createInitialData() {
        RegisterDTO initialRegisterDto = new RegisterDTO(
                new ClinicRegisterDTO("Clinic1", "12345678901234", "1234567890"),
                new ManagerRegisterDTO("Fernando", "Junior", "12345678901", "manager@manager.com", "manager")
        );

        try {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(initialRegisterDto)))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create initial test data", e);
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP SCHEMA public CASCADE; CREATE SCHEMA public;");
        }

        flyway.migrate();

        createInitialData();

        UserDetails admin = User.builder()
                .username(adminUsername)
                .password(new BCryptPasswordEncoder().encode(adminPassword))
                .roles("ADMIN")
                .build();
        adminToken = tokenService.generateTokenForAdmin(admin);

        Employee manager = (Employee) employeeService.getEmployeeByEmail("manager@manager.com");
        managerToken = tokenService.generateToken(manager);
    }

    @Test
    void whenRegisteringNonExistingClinic_thenSucceeds() throws Exception {
        RegisterDTO registerDto = new RegisterDTO(
                new ClinicRegisterDTO("Clinic 2", "12345678901235", "1234567891"),
                new ManagerRegisterDTO("John", "Doe", "12345678902", "manager2@manager.com", "manager")
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
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
    void whenRegisteringExistingManager_thenConflict() throws Exception {
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
    void whenLoginExistingManager_thenSucceeds() throws Exception {
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
    void whenLoginInvalidEmailManager_thenBadCredentials() throws Exception {
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
    void whenLoginExistingInvalidPasswordManager_thenBadCredentials() throws Exception {
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

    @Test
    void whenAnonymousAccessSecuredEndpoint_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/clinics"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAdminAccessClinics_thenSucceeds() throws Exception {
        mockMvc.perform(get("/clinics")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void whenManagerAccessClinics_thenForbidden() throws Exception {
        mockMvc.perform(get("/clinics")
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

        mockMvc.perform(delete("/clinics/delete/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenManagerDeleteClinics_thenForbidden() throws Exception {
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

        UUID id = clinics.get(0).id();

        mockMvc.perform(put("/clinics/activate/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenManagerActivateClinics_thenForbidden() throws Exception {
        List<ClinicResponseDTO> clinics = clinicService.getAllClinics();

        UUID id = clinics.get(0).id();

        mockMvc.perform(put("/clinics/activate/" + id)
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenLoginoInactiveClinic_thenNotFound() throws Exception {
        Employee employee = employeeService.getEmployeeByEmail("manager@manager.com");

        Clinic clinic = employee.getClinic();

        clinic.setIsActive(false);

        clinicService.saveClinic(clinic);

        AuthenticationDTO data = new AuthenticationDTO(
                employee.getEmail(),
                "manager"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(data)))
                .andExpect(status().isNotFound());

    }

}
