package example.medCashFlow.controller;

import example.medCashFlow.dto.employee.EmployeeRegisterDTO;
import example.medCashFlow.dto.employee.EmployeeResponseDTO;
import example.medCashFlow.model.Employee;
import example.medCashFlow.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_MANAGER')")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@AuthenticationPrincipal Employee loggedManager, @PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeResponseDTOById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<EmployeeResponseDTO>> listAllEmployees(@AuthenticationPrincipal Employee loggedManager) {
        List<EmployeeResponseDTO> employees = employeeService.getAllEmployeesByClinicId(loggedManager.getClinic().getId());
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/create")
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@AuthenticationPrincipal Employee loggedManager, @RequestBody EmployeeRegisterDTO data) {
        return ResponseEntity.ok(employeeService.createEmployee(data, loggedManager.getClinic()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @AuthenticationPrincipal Employee loggedManager,
            @PathVariable Long id,
            @RequestBody EmployeeRegisterDTO data) {
        return ResponseEntity.ok(employeeService.updateEmployee(data, id));
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<Void> activateEmployee(
            @AuthenticationPrincipal Employee loggedManager,
            @PathVariable Long id) {
        employeeService.activateEmployee(id);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @AuthenticationPrincipal Employee loggedManager,
            @PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }


}
