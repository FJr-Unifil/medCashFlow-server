package example.medCashFlow.services;

import example.medCashFlow.dto.bill.InstallmentUpdateDTO;
import example.medCashFlow.exceptions.InstallmentNotFoundException;
import example.medCashFlow.model.Bill;
import example.medCashFlow.model.Installment;
import example.medCashFlow.repository.InstallmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstallmentService {

    private final InstallmentRepository repository;

    public Installment getInstallmentById(Long id) {
        return repository.findById(id).orElseThrow(InstallmentNotFoundException::new);
    }

    public void saveInstallments(Bill bill) {
        int installmentAmount = bill.getInstallmentsAmount();
        double installmentPrice = bill.getPricing() / installmentAmount;
        LocalDateTime dueDate = bill.getDueDate();

        List<Installment> installmentList = new ArrayList<>();
        for (int i = 1; i <= installmentAmount; i++) {
            Installment installment = new Installment();
            installment.setBill(bill);
            installment.setInstallmentNumber(installmentAmount);
            installment.setPricing(installmentPrice);
            installment.setDueDate(dueDate);
            installmentList.add(installment);
            dueDate = dueDate.plusMonths(1);
        }
        repository.saveAll(installmentList);
    }

    public List<Installment> getAllInstallmentsByBillId(Long billId) {
        return repository.findAllByBillId(billId);
    }

    public void updateInstallmentById(Long id, InstallmentUpdateDTO data) {
        Installment installment = getInstallmentById(id);

        installment.setDueDate(data.dueDate());

        repository.save(installment);
    }

    public void markInstallmentAsPaid(Long id) {
        Installment installment = getInstallmentById(id);

        installment.setPaid(true);

        repository.save(installment);
    }

    public void deleteInstallmentByBillId(Long id) {
        List<Installment> installments = getAllInstallmentsByBillId(id);

        repository.deleteAll(installments);
    }


    public void markInstallmentAsUnpaid(Long id) {
        Installment installment = getInstallmentById(id);

        installment.setPaid(false);

        repository.save(installment);
    }

}
