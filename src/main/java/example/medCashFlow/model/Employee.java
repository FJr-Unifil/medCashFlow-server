package example.medCashFlow.model;

import example.medCashFlow.dto.employee.EmployeeRegisterDTO;
import example.medCashFlow.dto.employee.ManagerRegisterDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity(name = "employees")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Employee implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName",nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @Column(nullable = false, name = "is_active")
    private boolean isActive = true;

    public Employee(ManagerRegisterDTO data, String encryptedPassword, Role role, Clinic clinic) {
        this.firstName = data.firstName();
        this.lastName = data.lastName();
        this.cpf = data.cpf();
        this.email = data.email();
        this.password = encryptedPassword;
        this.role = role;
        this.clinic = clinic;
    }

    public Employee(EmployeeRegisterDTO data) {
        this.firstName = data.firstName();
        this.lastName = data.lastName();
        this.cpf = data.cpf();
        this.email = data.email();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return switch (this.role.getName()) {
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
            default -> null;
        };

    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
