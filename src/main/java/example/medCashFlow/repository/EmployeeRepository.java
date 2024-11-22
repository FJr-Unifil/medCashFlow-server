package example.medCashFlow.repository;

import example.medCashFlow.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    UserDetails findByEmail(String email);

    UserDetails findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);
}
