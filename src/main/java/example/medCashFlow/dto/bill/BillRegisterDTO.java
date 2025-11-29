package example.medCashFlow.dto.bill;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillRegisterDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 50, message = "Name cannot exceed 50 characters")
        String name,

        @NotNull(message = "Pricing is required")
        @DecimalMin(value = "0.01", message = "Pricing must be greater than zero")
        @Digits(integer = 8, fraction = 2, message = "Pricing must have at most 8 integer digits and 2 decimal places")
        BigDecimal pricing,

        @NotBlank(message = "Type is required")
        @Pattern(regexp = "INCOME|EXPENSE", message = "Type must be INCOME or EXPENSE")
        String type,

        @NotNull(message = "Involved party is required")
        Long involvedId,

        Long accountPlanningId,

        @NotNull(message = "Payment method is required")
        Long paymentMethodId,

        @NotNull(message = "Due date is required")
        @Future(message = "Due date must be in the future")
        LocalDateTime dueDate,

        @NotNull(message = "Installments is required")
        @Min(value = 1, message = "Must have at least 1 installment")
        @Max(value = 60, message = "Cannot exceed 60 installments")
        Integer installments
) {
}
