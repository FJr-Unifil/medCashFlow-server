package example.medCashFlow.dto.bill;

import java.time.LocalDateTime;

public record InstallmentUpdateDTO(LocalDateTime dueDate) {
}
