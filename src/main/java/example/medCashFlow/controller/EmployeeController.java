package example.medCashFlow.controller;

import example.medCashFlow.dto.employee.EmployeeRegisterDTO;
import example.medCashFlow.dto.employee.EmployeeResponseDTO;
import example.medCashFlow.exceptions.ForbiddenException;
import example.medCashFlow.model.Employee;
import example.medCashFlow.model.Role;
import example.medCashFlow.services.EmployeeService;
import example.medCashFlow.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    private final RoleService roleService;

    @GetMapping("/list")
    public ResponseEntity<List<EmployeeResponseDTO>> listAllEmployees(@AuthenticationPrincipal UserDetails loggedManager) {
        if (!(loggedManager instanceof Employee)) {
            throw new ForbiddenException();
        }

        List<EmployeeResponseDTO> employees = employeeService.getAllEmployeesByClinicId(((Employee) loggedManager).getClinic().getId());
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/create")
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@AuthenticationPrincipal UserDetails loggedManager, @RequestBody EmployeeRegisterDTO data) {
        if (!(loggedManager instanceof Employee manager)) {
            throw new ForbiddenException();
        }

        Employee newEmployee = new Employee(data);

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        newEmployee.setPassword(encryptedPassword);

        Role role = roleService.getRoleById(data.roleId());

        newEmployee.setRole(role);

        newEmployee.setClinic(manager.getClinic());

        return ResponseEntity.ok(employeeService.saveEmployee(newEmployee));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @AuthenticationPrincipal UserDetails loggedManager,
            @PathVariable Long id,
            @RequestBody EmployeeRegisterDTO data) {

        if (!(loggedManager instanceof Employee manager)) {
            throw new ForbiddenException();
        }

        Employee employeeToUpdate = new Employee(data);
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        employeeToUpdate.setPassword(encryptedPassword);
        employeeToUpdate.setRole(roleService.getRoleById(data.roleId()));
        employeeToUpdate.setClinic(manager.getClinic());

        return ResponseEntity.ok(employeeService.updateEmployee(employeeToUpdate, id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @AuthenticationPrincipal UserDetails loggedManager,
            @PathVariable Long id) {

        if (!(loggedManager instanceof Employee)) {
            throw new ForbiddenException();
        }

        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }


}
