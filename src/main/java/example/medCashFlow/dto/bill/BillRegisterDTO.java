package example.medCashFlow.dto.bill;

import java.time.LocalDateTime;

public record BillRegisterDTO(
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
