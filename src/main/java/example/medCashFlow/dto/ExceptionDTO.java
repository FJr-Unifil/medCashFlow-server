package example.medCashFlow.dto;

import java.time.LocalDateTime;

public record ExceptionDTO(int Status, String title, String description, LocalDateTime timestamp) {
}
