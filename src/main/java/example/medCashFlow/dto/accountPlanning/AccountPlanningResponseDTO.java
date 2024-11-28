package example.medCashFlow.dto.accountPlanning;

import java.util.UUID;

public record AccountPlanningResponseDTO(
        Long id,
        String name,
        String description,
        String emoji,
        String color,
        UUID clinicId
) {
}