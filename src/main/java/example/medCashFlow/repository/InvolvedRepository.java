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

    boolean existsByPhone(String phone);

    boolean existsByDocumentAndIdNot(String document, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    List<Involved> findAllByClinicIdOrderById(UUID clinicId);

}
