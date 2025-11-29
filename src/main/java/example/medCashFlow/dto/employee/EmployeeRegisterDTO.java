package example.medCashFlow.dto.employee;

import jakarta.validation.constraints.*;

public record EmployeeRegisterDTO(
        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,
        
        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,
        
        @NotBlank(message = "CPF is required")
        @Pattern(regexp = "\\d{11}", message = "CPF must contain exactly 11 digits")
        String cpf,
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,
        
        @NotNull(message = "Role ID is required")
        @Positive(message = "Role ID must be positive")
        Long roleId
) {
}
