package example.medCashFlow.repository;

import example.medCashFlow.model.Involved;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvolvedRepository extends JpaRepository<Involved, Long> {

    boolean existsByDocument(String document);

    boolean existsByEmail(String email);

    Involved findByEmail(String email);

    List<Involved> findAllByClinicId(UUID clinicId);

}
