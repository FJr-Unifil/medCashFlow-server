package example.medCashFlow.services;

import example.medCashFlow.dto.bill.BillRegisterDTO;
import example.medCashFlow.dto.bill.BillResponseDTO;
import example.medCashFlow.exceptions.BillNotFoundException;
import example.medCashFlow.model.Bill;
import example.medCashFlow.model.BillType;
import example.medCashFlow.model.Employee;
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

    public Bill getBillById(Long id) {
        return repository.findById(id).orElseThrow(BillNotFoundException::new);
    }

    public List<BillResponseDTO> getAllBillsByClinicId(UUID clinicId) {
        return repository.findByClinicId(clinicId).stream()
                .map(this::toDTO)
                .toList();
    }

    public BillResponseDTO saveBill(Bill bill) {
        repository.save(bill);
        return toDTO(bill);
    }

    public BillResponseDTO updateBill(Long id, BillRegisterDTO data, Employee loggedEmployee) {
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
        existingBill.setInstallments(data.installments());

        repository.save(existingBill);
        return toDTO(existingBill);
    }

    public void deleteBill(Long id) {
        Bill bill = getBillById(id);
        repository.delete(bill);
    }

    public void markAsPaid(Long id) {
        Bill bill = getBillById(id);
        bill.setPaid(true);
        repository.save(bill);
    }

    public void markAsUnpaid(Long id) {
        Bill bill = getBillById(id);
        bill.setPaid(false);
        repository.save(bill);
    }

    private BillResponseDTO toDTO(Bill bill) {
        return new BillResponseDTO(
                bill.getId(),
                bill.getName(),
                bill.getPricing(),
                bill.getType().toString(),
                bill.getEmployee().getId(),
                bill.getInvolved().getId(),
                bill.getAccountPlanning() != null ? bill.getAccountPlanning().getId() : null,
                bill.getPaymentMethod().getId(),
                bill.getCreatedAt(),
                bill.getDueDate(),
                bill.getInstallments(),
                bill.isPaid()
        );
    }
}