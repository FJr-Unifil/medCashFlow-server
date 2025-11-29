package example.medCashFlow.dto.auth;

import example.medCashFlow.dto.clinic.ClinicRegisterDTO;
import example.medCashFlow.dto.employee.EmployeeRegisterDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record RegisterDTO(
        @NotNull(message = "Clinic information is required")
        @Valid
        ClinicRegisterDTO clinic,
        
        @NotNull(message = "Manager information is required")
        @Valid
        EmployeeRegisterDTO manager
) {
}
