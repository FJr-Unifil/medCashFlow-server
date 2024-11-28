package example.medCashFlow.controller;

import example.medCashFlow.dto.bill.BillRegisterDTO;
import example.medCashFlow.dto.bill.BillResponseDTO;
import example.medCashFlow.exceptions.ForbiddenException;
import example.medCashFlow.model.Bill;
import example.medCashFlow.model.BillType;
import example.medCashFlow.model.Employee;
import example.medCashFlow.services.AccountPlanningService;
import example.medCashFlow.services.BillService;
import example.medCashFlow.services.InvolvedService;
import example.medCashFlow.services.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    private final InvolvedService involvedService;

    private final AccountPlanningService accountPlanningService;

    private final PaymentMethodService paymentMethodService;

    @GetMapping("/list")
    public ResponseEntity<List<BillResponseDTO>> listAllBills(
            @AuthenticationPrincipal UserDetails loggedUser) {
        if (!(loggedUser instanceof Employee employee)) {
            throw new ForbiddenException();
        }

        List<BillResponseDTO> bills = billService.getAllBillsByClinicId(employee.getClinic().getId());
        return ResponseEntity.ok(bills);
    }

    @PostMapping("/create")
    public ResponseEntity<BillResponseDTO> createBill(
            @AuthenticationPrincipal UserDetails loggedUser,
            @RequestBody BillRegisterDTO data) {
        if (!(loggedUser instanceof Employee employee)) {
            throw new ForbiddenException();
        }

        Bill bill = new Bill();
        bill.setName(data.name());
        bill.setPricing(data.pricing());
        bill.setType(BillType.valueOf(data.type()));
        bill.setEmployee(employee);


        bill.setClinic(employee.getClinic());

        bill.setInvolved(involvedService.getInvolvedById(data.involvedId()));

        if (data.accountPlanningId() != null) {
            bill.setAccountPlanning(accountPlanningService.getAccountPlanningById(data.accountPlanningId()));
        }

        bill.setPaymentMethod(paymentMethodService.getPaymentMethodById(data.paymentMethodId()));
        bill.setCreatedAt(LocalDateTime.now());
        bill.setDueDate(data.dueDate());
        bill.setInstallments(data.installments());
        bill.setPaid(false);

        BillResponseDTO newBill = billService.saveBill(bill);
        return ResponseEntity.ok(newBill);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BillResponseDTO> updateBill(
            @AuthenticationPrincipal UserDetails loggedUser,
            @PathVariable Long id,
            @RequestBody BillRegisterDTO data) {
        if (!(loggedUser instanceof Employee employee)) {
            throw new ForbiddenException();
        }

        BillResponseDTO bill = billService.updateBill(id, data, employee);
        return ResponseEntity.ok(bill);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBill(
            @AuthenticationPrincipal UserDetails loggedUser,
            @PathVariable Long id) {
        if (!(loggedUser instanceof Employee)) {
            throw new ForbiddenException();
        }

        billService.deleteBill(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/mark-as-paid/{id}")
    public ResponseEntity<Void> markBillAsPaid(
            @AuthenticationPrincipal UserDetails loggedUser,
            @PathVariable Long id) {
        if (!(loggedUser instanceof Employee)) {
            throw new ForbiddenException();
        }

        billService.markAsPaid(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/mark-as-unpaid/{id}")
    public ResponseEntity<Void> markBillAsUnpaid(
            @AuthenticationPrincipal UserDetails loggedUser,
            @PathVariable Long id) {
        if (!(loggedUser instanceof Employee)) {
            throw new ForbiddenException();
        }

        billService.markAsUnpaid(id);
        return ResponseEntity.noContent().build();
    }
}
