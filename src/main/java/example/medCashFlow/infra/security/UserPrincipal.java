package example.medCashFlow.infra.security;

import example.medCashFlow.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {
    
    private final Employee employee;
    
    public Employee getEmployee() {
        return employee;
    }
    
    public UUID getClinicId() {
        return employee.getClinic().getId();
    }
    
    public String getRole() {
        return employee.getRole().getName();
    }
    
    public boolean hasRole(String role) {
        return getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return switch (employee.getRole().getName()) {
            case "MANAGER" -> List.of(
                    new SimpleGrantedAuthority("ROLE_MANAGER"),
                    new SimpleGrantedAuthority("ROLE_FINANCIAL_ANALYST"),
                    new SimpleGrantedAuthority("ROLE_DOCTOR")
            );
            case "FINANCIAL_ANALYST" -> List.of(
                    new SimpleGrantedAuthority("ROLE_FINANCIAL_ANALYST"),
                    new SimpleGrantedAuthority("ROLE_DOCTOR")
            );
            case "DOCTOR" -> List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"));
            default -> List.of();
        };
    }
    
    @Override
    public String getPassword() {
        return employee.getPassword();
    }
    
    @Override
    public String getUsername() {
        return employee.getEmail();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return employee.isActive();
    }
}