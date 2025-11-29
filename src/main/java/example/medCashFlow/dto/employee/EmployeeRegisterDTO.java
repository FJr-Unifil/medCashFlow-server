package example.medCashFlow.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmployeeRegisterDTO(
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name cannot exceed 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name cannot exceed 50 characters")
        String lastName,

        @NotBlank(message = "CPF is required")
        @Pattern(regexp = "\\d{11}", message = "CPF must contain exactly 11 digits")
        String cpf,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        String password,

        @NotNull(message = "Role is required")
        Long roleId
) {
}
