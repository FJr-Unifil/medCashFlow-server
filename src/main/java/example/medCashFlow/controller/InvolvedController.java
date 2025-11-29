package example.medCashFlow.controller;

import example.medCashFlow.dto.involved.InvolvedRegisterDTO;
import example.medCashFlow.dto.involved.InvolvedResponseDTO;
import example.medCashFlow.model.Employee;
import example.medCashFlow.model.Involved;
import example.medCashFlow.services.InvolvedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/involveds")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_FINANCIAL_ANALYST')")
public class InvolvedController {

    private final InvolvedService involvedService;

    @GetMapping("/{id}")
    public ResponseEntity<InvolvedResponseDTO> getInvolvedById(@AuthenticationPrincipal Employee loggedUser, @PathVariable("id") Long id) {
        return ResponseEntity.ok(involvedService.getInvolvedResponseDTOById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<InvolvedResponseDTO>> listAllInvolveds(
            @AuthenticationPrincipal Employee loggedUser) {
        List<InvolvedResponseDTO> involveds = involvedService.getAllInvolvedsByClinicId(
                loggedUser.getClinic().getId());
        return ResponseEntity.ok(involveds);
    }

    @PostMapping("/create")
    public ResponseEntity<InvolvedResponseDTO> createInvolved(
            @AuthenticationPrincipal Employee loggedUser,
            @RequestBody InvolvedRegisterDTO data) {
        return ResponseEntity.ok(involvedService.createInvolved(data, loggedUser.getClinic()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvolvedResponseDTO> updateInvolved(
            @AuthenticationPrincipal Employee loggedUser,
            @PathVariable Long id,
            @RequestBody InvolvedRegisterDTO data) {
        return ResponseEntity.ok(involvedService.updateInvolved(data, loggedUser.getClinic(), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvolved(
            @AuthenticationPrincipal Employee loggedUser,
            @PathVariable Long id) {
        involvedService.deleteInvolved(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<Void> activateInvolved(
            @AuthenticationPrincipal Employee loggedUser,
            @PathVariable Long id) {
        involvedService.activateInvolved(id);
        return ResponseEntity.noContent().build();
    }

}

