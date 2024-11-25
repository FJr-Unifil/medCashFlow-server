package example.medCashFlow.controller;

import example.medCashFlow.dto.clinic.ClinicResponseDTO;
import example.medCashFlow.exceptions.ClinicNotFoundException;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.services.ClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clinics")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicService service;

    @GetMapping
    public ResponseEntity<List<ClinicResponseDTO>> getAllClinics() {
        return ResponseEntity.ok(service.getAllClinics());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteClinic(@PathVariable UUID id) {
        Clinic clinic = service.getClinicById(id);

        if (!clinic.getIsActive()) {
            throw new ClinicNotFoundException("Clínica já estava inativa");
        }

        clinic.setIsActive(false);
        service.saveClinic(clinic);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<Void> activateClinic(@PathVariable UUID id) {
        Clinic clinic = service.getClinicById(id);

        clinic.setIsActive(true);
        service.saveClinic(clinic);

        return ResponseEntity.noContent().build();
    }

}
