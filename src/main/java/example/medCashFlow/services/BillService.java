package example.medCashFlow.services;

import example.medCashFlow.dto.bill.BillDependencies;
import example.medCashFlow.dto.bill.BillOnlyResponseDTO;
import example.medCashFlow.dto.bill.BillRegisterDTO;
import example.medCashFlow.dto.bill.BillResponseDTO;
import example.medCashFlow.exceptions.BillNotFoundException;
import example.medCashFlow.exceptions.ForbiddenException;
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

    private BillDependencies fetchBillDependencies(BillRegisterDTO data) {
        return new BillDependencies(
                involvedService.getInvolvedById(data.involvedId()),
                accountPlanningService.getAccountPlanningById(data.accountPlanningId()),
                paymentMethodService.getPaymentMethodById(data.paymentMethodId())
        );
    }

    public Bill getBillById(Long id) {
        return repository.findById(id).orElseThrow(BillNotFoundException::new);
    }

    public Bill getBillByIdAndClinic(Long id, UUID clinicId) {
        Bill bill = getBillById(id);
        if (!bill.getClinic().getId().equals(clinicId)) {
            throw new ForbiddenException();
        }
        return bill;
    }

    public List<BillResponseDTO> getAllBillsByClinicId(UUID clinicId) {
        return repository.findAllBillByClinicId(clinicId);
    }

    public void createBill(BillRegisterDTO data, Employee employee) {
        BillDependencies dependencies = fetchBillDependencies(data);
        Bill bill = mapper.toBill(data, employee, dependencies.involved(), dependencies.accountPlanning(), dependencies.paymentMethod());
        Bill savedBill = repository.save(bill);
        installmentService.saveInstallments(savedBill);
    }

    public void updateBill(BillRegisterDTO data, Long id, UUID clinicId) {
        Bill existingBill = getBillByIdAndClinic(id, clinicId);

        BillDependencies dependencies = fetchBillDependencies(data);
        mapper.updateBill(existingBill, data, dependencies.involved(), dependencies.accountPlanning(), dependencies.paymentMethod());

        installmentService.deleteInstallmentByBillId(id);
        Bill savedBill = repository.save(existingBill);
        installmentService.saveInstallments(savedBill);
    }

    public void deleteBill(Long id, UUID clinicId) {
        Bill bill = getBillByIdAndClinic(id, clinicId);
        installmentService.deleteInstallmentByBillId(id);
        repository.delete(bill);
    }

    public BillOnlyResponseDTO getBillOnlyResponseDTO(Long id, UUID clinicId) {
        Bill bill = getBillByIdAndClinic(id, clinicId);
        return mapper.toBillOnlyResponseDTO(bill);
    }

}