package example.medCashFlow.dto.clinic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClinicRegisterDTO(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "CNPJ is required")
        @Size(min = 14, max = 18, message = "CNPJ must be between 14 and 18 characters")
        String cnpj,

        @NotBlank(message = "Phone is required")
        String phone
) {
}
