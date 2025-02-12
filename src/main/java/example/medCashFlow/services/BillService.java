package example.medCashFlow.services;

import example.medCashFlow.dto.bill.BillOnlyResponseDTO;
import example.medCashFlow.dto.bill.BillRegisterDTO;
import example.medCashFlow.dto.bill.BillResponseDTO;
import example.medCashFlow.exceptions.BillNotFoundException;
import example.medCashFlow.model.*;
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
    private final InvolvedService involvedService;
    private final AccountPlanningService accountPlanningService;
    private final PaymentMethodService paymentMethodService;

    private final BillRepository repository;

    private final BillMapper mapper;

    public Bill toBill(BillRegisterDTO data, Employee employee) {
        Involved involved = involvedService.getInvolvedById(data.involvedId());
        AccountPlanning accountPlanning = accountPlanningService.getAccountPlanningById(data.accountPlanningId());
        PaymentMethod paymentMethod= paymentMethodService.getPaymentMethodById(data.paymentMethodId());
        return mapper.toBill(data, employee, involved, accountPlanning, paymentMethod);
    }

    public Bill getBillById(Long id) {
        return repository.findById(id).orElseThrow(BillNotFoundException::new);
    }

    public List<BillResponseDTO> getAllBillsByClinicId(UUID clinicId) {
        return repository.findAllBillByClinicId(clinicId);
    }

    public void createBill(BillRegisterDTO data, Employee employee) {
        Bill bill = toBill(data, employee);
        Bill savedBill = repository.save(bill);
        installmentService.saveInstallments(savedBill);
    }

    public void updateBill(BillRegisterDTO data, Employee employee, Long id) {
        if (!repository.existsById(id)) {
            throw new BillNotFoundException();
        }

        Bill updatedBill = toBill(data, employee);
        updatedBill.setId(id);

        installmentService.deleteInstallmentByBillId(id);
        Bill savedBill = repository.save(updatedBill);
        installmentService.saveInstallments(savedBill);
    }

    public void deleteBill(Long id) {
        Bill bill = getBillById(id);
        installmentService.deleteInstallmentByBillId(id);
        repository.delete(bill);
    }

    public BillOnlyResponseDTO getBillOnlyResponseDTO(Long id) {
        Bill bill = getBillById(id);
        return mapper.toBillOnlyResponseDTO(bill);
    }

}