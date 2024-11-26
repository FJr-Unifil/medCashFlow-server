package example.medCashFlow.dto.employee;

public record EmployeeResponseDTO(Long id, String firstName, String lastName, String cpf, String email, String role,
                                  boolean isActive) {
}
