package example.medCashFlow.dto.employee;

public record EmployeeResponseDTO(String firstName, String lastName, String cpf, String email, String role,
                                  boolean isActive) {
}
