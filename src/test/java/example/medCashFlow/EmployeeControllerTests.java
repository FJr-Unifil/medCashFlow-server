package example.medCashFlow;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.medCashFlow.dto.employee.EmployeeRegisterDTO;
import example.medCashFlow.model.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmployeeControllerTests extends MedCashFlowApplicationTests {

    @Test
    void whenAnonymousListingEmployees_thenForbidden() throws Exception {
        mockMvc.perform(get("/employees/list"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenManagerListingEmployees_thenSucceeds() throws Exception {
        mockMvc.perform(get("/employees/list")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
    }

    @Test
    void whenAdminListingEmployees_thenForbidden() throws Exception {
        mockMvc.perform(get("/employees/list")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAnonymousCreateEmployee_thenForbidden() throws Exception {
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
    void whenManagerCreateEmployee_thenSucceeds() throws Exception {
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
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void whenAdminCreateEmployee_thenForbidden() throws Exception {
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
    void whenAnonymousUpdateEmployee_thenForbidden() throws Exception {
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
    void whenManagerUpdateEmployee_thenSucceeds() throws Exception {
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
                .andExpect(jsonPath("$.lastName").value("Name"));
    }

    @Test
    void whenManagerUpdateEmployeeWithExistingEmail_thenConflict() throws Exception {
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
    void whenAdminUpdateEmployee_thenForbidden() throws Exception {
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
    void whenAnonymousDeleteEmployee_thenForbidden() throws Exception {
        mockMvc.perform(delete("/employees/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenManagerDeleteEmployee_thenSucceeds() throws Exception {
        EmployeeRegisterDTO newEmployeeDTO = new EmployeeRegisterDTO(
                "To",
                "Delete",
                "12345678907",
                "todelete@example.com",
                "password123",
                2L
        );

        MvcResult result = mockMvc.perform(post("/employees/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newEmployeeDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andReturn();

        Employee createdEmployee = employeeService.getEmployeeByEmail("todelete@example.com");

        mockMvc.perform(delete("/employees/delete/" + createdEmployee.getId())
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());

        Employee deletedEmployee = employeeService.getEmployeeByEmail("todelete@example.com");
        assertFalse(deletedEmployee.isActive());
    }

    @Test
    void whenAdminDeleteEmployee_thenForbidden() throws Exception {
        mockMvc.perform(delete("/employees/delete/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenManagerUpdateNonExistentEmployee_thenNotFound() throws Exception {
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
    void whenManagerDeleteNonExistentEmployee_thenNotFound() throws Exception {
        mockMvc.perform(delete("/employees/delete/999999")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenAnonymousActivateEmployee_thenForbidden() throws Exception {
        mockMvc.perform(put("/employees/activate/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenManagerActivateEmployee_thenSucceeds() throws Exception {
        EmployeeRegisterDTO newEmployeeDTO = new EmployeeRegisterDTO(
                "To",
                "Activate",
                "12345678908",
                "toactivate@example.com",
                "password123",
                2L
        );

        mockMvc.perform(post("/employees/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newEmployeeDTO))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());

        Employee createdEmployee = employeeService.getEmployeeByEmail("toactivate@example.com");

        mockMvc.perform(delete("/employees/delete/" + createdEmployee.getId())
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(put("/employees/activate/" + createdEmployee.getId())
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());

        Employee activatedEmployee = employeeService.getEmployeeByEmail("toactivate@example.com");
        assertTrue(activatedEmployee.isActive());
    }

    @Test
    void whenAdminActivateEmployee_thenForbidden() throws Exception {
        mockMvc.perform(put("/employees/activate/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenManagerActivateNonExistentEmployee_thenNotFound() throws Exception {
        mockMvc.perform(put("/employees/activate/999999") // non-existent ID
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenManagerActivateAlreadyActiveEmployee_thenSucceeds() throws Exception {
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
