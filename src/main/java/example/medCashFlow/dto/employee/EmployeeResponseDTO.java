package example.medCashFlow.dto.employee;

public record EmployeeResponseDTO(String first_name, String last_name, String cpf, String email, String role,
                                  boolean isActive) {
}
