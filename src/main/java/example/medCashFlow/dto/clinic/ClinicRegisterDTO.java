package example.medCashFlow.dto.clinic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClinicRegisterDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        @NotBlank(message = "CNPJ is required")
        @Pattern(regexp = "\\d{14}", message = "CNPJ must contain exactly 14 digits")
        String cnpj,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "\\d{10,11}", message = "Phone must contain 10 or 11 digits")
        String phone
) {
}
