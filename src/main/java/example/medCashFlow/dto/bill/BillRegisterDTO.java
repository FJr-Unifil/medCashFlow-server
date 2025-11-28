package example.medCashFlow.dto.bill;

import java.time.LocalDateTime;
import java.math.BigDecimal;

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
