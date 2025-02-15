package example.medCashFlow.repository;

import example.medCashFlow.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, UUID> {

    boolean existsByCnpj(String cnpj);

    boolean existsByPhone(String phone);

    boolean existsByName(String name);

    List<Clinic> findAllByOrderByCreatedAtAsc();

    Optional<Clinic> findByCnpj(String cnpj);

}
