package example.medCashFlow.controller;

import example.medCashFlow.dto.auth.AuthenticationDTO;
import example.medCashFlow.dto.auth.LoginResponseDTO;
import example.medCashFlow.dto.auth.RegisterDTO;
import example.medCashFlow.dto.clinic.ClinicRegisterDTO;
import example.medCashFlow.dto.employee.EmployeeResponseDTO;
import example.medCashFlow.dto.employee.ManagerRegisterDTO;
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
import org.springframework.security.core.userdetails.UserDetails;
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

        String token;
        if (auth.getPrincipal() instanceof Employee) {
            token = tokenService.generateToken((Employee) auth.getPrincipal());
        } else {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            token = tokenService.generateTokenForAdmin(userDetails);
        }

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<EmployeeResponseDTO> register(@RequestBody RegisterDTO data) {

        ClinicRegisterDTO clinicData = data.clinic();

        ManagerRegisterDTO managerData = data.manager();

        Clinic savedClinic = clinicService.saveClinic(clinicData);

        String encryptedPassword = new BCryptPasswordEncoder().encode(managerData.password());

        Role role = roleService.getRoleById(1L);

        Employee manager = new Employee(managerData, encryptedPassword, role, savedClinic);

        return ResponseEntity.ok(employeeService.saveEmployee(manager));
    }
}
