package example.medCashFlow.repository;

import example.medCashFlow.dto.bill.BillResponseDTO;
import example.medCashFlow.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    @Query("SELECT new example.medCashFlow.dto.bill.BillResponseDTO(" +
            "b.id, " +
            "i.id, " +
            "i.isPaid, " +
            "b.name, " +
            "i.pricing, " +
            "CAST(b.type AS string), " +
            "b.employee.id, " +
            "b.involved.id, " +
            "b.accountPlanning.id, " +
            "b.paymentMethod.id, " +
            "i.dueDate " +
            ") " +
            "FROM bills b " +
            "JOIN installments i ON i.bill = b " +
            "WHERE b.clinic.id = :clinicId " +
            "ORDER BY i.dueDate")
    List<BillResponseDTO> findAllBillByClinicId(UUID clinicId);
}
