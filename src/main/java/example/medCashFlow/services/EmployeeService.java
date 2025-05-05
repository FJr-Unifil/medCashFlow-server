package example.medCashFlow.services;

import example.medCashFlow.dto.employee.EmployeeRegisterDTO;
import example.medCashFlow.dto.employee.EmployeeResponseDTO;
import example.medCashFlow.exceptions.ApiValidationError;
import example.medCashFlow.exceptions.InvalidDataException;
import example.medCashFlow.exceptions.MultipleInvalidDataException;
import example.medCashFlow.exceptions.ResourceNotFoundException;
import example.medCashFlow.mappers.EmployeeMapper;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.model.Employee;
import example.medCashFlow.model.Role;
import example.medCashFlow.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repository;

    private final EmployeeMapper mapper;

    private final RoleService roleService;

    public Employee getEmployeeById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        Employee.class.getSimpleName(),
                        "id",
                        id.toString()
                )
        );
    }

    public EmployeeResponseDTO getEmployeeResponseDTOById(Long id) {
        Employee employee = getEmployeeById(id);
        return mapper.toResponseDTO(employee);
    }

    public Employee getEmployeeByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException(
                        Employee.class.getSimpleName(),
                        "email",
                        email
                )
        );
    }

    public List<EmployeeResponseDTO> getAllEmployeesByClinicId(UUID clinicId) {
        return repository.findAllByClinicIdOrderById(clinicId).stream()
                .map(mapper::toResponseDTO).toList();
    }

    public Map<String, String> getInvalidFields(EmployeeRegisterDTO data) {
        Map<String, String> invalidFields = new HashMap<>();

        if (repository.existsByCpf(data.cpf())) {
            invalidFields.put("cpf", data.cpf());
        }
        if (repository.existsByEmail(data.email())) {
            invalidFields.put("email", data.email());
        }

        return invalidFields;
    }

    public EmployeeResponseDTO createEmployee(EmployeeRegisterDTO data, Clinic clinic) {
        List<ApiValidationError> errors = new ArrayList<>();
        Map<String, String> invalidFields = getInvalidFields(data);

        if (!invalidFields.isEmpty()) {
            invalidFields.forEach((field, val) -> errors.add(new ApiValidationError(
                    Employee.class.getSimpleName(),
                    field,
                    val
            )));
            throw new MultipleInvalidDataException(errors);
        }

        Role role = roleService.getRoleById(data.roleId());

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        Employee employee = mapper.toEmployee(data, clinic, role, encryptedPassword);

        return mapper.toResponseDTO(repository.save(employee));
    }

    public EmployeeResponseDTO updateEmployee(EmployeeRegisterDTO data, Long id) {
        List<ApiValidationError> errors = new ArrayList<>();
        Employee existingEmployee = getEmployeeById(id);

        if (repository.existsByEmailAndIdNot(data.email(), id)) {
            errors.add(new ApiValidationError(
                            Employee.class.getSimpleName(),
                            "email",
                            data.email())
            );
        }

        if (repository.existsByCpfAndIdNot(data.cpf(), id)) {
            errors.add(new ApiValidationError(
                            Employee.class.getSimpleName(),
                            "cpf",
                            data.cpf())
            );
        }

        if (!errors.isEmpty()) {
            throw new MultipleInvalidDataException(errors);
        }

        Role role = roleService.getRoleById(data.roleId());

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        mapper.updateEmployee(existingEmployee,data, role, encryptedPassword);

        return mapper.toResponseDTO(repository.save(existingEmployee));
    }

    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);

        employee.setActive(false);
        repository.save(employee);
    }

    public void activateEmployee(Long id) {
        Employee employee = getEmployeeById(id);

        employee.setActive(true);
        repository.save(employee);
    }
}
