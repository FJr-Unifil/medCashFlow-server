package example.medCashFlow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import example.medCashFlow.dto.accountPlanning.AccountPlanningRegisterDTO;
import example.medCashFlow.dto.auth.RegisterDTO;
import example.medCashFlow.dto.clinic.ClinicRegisterDTO;
import example.medCashFlow.dto.employee.EmployeeRegisterDTO;
import example.medCashFlow.dto.employee.ManagerRegisterDTO;
import example.medCashFlow.dto.involved.InvolvedRegisterDTO;
import example.medCashFlow.exceptions.ClinicNotFoundException;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.model.Employee;
import example.medCashFlow.repository.ClinicRepository;
import example.medCashFlow.repository.EmployeeRepository;
import example.medCashFlow.services.EmployeeService;
import example.medCashFlow.services.TokenService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class MedCashFlowApplicationTests {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected TokenService tokenService;

    @Autowired
    protected EmployeeService employeeService;

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Value("${api.security.admin.username}")
    protected String adminUsername;

    @Value("${api.security.admin.password}")
    protected String adminPassword;

    protected String adminToken;

    protected String managerToken;

    protected String financialAnalystToken;

    protected String doctorToken;

    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected Flyway flyway;

    @Autowired
    protected ClinicRepository clinicRepository;

    protected final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    protected void createInitialData() {
        createActiveClinic();
        createInactiveClinic();
    }

    protected void createActiveClinic() {
        RegisterDTO activeRegisterDto = new RegisterDTO(
                new ClinicRegisterDTO("Active Clinic", "12345678901234", "1234567890"),
                new ManagerRegisterDTO("Fernando", "Junior", "12345678901", "manager@manager.com", "manager")
        );


        try {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(activeRegisterDto)))
                    .andExpect(status().isOk());

            Employee manager = employeeService.getEmployeeByEmail("manager@manager.com");
            managerToken = tokenService.generateToken(manager);

            createActiveFinancialAnalyst();
            createInactiveFinancialAnalyst();
            createActiveDoctor();
            createInactiveDoctor();
            createActiveInvolved();
            createAccountPlanning();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create initial test data", e);
        }
    }

    protected void createActiveFinancialAnalyst() {
        EmployeeRegisterDTO employeeRegisterDto = new EmployeeRegisterDTO(
                "João",
                "Pé de Feijão",
                "12345678902",
                "financial@financial.com",
                "financial",
                2L
        );

        try {
            mockMvc.perform(post("/employees/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(employeeRegisterDto))
                            .header("Authorization", "Bearer " + managerToken))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void createInactiveFinancialAnalyst() {
        EmployeeRegisterDTO employeeRegisterDto = new EmployeeRegisterDTO(
                "Francisco",
                "Xavier",
                "12345678903",
                "financial2@financial.com",
                "financial",
                2L
        );

        try {
            mockMvc.perform(post("/employees/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(employeeRegisterDto))
                            .header("Authorization", "Bearer " + managerToken))
                    .andExpect(status().isOk());

            Long id = employeeService.getEmployeeByEmail("financial2@financial.com").getId();

            mockMvc.perform(delete("/employees/delete/" + id)
                            .header("Authorization", "Bearer " + managerToken))
                    .andExpect(status().isNoContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void createActiveDoctor() {
        EmployeeRegisterDTO employeeRegisterDto = new EmployeeRegisterDTO(
                "José",
                "Silva",
                "12345678904",
                "doctor@doctor.com",
                "doctor",
                3L
        );

        try {
            mockMvc.perform(post("/employees/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(employeeRegisterDto))
                            .header("Authorization", "Bearer " + managerToken))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void createInactiveDoctor() {
        EmployeeRegisterDTO employeeRegisterDto = new EmployeeRegisterDTO(
                "Mauricio",
                "Meirelles",
                "12345678905",
                "doctor2@doctor.com",
                "doctor",
                2L
        );

        try {
            mockMvc.perform(post("/employees/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(employeeRegisterDto))
                            .header("Authorization", "Bearer " + managerToken))
                    .andExpect(status().isOk());

            Long id = employeeService.getEmployeeByEmail("doctor@doctor.com").getId();

            mockMvc.perform(delete("/employees/delete/" + id)
                            .header("Authorization", "Bearer " + managerToken))
                    .andExpect(status().isNoContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void createActiveInvolved() {
        InvolvedRegisterDTO involvedRegisterDTO = new InvolvedRegisterDTO(
                "Saúde Equipamentos LTDA",
                "99999999999999",
                "9999999999",
                "saudeequipamentos@gmail.com"
        );

        try {
            mockMvc.perform(post("/involveds/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(involvedRegisterDTO))
                            .header("Authorization", "Bearer " + managerToken))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void createAccountPlanning() {
        AccountPlanningRegisterDTO planningDTO = new AccountPlanningRegisterDTO(
                "Test Planning",
                "Test Description",
                "💰",
                "green"
        );

        try {
            try {
                mockMvc.perform(post("/employees/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(planningDTO))
                                .header("Authorization", "Bearer " + managerToken))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void createInactiveClinic() {
        RegisterDTO activeRegisterDto = new RegisterDTO(
                new ClinicRegisterDTO("Inactive Clinic", "12345678901235", "1234567891"),
                new ManagerRegisterDTO("Fernando", "Junior", "23456789012", "manager@manager.com.in", "manager")
        );


        try {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(activeRegisterDto)))
                    .andExpect(status().isOk());

            Clinic clinic = clinicRepository.findByCnpj("12345678901235").orElseThrow(ClinicNotFoundException::new);

            clinic.setIsActive(false);
            clinicRepository.save(clinic);
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

        Employee manager = employeeService.getEmployeeByEmail("manager@manager.com");
        managerToken = tokenService.generateToken(manager);

        Employee financialAnalyst = (Employee) employeeService.getEmployeeByEmail("financial@financial.com");
        financialAnalystToken = tokenService.generateToken(financialAnalyst);

        Employee doctor = (Employee) employeeService.getEmployeeByEmail("doctor@doctor.com");
        doctorToken = tokenService.generateToken(doctor);
    }


}
