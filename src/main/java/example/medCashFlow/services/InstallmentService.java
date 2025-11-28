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
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class InstallmentService {

    private final InstallmentRepository repository;

    public Installment getInstallmentById(Long id) {
        return repository.findById(id).orElseThrow(InstallmentNotFoundException::new);
    }

    public void saveInstallments(Bill bill) {
        int installmentAmount = bill.getInstallmentsAmount();
        BigDecimal total = bill.getPricing();
        BigDecimal base = total.divide(BigDecimal.valueOf(installmentAmount), 2, RoundingMode.DOWN);
        BigDecimal sumBase = base.multiply(BigDecimal.valueOf(installmentAmount));
        BigDecimal remainder = total.subtract(sumBase);
        int centsToDistribute = remainder.movePointRight(2).intValue();
        LocalDateTime dueDate = bill.getDueDate();

        List<Installment> installmentList = new ArrayList<>();
        for (int i = 0; i < installmentAmount; i++) {
            Installment installment = new Installment();
            installment.setBill(bill);
            installment.setInstallmentNumber(i + 1);
            BigDecimal price = base;
            if (i < centsToDistribute) {
                price = price.add(new BigDecimal("0.01"));
            }
            installment.setPricing(price);
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
