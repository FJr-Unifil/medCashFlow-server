package example.medCashFlow.services;

import example.medCashFlow.dto.employee.EmployeeRegisterDTO;
import example.medCashFlow.dto.employee.EmployeeResponseDTO;
import example.medCashFlow.exceptions.EmployeeNotFoundException;
import example.medCashFlow.exceptions.InvalidEmployeeException;
import example.medCashFlow.mappers.EmployeeMapper;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.model.Employee;
import example.medCashFlow.model.Role;
import example.medCashFlow.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repository;

    private final EmployeeMapper mapper;

    private final RoleService roleService;

    public Employee getEmployeeById(Long Id) {
        return repository.findById(Id).orElseThrow(EmployeeNotFoundException::new);
    }

    public EmployeeResponseDTO getEmployeeResponseDTOById(Long Id) {
        Employee employee = getEmployeeById(Id);
        return mapper.toResponseDTO(employee);
    }

    public Employee getEmployeeByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(EmployeeNotFoundException::new);
    }

    public List<EmployeeResponseDTO> getAllEmployeesByClinicId(UUID clinicId) {
        return repository.findAllByClinicIdOrderById(clinicId).stream()
                .map(mapper::toResponseDTO).toList();
    }

    public boolean isEmployeeValid(String cpf, String email) {
        return (!isEmployeeValidByCpf(cpf) || !isEmployeeValidByEmail(email));
    }

    public boolean isEmployeeValidByCpf(String cpf) {
        if (repository.existsByCpf(cpf)) {
            throw new InvalidEmployeeException("manager.cpf");
        }

        return true;
    }

    public boolean isEmployeeValidByEmail(String email) {
        if (repository.existsByEmail(email)) {
            throw new InvalidEmployeeException("manager.email");
        }

        return true;
    }

    public EmployeeResponseDTO createEmployee(EmployeeRegisterDTO data, Clinic clinic) {
        if (isEmployeeValid(data.cpf(), data.email())) {
            throw new InvalidEmployeeException();
        }

        Role role = roleService.getRoleById(data.roleId());

        Employee employee = mapper.toEmployee(data, clinic, role);

        return mapper.toResponseDTO(repository.save(employee));
    }

    public EmployeeResponseDTO updateEmployee(EmployeeRegisterDTO data, Long id) {
        Employee existingEmployee = getEmployeeById(id);

        if (!data.email().equals(existingEmployee.getEmail())
                && repository.existsByEmail(data.email())) {
            throw new InvalidEmployeeException("manager.email");
        }

        if (!data.cpf().equals(existingEmployee.getCpf())
                && repository.existsByCpf(data.cpf())) {
            throw new InvalidEmployeeException("manager.cpf");
        }

        Role role = roleService.getRoleById(data.roleId());
        Employee updatedEmployee = mapper.toEmployee(data, existingEmployee.getClinic(), role);
        updatedEmployee.setId(existingEmployee.getId());

        return mapper.toResponseDTO(repository.save(updatedEmployee));
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
