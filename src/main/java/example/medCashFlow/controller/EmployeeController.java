package example.medCashFlow.controller;

import example.medCashFlow.dto.employee.EmployeeRegisterDTO;
import example.medCashFlow.dto.employee.EmployeeResponseDTO;
import example.medCashFlow.exceptions.ForbiddenException;
import example.medCashFlow.model.Employee;
import example.medCashFlow.services.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@AuthenticationPrincipal UserDetails loggedManager, @PathVariable Long id) {
        if (!(loggedManager instanceof Employee employee)) {
            throw new ForbiddenException();
        }

        return ResponseEntity.ok(employeeService.getEmployeeResponseDTOById(id, employee.getClinic().getId()));
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
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@AuthenticationPrincipal UserDetails loggedManager, @Valid @RequestBody EmployeeRegisterDTO data) {
        if (!(loggedManager instanceof Employee manager)) {
            throw new ForbiddenException();
        }

        return ResponseEntity.ok(employeeService.createEmployee(data, manager.getClinic()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @AuthenticationPrincipal UserDetails loggedManager,
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRegisterDTO data) {

        if (!(loggedManager instanceof Employee employee)) {
            throw new ForbiddenException();
        }

        return ResponseEntity.ok(employeeService.updateEmployee(data, id, employee.getClinic().getId()));
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<Void> activateEmployee(
            @AuthenticationPrincipal UserDetails loggedManager,
            @PathVariable Long id) {

        if (!(loggedManager instanceof Employee employee)) {
            throw new ForbiddenException();
        }

        employeeService.activateEmployee(id, employee.getClinic().getId());
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @AuthenticationPrincipal UserDetails loggedManager,
            @PathVariable Long id) {

        if (!(loggedManager instanceof Employee employee)) {
            throw new ForbiddenException();
        }

        employeeService.deleteEmployee(id, employee.getClinic().getId());
        return ResponseEntity.noContent().build();
    }


}
