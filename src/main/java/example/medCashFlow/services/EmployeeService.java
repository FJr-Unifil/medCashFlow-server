package example.medCashFlow.services;

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

    public boolean isEmployeeAlreadyOnDatabase(String cpf, String email) {
        return (repository.findByEmail(email) == null && repository.findByCpf(cpf) == null);
    }

    public Long saveEmployeeOnDatabase(Employee employee) {
        Employee savedEmployee = repository.save(employee);
        return savedEmployee.getId();
    }
}
