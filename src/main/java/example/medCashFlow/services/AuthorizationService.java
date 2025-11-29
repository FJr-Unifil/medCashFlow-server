package example.medCashFlow.services;

import example.medCashFlow.infra.security.UserPrincipal;
import example.medCashFlow.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements UserDetailsService {

    private final EmployeeService employeeService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeService.getEmployeeByEmail(username);
        if (employee == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return new UserPrincipal(employee);
    }
}
