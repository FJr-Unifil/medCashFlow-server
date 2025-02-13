package example.medCashFlow.mappers;

import example.medCashFlow.dto.employee.EmployeeRegisterDTO;
import example.medCashFlow.dto.employee.EmployeeResponseDTO;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.model.Employee;
import example.medCashFlow.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "role", source = "role")
    @Mapping(target = "password", source = "encryptedPassword")
    Employee toEmployee(EmployeeRegisterDTO data, Clinic clinic, Role role, String encryptedPassword);

    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "role", source = "role.name")
    EmployeeResponseDTO toResponseDTO(Employee employee);

}
