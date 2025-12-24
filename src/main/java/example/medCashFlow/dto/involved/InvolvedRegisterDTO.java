package example.medCashFlow.dto.involved;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InvolvedRegisterDTO(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Document is required")
        String document,

        @NotBlank(message = "Phone is required")
        String phone,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {
}
