package example.medCashFlow.mappers;

import example.medCashFlow.dto.employee.EmployeeRegisterDTO;
import example.medCashFlow.dto.employee.EmployeeResponseDTO;
import example.medCashFlow.dto.employee.ManagerRegisterDTO;
import example.medCashFlow.model.Employee;
import example.medCashFlow.model.Role;
import example.medCashFlow.services.RoleService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class EmployeeMapper {

    @Autowired
    protected RoleService roleService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clinic", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "role", source = "roleId")
    @Mapping(target = "password", source = "password", qualifiedByName = "mapPassword")
    public abstract Employee toEmployee(EmployeeRegisterDTO data);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clinic", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", source = "password", qualifiedByName = "mapPassword")
    public abstract Employee toManager(ManagerRegisterDTO data);

    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "role", source = "role.name")
    public abstract EmployeeResponseDTO toResponseDTO(Employee employee);

    protected Role mapRole(Long roleId) {
        return roleService.getRoleById(roleId);
    }

    @Named("mapPassword")
    protected String mapPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

}
