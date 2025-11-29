package example.medCashFlow.dto.accountPlanning;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccountPlanningRegisterDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description,

        @Size(max = 10, message = "Emoji cannot exceed 10 characters")
        String emoji,

        @Size(max = 7, message = "Color must be a valid hex code")
        String color
) {
}
