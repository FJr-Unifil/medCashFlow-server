package example.medCashFlow.services;

import example.medCashFlow.dto.bill.BillOnlyResponseDTO;
import example.medCashFlow.dto.bill.BillRegisterDTO;
import example.medCashFlow.dto.bill.BillResponseDTO;
import example.medCashFlow.exceptions.BillNotFoundException;
import example.medCashFlow.model.Bill;
import example.medCashFlow.model.Employee;
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

    public Bill toBill(BillRegisterDTO data, Employee employee) {
        return billMapper.toBill(data, employee);
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

    public void updateBill(Bill bill, Long id) {
        Bill existingBill = getBillById(id);

        existingBill.setName(bill.getName());
        existingBill.setPricing(bill.getPricing());
        existingBill.setType(bill.getType());
        existingBill.setInvolved(bill.getInvolved());
        existingBill.setAccountPlanning(bill.getAccountPlanning());
        existingBill.setPaymentMethod(bill.getPaymentMethod());
        existingBill.setDueDate(bill.getDueDate());
        existingBill.setInstallmentsAmount(bill.getInstallmentsAmount());

        installmentService.deleteInstallmentByBillId(id);
        saveBill(existingBill);
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