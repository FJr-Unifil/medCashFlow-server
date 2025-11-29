package example.medCashFlow.controller;

import example.medCashFlow.dto.bill.BillOnlyResponseDTO;
import example.medCashFlow.dto.bill.BillRegisterDTO;
import example.medCashFlow.dto.bill.BillResponseDTO;
import example.medCashFlow.model.Bill;
import example.medCashFlow.model.Employee;
import example.medCashFlow.services.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_FINANCIAL_ANALYST')")
public class BillController {

    private final BillService billService;

    @GetMapping("/{id}")
    public ResponseEntity<BillOnlyResponseDTO> getBillById(
            @AuthenticationPrincipal Employee loggedUser,
            @PathVariable Long id) {
        BillOnlyResponseDTO bill = billService.getBillOnlyResponseDTO(id);
        return ResponseEntity.ok(bill);
    }

    @GetMapping("/list")
    public ResponseEntity<List<BillResponseDTO>> listAllBills(
            @AuthenticationPrincipal Employee loggedUser) {
        List<BillResponseDTO> bills = billService.getAllBillsByClinicId(loggedUser.getClinic().getId());
        return ResponseEntity.ok(bills);
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createBill(
            @AuthenticationPrincipal Employee loggedUser,
            @RequestBody BillRegisterDTO data) {
        billService.createBill(data, loggedUser);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBill(
            @AuthenticationPrincipal Employee loggedUser,
            @PathVariable Long id,
            @RequestBody BillRegisterDTO data) {
        billService.updateBill(data, id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBill(
            @AuthenticationPrincipal Employee loggedUser,
            @PathVariable Long id) {
        billService.deleteBill(id);
        return ResponseEntity.noContent().build();
    }

}
