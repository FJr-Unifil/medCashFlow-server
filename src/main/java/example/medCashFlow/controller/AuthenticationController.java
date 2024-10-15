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
import org.springframework.web.bind.annotation.*;

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
        System.out.println("Dado: " + data);

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());

        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Employee) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterDTO data) {
        System.out.println("Dados: " + data);

        ClinicRegisterDTO clinicData = new ClinicRegisterDTO(
                data.clinic().name(),
                unmaskInput.unmaskNumeric(data.clinic().cnpj()),
                unmaskInput.unmaskNumeric(data.clinic().phone())
        );

        if (!clinicService.isClinicValid(clinicData)) {
            return ResponseEntity.badRequest().build();
        }

        Clinic savedClinic = clinicService.saveClinic(new Clinic(clinicData));

        ManagerRegisterDTO managerData = new ManagerRegisterDTO(
                data.manager().name(),
                unmaskInput.unmaskNumeric(data.manager().cpf()),
                data.manager().email(),
                data.manager().password()
        );

        if (!employeeService.isEmployeeValid(managerData.cpf(), managerData.email())) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(managerData.password());

        Role role = roleService.getRoleById(1L);

        Employee newEmployee = new Employee(managerData, encryptedPassword, role, savedClinic);

        Long id = employeeService.saveEmployeeOnDatabase(newEmployee);

        return ResponseEntity.ok(new RegisterResponseDTO(id, "Clínica cadastrada com sucesso"));
    }
}
