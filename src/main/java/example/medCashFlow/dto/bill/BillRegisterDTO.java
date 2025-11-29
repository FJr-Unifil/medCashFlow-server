package example.medCashFlow.dto.bill;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record BillRegisterDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        String name,
        
        @NotNull(message = "Pricing is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Pricing must be greater than 0")
        Double pricing,
        
        @NotBlank(message = "Type is required")
        @Pattern(regexp = "^(INCOME|EXPENSE)$", message = "Type must be either INCOME or EXPENSE")
        String type,
        
        @NotNull(message = "Involved ID is required")
        @Positive(message = "Involved ID must be positive")
        Long involvedId,
        
        @NotNull(message = "Account Planning ID is required")
        @Positive(message = "Account Planning ID must be positive")
        Long accountPlanningId,
        
        @NotNull(message = "Payment Method ID is required")
        @Positive(message = "Payment Method ID must be positive")
        Long paymentMethodId,
        
        @NotNull(message = "Due date is required")
        @Future(message = "Due date must be in the future")
        LocalDateTime dueDate,
        
        @NotNull(message = "Installments is required")
        @Min(value = 1, message = "Installments must be at least 1")
        @Max(value = 60, message = "Installments must not exceed 60")
        Integer installments
) {
}
