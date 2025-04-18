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

    @Query(name = "Bills.getAllBillDTOByClinicId")
    List<BillResponseDTO> findAllBillByClinicId(UUID clinicId);
}
