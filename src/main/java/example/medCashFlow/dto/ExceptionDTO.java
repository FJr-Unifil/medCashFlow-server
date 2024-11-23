package example.medCashFlow.dto;

import java.time.LocalDateTime;

public record ExceptionDTO(int status, String title, String description, LocalDateTime timestamp) {
}
