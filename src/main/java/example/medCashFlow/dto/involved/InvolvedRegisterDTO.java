package example.medCashFlow.dto.involved;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InvolvedRegisterDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        @Size(max = 20, message = "Document cannot exceed 20 characters")
        String document,

        @Size(max = 15, message = "Phone cannot exceed 15 characters")
        String phone,

        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        String email
) {
}
