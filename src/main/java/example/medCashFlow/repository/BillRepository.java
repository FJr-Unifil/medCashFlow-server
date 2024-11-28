package example.medCashFlow.repository;

import example.medCashFlow.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByClinicId(@Param("clinicId") UUID clinicId);

    List<Bill> findByClinicIdAndDueDateBetween(
            @Param("clinicId") UUID clinicId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    List<Bill> findByClinicIdAndIsPaidFalse(UUID clinicId);

    List<Bill> findByClinicIdAndIsPaidTrue(UUID clinicId);

    List<Bill> findByClinicIdAndType(UUID clinicId, String type);

    List<Bill> findByClinicIdAndInvolvedId(UUID clinicId, Long involvedId);

    List<Bill> findByClinicIdAndAccountPlanningId(UUID clinicId, Long accountPlanningId);

    List<Bill> findByClinicIdAndPaymentMethodId(UUID clinicId, Long paymentMethodId);
}
