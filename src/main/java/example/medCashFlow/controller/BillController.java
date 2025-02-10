package example.medCashFlow.controller;

import example.medCashFlow.dto.bill.BillOnlyResponseDTO;
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
import org.springframework.http.HttpStatus;
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

    @GetMapping("/{id}")
    public ResponseEntity<BillOnlyResponseDTO> getBillById(
            @AuthenticationPrincipal UserDetails loggedUser,
            @PathVariable Long id) {
        if (!(loggedUser instanceof Employee )) {
            throw new ForbiddenException();
        }

        BillOnlyResponseDTO bill = billService.getBillOnlyResponseDTO(id);
        return ResponseEntity.ok(bill);
    }

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
    public ResponseEntity<Void> createBill(
            @AuthenticationPrincipal UserDetails loggedUser,
            @RequestBody BillRegisterDTO data) {
        if (!(loggedUser instanceof Employee employee)) {
            throw new ForbiddenException();
        }

        Bill bill = billService.toBill(data, employee);
        billService.saveBill(bill);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BillResponseDTO> updateBill(
            @AuthenticationPrincipal UserDetails loggedUser,
            @PathVariable Long id,
            @RequestBody BillRegisterDTO data) {
        if (!(loggedUser instanceof Employee employee)) {
            throw new ForbiddenException();
        }

        Bill bill = billService.toBill(data, employee);

        billService.updateBill(bill, id);
        return ResponseEntity.noContent().build();
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

}
