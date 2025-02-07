package example.medCashFlow.services;

import example.medCashFlow.dto.bill.BillOnlyResponseDTO;
import example.medCashFlow.dto.bill.BillRegisterDTO;
import example.medCashFlow.dto.bill.BillResponseDTO;
import example.medCashFlow.exceptions.BillNotFoundException;
import example.medCashFlow.model.Bill;
import example.medCashFlow.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import example.medCashFlow.mappers.BillMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillService {

    private final InstallmentService installmentService;

    private final BillRepository repository;
    private final BillMapper billMapper;

    public Bill toBill(BillRegisterDTO data) {
        return billMapper.toBill(data);
    }

    public BillOnlyResponseDTO toOnlyResponseDTO(Bill bill) {
        return billMapper.toBillOnlyResponseDTO(bill);
    }

    public Bill getBillById(Long id) {
        return repository.findById(id).orElseThrow(BillNotFoundException::new);
    }

    public List<BillResponseDTO> getAllBillsByClinicId(UUID clinicId) {
        return repository.findAllBillByClinicId(clinicId);
    }

    public void saveBill(Bill bill) {
        Bill savedBill = repository.save(bill);
        installmentService.saveInstallments(savedBill);
    }

    public void updateBill(Bill bill) {
        if (!repository.existsById(bill.getId())) {
            throw new BillNotFoundException();
        }

        installmentService.deleteInstallmentByBillId(bill.getId());
        saveBill(bill);
    }

    public void deleteBill(Long id) {
        Bill bill = getBillById(id);
        installmentService.deleteInstallmentByBillId(id);
        repository.delete(bill);
    }

    public BillOnlyResponseDTO getBillOnlyResponseDTO(Long id) {
        Bill bill = getBillById(id);
        return toOnlyResponseDTO(bill);
    }

}