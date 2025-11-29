package example.medCashFlow.infra.security;

import example.medCashFlow.exceptions.ForbiddenException;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.model.Employee;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SecurityService {
    
    public UserPrincipal getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenException("User not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal;
        }
        
        if (principal instanceof Employee employee) {
            return new UserPrincipal(employee);
        }
        
        throw new ForbiddenException("Invalid user principal type");
    }
    
    public Employee getCurrentEmployee() {
        return getCurrentUserPrincipal().getEmployee();
    }
    
    public Clinic getCurrentClinic() {
        return getCurrentEmployee().getClinic();
    }
    
    public UUID getCurrentClinicId() {
        return getCurrentUserPrincipal().getClinicId();
    }
    
    public boolean hasRole(String role) {
        return getCurrentUserPrincipal().hasRole(role);
    }
    
    public void requireRole(String role) {
        if (!hasRole(role)) {
            throw new ForbiddenException("Insufficient permissions");
        }
    }
    
    public void requireAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return;
            }
        }
        throw new ForbiddenException("Insufficient permissions");
    }
    
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.getAuthorities().stream()
                   .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
    
    public void requireAdmin() {
        if (!isAdmin()) {
            throw new ForbiddenException("Admin access required");
        }
    }
}