package example.medCashFlow.dto.involved;

public record InvolvedResponseDTO(
        Long id,
        String name,
        String document,
        String phone,
        String email,
        boolean isActive
) {
}
