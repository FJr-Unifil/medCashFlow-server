package example.medCashFlow.repository;

import example.medCashFlow.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, UUID> {

    boolean existsByIdAndIsActiveTrue(UUID id);

    boolean existsByCnpjAndIsActiveTrue(String cnpj);

    boolean existsByPhoneAndIsActiveTrue(String phone);

    boolean existsByNameAndIsActiveTrue(String name);
}
