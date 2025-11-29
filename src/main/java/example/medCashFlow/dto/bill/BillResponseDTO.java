package example.medCashFlow.dto.bill;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillResponseDTO(
        Long billId,
        Long installmentId,
        boolean isPaid,
        String name,
        BigDecimal pricing,
        String type,
        Long employeeId,
        Long involvedId,
        Long accountPlanningId,
        String paymentMethod,
        LocalDateTime dueDate
) {
}
