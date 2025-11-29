package example.medCashFlow.controller;

import example.medCashFlow.dto.bill.BillOnlyResponseDTO;
import example.medCashFlow.dto.bill.BillRegisterDTO;
import example.medCashFlow.dto.bill.BillResponseDTO;
import example.medCashFlow.infra.security.SecurityService;
import example.medCashFlow.model.Bill;
import example.medCashFlow.services.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;
    private final SecurityService securityService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<BillOnlyResponseDTO> getBillById(@PathVariable Long id) {
        BillOnlyResponseDTO bill = billService.getBillOnlyResponseDTO(id);
        return ResponseEntity.ok(bill);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<BillResponseDTO>> listAllBills() {
        List<BillResponseDTO> bills = billService.getAllBillsByClinicId(securityService.getCurrentClinicId());
        return ResponseEntity.ok(bills);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> createBill(@Valid @RequestBody BillRegisterDTO data) {
        billService.createBill(data, securityService.getCurrentEmployee());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBill(@PathVariable Long id, @Valid @RequestBody BillRegisterDTO data) {
        billService.updateBill(data, id);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
        return ResponseEntity.noContent().build();
    }

}
