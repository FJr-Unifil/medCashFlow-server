package example.medCashFlow.controller;

import example.medCashFlow.dto.accountPlanning.AccountPlanningRegisterDTO;
import example.medCashFlow.dto.accountPlanning.AccountPlanningResponseDTO;
import example.medCashFlow.model.AccountPlanning;
import example.medCashFlow.model.Employee;
import example.medCashFlow.services.AccountPlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account-plannings")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_FINANCIAL_ANALYST')")
public class AccountPlanningController {

    private final AccountPlanningService accountPlanningService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountPlanningResponseDTO> getAccountPlanningById(@AuthenticationPrincipal Employee loggedUser, @PathVariable("id") Long id) {
        return ResponseEntity.ok(accountPlanningService.getAccountPlanningResponseDTOById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<AccountPlanningResponseDTO>> listAllAccountPlannings(
            @AuthenticationPrincipal Employee loggedUser) {
        List<AccountPlanningResponseDTO> accountPlannings = accountPlanningService
                .getAllAccountPlanningsByClinicId(loggedUser.getClinic().getId());
        return ResponseEntity.ok(accountPlannings);
    }

    @PostMapping("/create")
    public ResponseEntity<AccountPlanningResponseDTO> createAccountPlanning(
            @AuthenticationPrincipal Employee loggedUser,
            @RequestBody AccountPlanningRegisterDTO data) {
        return ResponseEntity.ok(accountPlanningService.createAccountPlanning(data, loggedUser.getClinic()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AccountPlanningResponseDTO> updateAccountPlanning(
            @AuthenticationPrincipal Employee loggedUser,
            @PathVariable Long id,
            @RequestBody AccountPlanningRegisterDTO data) {
        return ResponseEntity.ok(accountPlanningService.updateAccountPlanning(data, loggedUser.getClinic() ,id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAccountPlanning(
            @AuthenticationPrincipal Employee loggedUser,
            @PathVariable Long id) {
        accountPlanningService.deleteAccountPlanning(id);
        return ResponseEntity.noContent().build();
    }
}
