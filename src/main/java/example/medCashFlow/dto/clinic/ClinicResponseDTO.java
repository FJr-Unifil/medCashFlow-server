package example.medCashFlow.dto.clinic;

import java.time.LocalDateTime;

public record ClinicResponseDTO(String name, String cnpj, String phone, LocalDateTime createdAt, boolean isActive) {
}
