package example.medCashFlow.dto.employee;

public record EmployeeRegisterDTO(String firstName, String lastName, String cpf, String email, String password,
                                  Long roleId) {
}
