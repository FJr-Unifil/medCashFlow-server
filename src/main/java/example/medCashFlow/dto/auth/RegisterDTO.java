package example.medCashFlow.dto.auth;

import example.medCashFlow.dto.clinic.ClinicRegisterDTO;
import example.medCashFlow.dto.employee.EmployeeRegisterDTO;

public record RegisterDTO(ClinicRegisterDTO clinic, EmployeeRegisterDTO manager) {
}
