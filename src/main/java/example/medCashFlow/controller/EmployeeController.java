package example.medCashFlow.controller;

import example.medCashFlow.dto.employee.EmployeeRegisterDTO;
import example.medCashFlow.dto.employee.EmployeeResponseDTO;
import example.medCashFlow.exceptions.ForbiddenException;
import example.medCashFlow.model.Employee;
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

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@AuthenticationPrincipal UserDetails loggedManager, @PathVariable Long id) {
        if (!(loggedManager instanceof Employee)) {
            throw new ForbiddenException();
        }

        Employee employee = employeeService.getEmployeeById(id);

        EmployeeResponseDTO employeeResponseDTO = new EmployeeResponseDTO(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getCpf(),
                employee.getEmail(),
                employee.getRole().getName(),
                employee.isActive()
        );

        return ResponseEntity.ok(employeeResponseDTO);
    }

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

        Employee newEmployee = employeeService.toEmployee(data);;
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

        Employee employeeToUpdate = employeeService.toEmployee(data);
        employeeToUpdate.setId(id);
        employeeToUpdate.setClinic(manager.getClinic());

        return ResponseEntity.ok(employeeService.updateEmployee(employeeToUpdate));
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<Void> activateEmployee(
            @AuthenticationPrincipal UserDetails loggedManager,
            @PathVariable Long id) {

        if (!(loggedManager instanceof Employee)) {
            throw new ForbiddenException();
        }

        employeeService.activateEmployee(id);
        return ResponseEntity.noContent().build();
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
