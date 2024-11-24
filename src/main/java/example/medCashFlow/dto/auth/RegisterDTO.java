package example.medCashFlow.dto.auth;

import example.medCashFlow.dto.clinic.ClinicRegisterDTO;
import example.medCashFlow.dto.employee.ManagerRegisterDTO;

public record RegisterDTO(ClinicRegisterDTO clinic, ManagerRegisterDTO manager) {
}
