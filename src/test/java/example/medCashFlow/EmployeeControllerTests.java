package example.medCashFlow;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.medCashFlow.dto.employee.EmployeeRegisterDTO;
import example.medCashFlow.model.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmployeeControllerTests extends MedCashFlowApplicationTests {

    @Test
    void whenAnonymousGettingEmployeeByIdThenForbidden() throws Exception {
        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeGettingEmployeeByIdThenSucceeds() throws Exception {
        mockMvc.perform(get("/employees/1")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.cpf").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.role").exists())
                .andExpect(jsonPath("$.isActive").exists());
    }

    @Test
    void whenAllowedEmployeeGettingNonExistentEmployeeByIdThenSucceeds() throws Exception {
        mockMvc.perform(get("/employees/999")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNotAllowedEmployeeGettingEmployeeByIdThenForbidden() throws Exception {
        mockMvc.perform(get("/employees/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousListingEmployeesThenForbidden() throws Exception {
        mockMvc.perform(get("/employees/list"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeListingEmployeesThenSucceeds() throws Exception {
        mockMvc.perform(get("/employees/list")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].firstName").exists())
                .andExpect(jsonPath("$[0].lastName").exists())
                .andExpect(jsonPath("$[0].cpf").exists())
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[0].role").exists())
                .andExpect(jsonPath("$[0].isActive").exists());
    }

    @Test
    void whenNotAllowedEmployeeListingEmployeesThenForbidden() throws Exception {
        mockMvc.perform(get("/employees/list")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousCreateEmployeeThenForbidden() throws Exception {
        EmployeeRegisterDTO employeeDTO = new EmployeeRegisterDTO(
                "John",
                "Doe",
                "12345678903",
                "john@example.com",
                "password123",
                2L
        );

        mockMvc.perform(post("/employees/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(employeeDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeCreateEmployeeThenSucceeds() throws Exception {
        EmployeeRegisterDTO employeeDTO = new EmployeeRegisterDTO(
                "John",
                "Doe",
                "12345678910",
                "john@example.com",
                "password123",
                2L
        );

        mockMvc.perform(post("/employees/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(employeeDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("FINANCIAL_ANALYST"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void whenNotAllowedEmployeeCreateEmployeeThenForbidden() throws Exception {
        EmployeeRegisterDTO employeeDTO = new EmployeeRegisterDTO(
                "John",
                "Doe",
                "12345678903",
                "john@example.com",
                "password123",
                2L
        );

        mockMvc.perform(post("/employees/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(employeeDTO))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousUpdateEmployeeThenForbidden() throws Exception {
        EmployeeRegisterDTO employeeDTO = new EmployeeRegisterDTO(
                "Updated",
                "Name",
                "12345678904",
                "updated@example.com",
                "newpassword",
                2L
        );

        mockMvc.perform(put("/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(employeeDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeUpdateEmployeeThenSucceeds() throws Exception {
        Employee existingEmployee = employeeService.getEmployeeByEmail("manager@manager.com");

        EmployeeRegisterDTO employeeDTO = new EmployeeRegisterDTO(
                "Updated",
                "Name",
                existingEmployee.getCpf(),
                existingEmployee.getEmail(),
                "newpassword",
                2L
        );

        mockMvc.perform(put("/employees/update/" + existingEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(employeeDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.email").value(existingEmployee.getEmail()))
                .andExpect(jsonPath("$.cpf").value(existingEmployee.getCpf()))
                .andExpect(jsonPath("$.role").exists())
                .andExpect(jsonPath("$.isActive").exists());
    }

    @Test
    void whenAllowedEmployeeUpdateEmployeeWithExistingEmailThenConflict() throws Exception {
        EmployeeRegisterDTO newEmployeeDTO = new EmployeeRegisterDTO(
                "New",
                "Employee",
                "12345678911",
                "new@example.com",
                "password123",
                2L
        );

        mockMvc.perform(post("/employees/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newEmployeeDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());

        Employee existingEmployee = employeeService.getEmployeeByEmail("manager@manager.com");

        EmployeeRegisterDTO updateDTO = new EmployeeRegisterDTO(
                "Updated",
                "Name",
                "12345678912",
                "new@example.com",
                "password123",
                2L
        );

        mockMvc.perform(put("/employees/update/" + existingEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isConflict());
    }

    @Test
    void whenNotAllowedEmployeeUpdateEmployeeThenForbidden() throws Exception {
        EmployeeRegisterDTO employeeDTO = new EmployeeRegisterDTO(
                "Updated",
                "Name",
                "12345678904",
                "updated@example.com",
                "newpassword",
                2L
        );

        mockMvc.perform(put("/employees/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(employeeDTO))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousDeleteEmployeeThenForbidden() throws Exception {
        mockMvc.perform(delete("/employees/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeDeleteEmployeeThenSucceeds() throws Exception {
        Employee employeeToDelete = employeeService.getEmployeeByEmail("financial@financial.com");

        assertTrue(employeeToDelete.isActive());

        mockMvc.perform(delete("/employees/delete/" + employeeToDelete.getId())
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());

        Employee deletedEmployee = employeeService.getEmployeeByEmail("financial@financial.com");
        assertFalse(deletedEmployee.isActive());
        assertNotNull(deletedEmployee.getId());
        assertEquals("João", deletedEmployee.getFirst_name());
        assertEquals("Pé de Feijão", deletedEmployee.getLast_name());
        assertEquals("financial@financial.com", deletedEmployee.getEmail());
    }

    @Test
    void whenNotAllowedEmployeeDeleteEmployeeThenForbidden() throws Exception {
        mockMvc.perform(delete("/employees/delete/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeUpdateNonExistentEmployeeThenNotFound() throws Exception {
        EmployeeRegisterDTO employeeDTO = new EmployeeRegisterDTO(
                "Updated",
                "Name",
                "12345678904",
                "updated@example.com",
                "newpassword",
                2L
        );

        mockMvc.perform(put("/employees/update/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(employeeDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenAllowedEmployeeDeleteNonExistentEmployeeThenNotFound() throws Exception {
        mockMvc.perform(delete("/employees/delete/999999")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenAnonymousActivateEmployeeThenForbidden() throws Exception {
        mockMvc.perform(put("/employees/activate/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeActivateEmployeeThenSucceeds() throws Exception {
        Employee employeeToActivate = employeeService.getEmployeeByEmail("financial2@financial.com");

        assertFalse(employeeToActivate.isActive());

        mockMvc.perform(put("/employees/activate/" + employeeToActivate.getId())
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());

        Employee activatedEmployee = employeeService.getEmployeeByEmail("financial2@financial.com");
        assertTrue(activatedEmployee.isActive());
        assertNotNull(activatedEmployee.getId());
        assertEquals("Francisco", activatedEmployee.getFirst_name());
        assertEquals("Xavier", activatedEmployee.getLast_name());
        assertEquals("financial2@financial.com", activatedEmployee.getEmail());
    }

    @Test
    void whenNotAllowedEmployeeActivateEmployeeThenForbidden() throws Exception {
        mockMvc.perform(put("/employees/activate/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAllowedEmployeeActivateNonExistentEmployeeThenNotFound() throws Exception {
        mockMvc.perform(put("/employees/activate/999999") // non-existent ID
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenAllowedEmployeeActivateAlreadyActiveEmployeeThenSucceeds() throws Exception {
        EmployeeRegisterDTO newEmployeeDTO = new EmployeeRegisterDTO(
                "Already",
                "Active",
                "12345678909",
                "alreadyactive@example.com",
                "password123",
                2L
        );

        mockMvc.perform(post("/employees/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newEmployeeDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());

        Employee createdEmployee = employeeService.getEmployeeByEmail("alreadyactive@example.com");

        mockMvc.perform(put("/employees/activate/" + createdEmployee.getId())
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());

        Employee stillActiveEmployee = employeeService.getEmployeeByEmail("alreadyactive@example.com");
        assertTrue(stillActiveEmployee.isActive());
    }

}
