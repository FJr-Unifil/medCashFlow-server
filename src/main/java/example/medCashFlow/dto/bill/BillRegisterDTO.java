package example.medCashFlow.dto.bill;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillRegisterDTO(
        String name,
        BigDecimal pricing,
        String type,
        Long involvedId,
        Long accountPlanningId,
        Long paymentMethodId,
        LocalDateTime dueDate,
        Integer installments
) {
}
