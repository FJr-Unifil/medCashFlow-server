package example.medCashFlow.dto.bill;

import java.time.LocalDateTime;

public record BillOnlyResponseDTO(
        Long id,
        String name,
        Double pricing,
        String type,
        Long involvedId,
        Long accountPlanningId,
        Long paymentMethodId,
        LocalDateTime dueDate,
        Integer installments
) {
}
