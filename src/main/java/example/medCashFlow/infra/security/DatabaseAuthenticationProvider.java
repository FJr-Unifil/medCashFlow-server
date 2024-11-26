package example.medCashFlow.infra.security;

import example.medCashFlow.model.Employee;
import example.medCashFlow.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DatabaseAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private EmployeeRepository employeeRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${api.security.admin.username}")
    private String adminUsername;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {

        String email = auth.getName();
        String password = auth.getCredentials().toString();


        if (email.equals(adminUsername)) {
            return null;
        }

        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);

        if (optionalEmployee.isEmpty()) {
            throw new BadCredentialsException("Invalid credentials");
        }

        Employee employee = optionalEmployee.get();

        if (!employee.getClinic().getIsActive()) {
            throw new DisabledException("Clinic is not active");
        }

        if (!employee.isActive()) {
            throw new DisabledException("User is not active");
        }

        if (!passwordEncoder.matches(password, employee.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return new UsernamePasswordAuthenticationToken(
                employee,
                null,
                employee.getAuthorities()
        );

    }

    @Override
    public boolean supports(Class<?> auth) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(auth);
    }

}
