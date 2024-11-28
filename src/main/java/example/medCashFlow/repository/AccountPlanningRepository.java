package example.medCashFlow.repository;

import example.medCashFlow.model.AccountPlanning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountPlanningRepository extends JpaRepository<AccountPlanning, Long> {

    List<AccountPlanning> findByClinicIdOrderById(UUID clinicId);
}
