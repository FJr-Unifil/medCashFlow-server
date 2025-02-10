package example.medCashFlow.services;

import example.medCashFlow.dto.employee.EmployeeRegisterDTO;
import example.medCashFlow.dto.employee.EmployeeResponseDTO;
import example.medCashFlow.dto.employee.ManagerRegisterDTO;
import example.medCashFlow.exceptions.EmployeeNotFoundException;
import example.medCashFlow.exceptions.InvalidEmployeeException;
import example.medCashFlow.mappers.EmployeeMapper;
import example.medCashFlow.model.Clinic;
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

    private final EmployeeMapper mapper;

    public Employee toEmployee(EmployeeRegisterDTO data, Clinic clinic) {
        return mapper.toEmployee(data, clinic);
    }

    public Employee toManager(ManagerRegisterDTO data, Clinic clinic, Long roleId) {
        return mapper.toManager(data, clinic, roleId);
    }

    public EmployeeResponseDTO toResponseDTO(Employee employee) {
        return mapper.toResponseDTO(employee);
    }

    public Employee getEmployeeById(Long Id) {
        return repository.findById(Id).orElseThrow(EmployeeNotFoundException::new);
    }

    public Employee getEmployeeByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(EmployeeNotFoundException::new);
    }

    public List<EmployeeResponseDTO> getAllEmployeesByClinicId(UUID clinicId) {
        return repository.findAllByClinicIdOrderById(clinicId).stream()
                .map(this::toResponseDTO).toList();
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

        return toResponseDTO(repository.save(employee));
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

        existingEmployee.setFirstName(employee.getFirstName());
        existingEmployee.setLastName(employee.getLastName());
        existingEmployee.setCpf(employee.getCpf());
        existingEmployee.setEmail(employee.getEmail());

        return toResponseDTO(repository.save(existingEmployee));
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
