package example.medCashFlow.dto.bill;

import java.time.LocalDateTime;
import java.math.BigDecimal;

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
