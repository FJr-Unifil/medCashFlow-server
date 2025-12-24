package example.medCashFlow.dto.accountPlanning;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccountPlanningRegisterDTO(
        @NotBlank(message = "Name is required")
        String name,

        String description,

        @Size(max = 4, message = "Emoji must be at most 4 characters")
        String emoji,

        @NotBlank(message = "Color is required")
        String color
) {
}
