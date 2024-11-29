package example.medCashFlow.services;

import example.medCashFlow.dto.bill.BillRegisterDTO;
import example.medCashFlow.dto.bill.BillResponseDTO;
import example.medCashFlow.exceptions.BillNotFoundException;
import example.medCashFlow.model.Bill;
import example.medCashFlow.model.BillType;
import example.medCashFlow.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository repository;
    private final InvolvedService involvedService;
    private final AccountPlanningService accountPlanningService;
    private final PaymentMethodService paymentMethodService;
    private final InstallmentService installmentService;

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

    public void updateBill(Long id, BillRegisterDTO data) {
        Bill existingBill = getBillById(id);

        existingBill.setName(data.name());
        existingBill.setPricing(data.pricing());
        existingBill.setType(BillType.valueOf(data.type()));
        existingBill.setInvolved(involvedService.getInvolvedById(data.involvedId()));

        if (data.accountPlanningId() != null) {
            existingBill.setAccountPlanning(accountPlanningService.getAccountPlanningById(data.accountPlanningId()));
        } else {
            existingBill.setAccountPlanning(null);
        }

        existingBill.setPaymentMethod(paymentMethodService.getPaymentMethodById(data.paymentMethodId()));
        existingBill.setDueDate(data.dueDate());
        existingBill.setInstallmentsAmount(data.installments());

        installmentService.deleteInstallmentByBillId(existingBill.getId());
        saveBill(existingBill);
    }

    public void deleteBill(Long id) {
        Bill bill = getBillById(id);
        installmentService.deleteInstallmentByBillId(id);
        repository.delete(bill);
    }

}