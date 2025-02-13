package example.medCashFlow.controller;

import example.medCashFlow.dto.accountPlanning.AccountPlanningRegisterDTO;
import example.medCashFlow.dto.accountPlanning.AccountPlanningResponseDTO;
import example.medCashFlow.exceptions.ForbiddenException;
import example.medCashFlow.model.AccountPlanning;
import example.medCashFlow.model.Employee;
import example.medCashFlow.services.AccountPlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account-plannings")
@RequiredArgsConstructor
public class AccountPlanningController {

    private final AccountPlanningService accountPlanningService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountPlanningResponseDTO> getAccountPlanningById(@AuthenticationPrincipal UserDetails loggedUser, @PathVariable("id") Long id) {
        if (!(loggedUser instanceof Employee)) {
            throw new ForbiddenException();
        }

        return ResponseEntity.ok(accountPlanningService.getAccountPlanningResponseDTOById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<AccountPlanningResponseDTO>> listAllAccountPlannings(
            @AuthenticationPrincipal UserDetails loggedUser) {
        if (!(loggedUser instanceof Employee employee)) {
            throw new ForbiddenException();
        }

        List<AccountPlanningResponseDTO> accountPlannings = accountPlanningService
                .getAllAccountPlanningsByClinicId(employee.getClinic().getId());
        return ResponseEntity.ok(accountPlannings);
    }

    @PostMapping("/create")
    public ResponseEntity<AccountPlanningResponseDTO> createAccountPlanning(
            @AuthenticationPrincipal UserDetails loggedUser,
            @RequestBody AccountPlanningRegisterDTO data) {

        if (!(loggedUser instanceof Employee employee)) {
            throw new ForbiddenException();
        }

        return ResponseEntity.ok(accountPlanningService.createAccountPlanning(data, employee.getClinic()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AccountPlanningResponseDTO> updateAccountPlanning(
            @AuthenticationPrincipal UserDetails loggedUser,
            @PathVariable Long id,
            @RequestBody AccountPlanningRegisterDTO data) {
        if (!(loggedUser instanceof Employee employee)) {
            throw new ForbiddenException();
        }

        return ResponseEntity.ok(accountPlanningService.updateAccountPlanning(data, employee.getClinic() ,id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAccountPlanning(
            @AuthenticationPrincipal UserDetails loggedUser,
            @PathVariable Long id) {
        if (!(loggedUser instanceof Employee)) {
            throw new ForbiddenException();
        }

        accountPlanningService.deleteAccountPlanning(id);
        return ResponseEntity.noContent().build();
    }
}
