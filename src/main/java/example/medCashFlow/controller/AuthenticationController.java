package example.medCashFlow.controller;

import example.medCashFlow.dto.auth.AuthenticationDTO;
import example.medCashFlow.dto.auth.LoginResponseDTO;
import example.medCashFlow.dto.auth.RegisterDTO;
import example.medCashFlow.dto.clinic.ClinicRegisterDTO;
import example.medCashFlow.dto.employee.EmployeeRegisterDTO;
import example.medCashFlow.dto.employee.EmployeeResponseDTO;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.model.Employee;
import example.medCashFlow.services.ClinicService;
import example.medCashFlow.services.EmployeeService;
import example.medCashFlow.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
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
        EmployeeRegisterDTO managerData = data.manager();

        Clinic savedClinic = clinicService.createClinic(clinicData);

        return ResponseEntity.ok(employeeService.createEmployee(managerData, savedClinic));
    }
}
