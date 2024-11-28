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

    @GetMapping("/list")
    public ResponseEntity<List<AccountPlanningResponseDTO>> listAllAccountPlannings(
            @AuthenticationPrincipal UserDetails loggedManager) {
        if (!(loggedManager instanceof Employee)) {
            throw new ForbiddenException();
        }

        List<AccountPlanningResponseDTO> accountPlannings = accountPlanningService
                .getAllAccountPlanningsByClinicId(((Employee) loggedManager).getClinic().getId());
        return ResponseEntity.ok(accountPlannings);
    }

    @PostMapping("/create")
    public ResponseEntity<AccountPlanningResponseDTO> createAccountPlanning(
            @AuthenticationPrincipal UserDetails loggedManager,
            @RequestBody AccountPlanningRegisterDTO data) {

        if (!(loggedManager instanceof Employee manager)) {
            throw new ForbiddenException();
        }

        AccountPlanning newAccountPlanning = new AccountPlanning();
        newAccountPlanning.setName(data.name());
        newAccountPlanning.setDescription(data.description());
        newAccountPlanning.setEmoji(data.emoji());
        newAccountPlanning.setColor(data.color());
        newAccountPlanning.setClinic(manager.getClinic());

        return ResponseEntity.ok(accountPlanningService.saveAccountPlanning(newAccountPlanning));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AccountPlanningResponseDTO> updateAccountPlanning(
            @AuthenticationPrincipal UserDetails loggedManager,
            @PathVariable Long id,
            @RequestBody AccountPlanningRegisterDTO data) {
        if (!(loggedManager instanceof Employee manager)) {
            throw new ForbiddenException();
        }

        AccountPlanning accountPlanningToUpdate = new AccountPlanning();
        accountPlanningToUpdate.setName(data.name());
        accountPlanningToUpdate.setDescription(data.description());
        accountPlanningToUpdate.setEmoji(data.emoji());
        accountPlanningToUpdate.setColor(data.color());
        accountPlanningToUpdate.setClinic(manager.getClinic());

        return ResponseEntity.ok(accountPlanningService.updateAccountPlanning(accountPlanningToUpdate, id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAccountPlanning(
            @AuthenticationPrincipal UserDetails loggedManager,
            @PathVariable Long id) {
        if (!(loggedManager instanceof Employee)) {
            throw new ForbiddenException();
        }

        accountPlanningService.deleteAccountPlanning(id);
        return ResponseEntity.noContent().build();
    }
}
