package example.medCashFlow.dto.accountPlannings;

import java.util.UUID;

public record AccountPlanningResponseDTO(
        Long id,
        String name,
        String description,
        String emoji,
        UUID clinicId
) {
}