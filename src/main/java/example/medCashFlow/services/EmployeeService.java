package example.medCashFlow.services;

import example.medCashFlow.exceptions.InvalidEmployeeException;
import example.medCashFlow.model.Employee;
import example.medCashFlow.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {


    private final EmployeeRepository repository;

    public UserDetails getEmployeeByEmail(String email) {
        return repository.findByEmail(email);
    }

    public boolean isEmployeeValid(String cpf, String email) {
        return (isEmployeeValidByCpf(cpf) && isEmployeeValidByEmail(email));
    }

    public boolean isEmployeeValidByCpf(String cpf) {
        if (repository.existsByCpf(cpf)) {
            throw new InvalidEmployeeException("employee.cpf");
        }

        return true;
    }

    public boolean isEmployeeValidByEmail(String email) {
        if (repository.existsByEmail(email)) {
            throw new InvalidEmployeeException("employee.email");
        }

        return true;
    }

    public Long saveEmployee(Employee employee) {
            Employee savedEmployee = repository.save(employee);
            return savedEmployee.getId();
    }
}
