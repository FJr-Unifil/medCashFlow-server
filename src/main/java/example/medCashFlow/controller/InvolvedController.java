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

    @GetMapping("/{id}")
    public ResponseEntity<InvolvedResponseDTO> getInvolvedById(@AuthenticationPrincipal UserDetails loggedUser, @PathVariable("id") Long id) {
        if (!(loggedUser instanceof Employee)) {
            throw new ForbiddenException();
        }

        Involved involved = involvedService.getInvolvedById(id);

        return ResponseEntity.ok(involvedService.toResponseDTO(involved));
    }

    @GetMapping("/list")
    public ResponseEntity<List<InvolvedResponseDTO>> listAllInvolveds(
            @AuthenticationPrincipal UserDetails loggedUser) {
        if (!(loggedUser instanceof Employee)) {
            throw new ForbiddenException();
        }

        List<InvolvedResponseDTO> involveds = involvedService.getAllInvolvedsByClinicId(
                ((Employee) loggedUser).getClinic().getId());
        return ResponseEntity.ok(involveds);
    }

    @PostMapping("/create")
    public ResponseEntity<InvolvedResponseDTO> createInvolved(
            @AuthenticationPrincipal UserDetails loggedUser,
            @RequestBody InvolvedRegisterDTO data) {
        if (!(loggedUser instanceof Employee employee)) {
            throw new ForbiddenException();
        }

        Involved newInvolved = involvedService.toInvolved(data);
        newInvolved.setClinic(employee.getClinic());

        return ResponseEntity.ok(involvedService.saveInvolved(newInvolved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvolvedResponseDTO> updateInvolved(
            @AuthenticationPrincipal UserDetails loggedUser,
            @PathVariable Long id,
            @RequestBody InvolvedRegisterDTO data) {
        if (!(loggedUser instanceof Employee)) {
            throw new ForbiddenException();
        }

        Involved involvedToUpdate = involvedService.toInvolved(data);
        involvedToUpdate.setId(id);

        return ResponseEntity.ok(involvedService.updateInvolved(involvedToUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvolved(
            @AuthenticationPrincipal UserDetails loggedUser,
            @PathVariable Long id) {
        if (!(loggedUser instanceof Employee)) {
            throw new ForbiddenException();
        }

        involvedService.deleteInvolved(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<Void> activateInvolved(
            @AuthenticationPrincipal UserDetails loggedUser,
            @PathVariable Long id) {
        if (!(loggedUser instanceof Employee)) {
            throw new ForbiddenException();
        }

        involvedService.activateInvolved(id);
        return ResponseEntity.noContent().build();
    }

}

