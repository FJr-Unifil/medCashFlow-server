package example.medCashFlow.controller;

import example.medCashFlow.dto.AuthenticationDTO;
import example.medCashFlow.dto.LoginResponseDTO;
import example.medCashFlow.dto.RegisterDTO;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.model.Employee;
import example.medCashFlow.model.Role;
import example.medCashFlow.services.ClinicService;
import example.medCashFlow.services.EmployeeService;
import example.medCashFlow.services.RoleService;
import example.medCashFlow.services.TokenService;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());

        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Employee) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO data) {
        if (!employeeService.isEmployeeValid(data.cpf(), data.email())) {
            return ResponseEntity.badRequest().build();
        }

        if (!roleService.isRoleValid(data.roleId()) || !clinicService.isClinicValid(data.clinicId())) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        Role role = roleService.getRoleById(data.roleId());
        Clinic clinic = clinicService.getClinicById(data.clinicId());
        Employee newEmployee = new Employee(data, encryptedPassword, role, clinic);

        Long id = employeeService.saveEmployeeOnDatabase(newEmployee);

        return ResponseEntity.ok("Id do funcionário: " + id);
    }
}
