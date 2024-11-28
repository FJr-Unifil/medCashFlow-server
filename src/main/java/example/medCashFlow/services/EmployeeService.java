package example.medCashFlow.services;

import example.medCashFlow.dto.employee.EmployeeResponseDTO;
import example.medCashFlow.exceptions.EmployeeNotFoundException;
import example.medCashFlow.exceptions.InvalidEmployeeException;
import example.medCashFlow.model.Employee;
import example.medCashFlow.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {


    private final EmployeeRepository repository;

    private final RoleService roleService;

    public Employee getEmployeeById(Long Id) {
        return repository.findById(Id).orElseThrow(EmployeeNotFoundException::new);
    }

    public Employee getEmployeeByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(EmployeeNotFoundException::new);
    }

    public List<EmployeeResponseDTO> getAllEmployeesByClinicId(UUID clinicId) {
        return repository.findAllByClinicId(clinicId).stream()
                .map(employee -> new EmployeeResponseDTO(
                        employee.getId(),
                        employee.getFirst_name(),
                        employee.getLast_name(),
                        employee.getCpf(),
                        employee.getEmail(),
                        employee.getRole().getName(),
                        employee.isActive()
                )).toList();
    }

    public boolean isEmployeeValid(String cpf, String email) {
        return (isEmployeeValidByCpf(cpf) && isEmployeeValidByEmail(email));
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

    public EmployeeResponseDTO saveEmployee(Employee employee) {
        if (!isEmployeeValid(employee.getCpf(), employee.getEmail())) {
            throw new InvalidEmployeeException();
        }

        repository.save(employee);

        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getFirst_name(),
                employee.getLast_name(),
                employee.getCpf(),
                employee.getEmail(),
                employee.getRole().getName(),
                employee.isActive()
        );
    }

    public EmployeeResponseDTO updateEmployee(Employee employee, Long id) {
        Employee existingEmployee = getEmployeeById(id);

        if (!employee.getEmail().equals(existingEmployee.getEmail())
                && repository.existsByEmail(employee.getEmail())) {
            throw new InvalidEmployeeException("manager.email");
        }

        if (!employee.getCpf().equals(existingEmployee.getCpf())
                && repository.existsByCpf(employee.getCpf())) {
            throw new InvalidEmployeeException("manager.cpf");
        }

        existingEmployee.setFirst_name(employee.getFirst_name());
        existingEmployee.setLast_name(employee.getLast_name());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setCpf(employee.getCpf());
        existingEmployee.setPassword(employee.getPassword());
        existingEmployee.setRole(employee.getRole());

        repository.save(existingEmployee);

        return new EmployeeResponseDTO(
                existingEmployee.getId(),
                existingEmployee.getFirst_name(),
                existingEmployee.getLast_name(),
                existingEmployee.getCpf(),
                existingEmployee.getEmail(),
                existingEmployee.getRole().getName(),
                existingEmployee.isActive()
        );
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
