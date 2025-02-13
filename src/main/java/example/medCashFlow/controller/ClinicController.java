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

    @GetMapping("/list")
    public ResponseEntity<List<ClinicResponseDTO>> getAllClinics() {
        return ResponseEntity.ok(service.getAllClinics());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteClinic(@PathVariable UUID id) {

        service.deactivateClinic(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<Void> activateClinic(@PathVariable UUID id) {
        service.activateClinic(id);

        return ResponseEntity.noContent().build();
    }

}
