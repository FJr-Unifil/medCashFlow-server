package example.medCashFlow.dto.bill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record BillRegisterDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 50, message = "Name must be at most 50 characters")
        String name,

        @NotNull(message = "Pricing is required")
        @Positive(message = "Pricing must be positive")
        Double pricing,

        @NotBlank(message = "Type is required")
        String type,

        @NotNull(message = "Involved ID is required")
        Long involvedId,

        Long accountPlanningId,

        @NotNull(message = "Payment method ID is required")
        Long paymentMethodId,

        @NotNull(message = "Due date is required")
        LocalDateTime dueDate,

        @NotNull(message = "Installments is required")
        @Positive(message = "Installments must be positive")
        Integer installments
) {
}
