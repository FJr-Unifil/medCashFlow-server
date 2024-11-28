package example.medCashFlow.dto.bill;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillResponseDTO(
        Long id,
        String name,
        BigDecimal pricing,
        String type,
        Long employeeId,
        Long involvedId,
        Long accountPlanningId,
        Long paymentMethodId,
        LocalDateTime createdAt,
        LocalDateTime dueDate,
        Integer installments,
        boolean isPaid
) {
}
