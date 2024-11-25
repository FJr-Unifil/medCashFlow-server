package example.medCashFlow.repository;

import example.medCashFlow.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findByEmail(String email);

    Employee findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

}
