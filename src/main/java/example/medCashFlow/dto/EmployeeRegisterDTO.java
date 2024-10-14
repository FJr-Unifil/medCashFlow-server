package example.medCashFlow.dto;

public record EmployeeRegisterDTO(String name, String cpf, String email, String password, Long roleId , Long clinicId) {
}
