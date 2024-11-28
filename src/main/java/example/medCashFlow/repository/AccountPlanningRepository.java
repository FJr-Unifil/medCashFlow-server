package example.medCashFlow.repository;

import example.medCashFlow.model.AccountPlanning;
import example.medCashFlow.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountPlanningRepository extends JpaRepository<AccountPlanning, Long> {

    List<AccountPlanning> findByClinicId(UUID clinicId);

    boolean existsByNameAndClinic(String name, Clinic clinic);
}
