package example.medCashFlow.dto.clinic;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClinicResponseDTO(UUID id, String name, String cnpj, String phone, LocalDateTime createdAt,
                                boolean isActive) {
}
