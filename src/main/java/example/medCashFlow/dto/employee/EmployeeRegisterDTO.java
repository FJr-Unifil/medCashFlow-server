package example.medCashFlow.dto.employee;

public record EmployeeRegisterDTO(String first_name, String last_name, String cpf, String email, String password, Long roleId , Long clinicId) {
}
