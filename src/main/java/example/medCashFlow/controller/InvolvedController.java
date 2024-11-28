package example.medCashFlow.controller;

import example.medCashFlow.dto.involved.InvolvedRegisterDTO;
import example.medCashFlow.dto.involved.InvolvedResponseDTO;
import example.medCashFlow.exceptions.ForbiddenException;
import example.medCashFlow.model.Employee;
import example.medCashFlow.model.Involved;
import example.medCashFlow.services.InvolvedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/involveds")
@RequiredArgsConstructor
public class InvolvedController {
    private final InvolvedService involvedService;

    @GetMapping("/list")
    public ResponseEntity<List<InvolvedResponseDTO>> listAllInvolveds(
            @AuthenticationPrincipal UserDetails loggedManager) {
        if (!(loggedManager instanceof Employee)) {
            throw new ForbiddenException();
        }

        List<InvolvedResponseDTO> involveds = involvedService.getAllInvolvedsByClinicId(
                ((Employee) loggedManager).getClinic().getId());
        return ResponseEntity.ok(involveds);
    }

    @PostMapping("/create")
    public ResponseEntity<InvolvedResponseDTO> createInvolved(
            @AuthenticationPrincipal UserDetails loggedManager,
            @RequestBody InvolvedRegisterDTO data) {
        if (!(loggedManager instanceof Employee manager)) {
            throw new ForbiddenException();
        }

        Involved newInvolved = new Involved();
        newInvolved.setName(data.name());
        newInvolved.setDocument(data.document());
        newInvolved.setPhone(data.phone());
        newInvolved.setEmail(data.email());
        newInvolved.setClinic(manager.getClinic());

        return ResponseEntity.ok(involvedService.saveInvolved(newInvolved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvolvedResponseDTO> updateInvolved(
            @AuthenticationPrincipal UserDetails loggedManager,
            @PathVariable Long id,
            @RequestBody InvolvedRegisterDTO data) {
        if (!(loggedManager instanceof Employee)) {
            throw new ForbiddenException();
        }

        Involved involvedToUpdate = new Involved();
        involvedToUpdate.setName(data.name());
        involvedToUpdate.setDocument(data.document());
        involvedToUpdate.setPhone(data.phone());
        involvedToUpdate.setEmail(data.email());

        return ResponseEntity.ok(involvedService.updateInvolved(involvedToUpdate, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvolved(
            @AuthenticationPrincipal UserDetails loggedManager,
            @PathVariable Long id) {
        if (!(loggedManager instanceof Employee)) {
            throw new ForbiddenException();
        }

        involvedService.deleteInvolved(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<Void> activateInvolved(
            @AuthenticationPrincipal UserDetails loggedManager,
            @PathVariable Long id) {
        if (!(loggedManager instanceof Employee)) {
            throw new ForbiddenException();
        }

        involvedService.activateInvolved(id);
        return ResponseEntity.noContent().build();
    }

}

