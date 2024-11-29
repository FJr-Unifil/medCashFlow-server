package example.medCashFlow.repository;

import example.medCashFlow.model.Installment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, Long> {

    List<Installment> findAllByBillId(Long billId);

    @Query("SELECT COALESCE(MAX(id), 0) + 1 FROM installments")
    Long getNextId();
}

