package example.medCashFlow.dto;

import example.medCashFlow.model.Clinic;
import example.medCashFlow.model.Role;

public record RegisterDTO(String name, String cpf, String email, String password, Long roleId , Long clinicId) {
}
