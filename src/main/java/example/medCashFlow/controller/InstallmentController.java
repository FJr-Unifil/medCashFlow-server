package example.medCashFlow.controller;

import example.medCashFlow.dto.bill.InstallmentUpdateDTO;
import example.medCashFlow.services.InstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/installments")
@RequiredArgsConstructor
public class InstallmentController {

    private final InstallmentService installmentService;

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateInstallment(@PathVariable Long id, @RequestBody InstallmentUpdateDTO data) {
        installmentService.updateInstallmentById(id, data);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/mark-as-paid/{id}")
    public ResponseEntity<Void> markInstallmentAsPaid(@PathVariable Long id) {
        installmentService.markInstallmentAsPaid(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/mark-as-unpaid/{id}")
    public ResponseEntity<Void> markInstallmentAsUnpaid(@PathVariable Long id) {
        installmentService.markInstallmentAsUnpaid(id);
        return ResponseEntity.noContent().build();
    }

}
