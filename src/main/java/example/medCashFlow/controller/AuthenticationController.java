package example.medCashFlow.controller;

import example.medCashFlow.dto.*;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.model.Employee;
import example.medCashFlow.model.Role;
import example.medCashFlow.services.ClinicService;
import example.medCashFlow.services.EmployeeService;
import example.medCashFlow.services.RoleService;
import example.medCashFlow.services.TokenService;
import example.medCashFlow.util.UnmaskInput;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final EmployeeService employeeService;

    private final ClinicService clinicService;

    private final RoleService roleService;

    private final TokenService tokenService;

    private final UnmaskInput unmaskInput;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());

        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Employee) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO data) {
        ClinicRegisterDTO clinicData = new ClinicRegisterDTO(
                data.clinicName(),
                unmaskInput.unmaskNumeric(data.cnpj()),
                unmaskInput.unmaskNumeric(data.clinicPhone())
        );

        if (!clinicService.isClinicValid(clinicData)) {
            return ResponseEntity.badRequest().build();
        }

        Clinic newClinic = new Clinic(clinicData);
        Clinic savedClinic = clinicService.saveClinic(newClinic);

        Role role = roleService.getRoleById(1L);

        EmployeeRegisterDTO employeeData = new EmployeeRegisterDTO(
                data.managerName(),
                unmaskInput.unmaskNumeric(data.cpf()),
                data.managerEmail(),
                data.managerPassword(),
                role.getId(),
                savedClinic.getId()
        );

        if (!employeeService.isEmployeeValid(data.cpf(), data.managerEmail())) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.managerPassword());

        Employee newEmployee = new Employee(employeeData, encryptedPassword, role, savedClinic);

        Long id = employeeService.saveEmployeeOnDatabase(newEmployee);

        return ResponseEntity.ok("Id do funcionário: " + id);
    }
}
