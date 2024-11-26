package example.medCashFlow.services;

import example.medCashFlow.dto.clinic.ClinicRegisterDTO;
import example.medCashFlow.dto.clinic.ClinicResponseDTO;
import example.medCashFlow.exceptions.ClinicNotFoundException;
import example.medCashFlow.exceptions.InvalidClinicException;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.repository.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository repository;

    public Clinic getClinicById(UUID id) {
        return repository.findById(id).orElseThrow(ClinicNotFoundException::new);
    }

    public List<ClinicResponseDTO> getAllClinics() {
        return repository.findAllByOrderByCreatedAtAsc().stream()
                .map(clinic -> new ClinicResponseDTO(
                        clinic.getId(),
                        clinic.getName(),
                        clinic.getCnpj(),
                        clinic.getPhone(),
                        clinic.getCreatedAt(),
                        clinic.getIsActive()))
                .toList();
    }

    public Clinic saveClinic(ClinicRegisterDTO data) {
        if (!isClinicValid(data)) {
            throw new InvalidClinicException();
        }

        return repository.save(new Clinic(data));
    }

    public void saveClinic(Clinic data) {
        repository.save(data);
    }

    public boolean isClinicValid(ClinicRegisterDTO clinicData) {
        return isClinicValidByName(clinicData.name()) && isClinicValidByCnpj(clinicData.cnpj()) && isClinicValidByPhone(clinicData.phone());
    }

    public boolean isClinicValidById(UUID id) {
        if (repository.existsById(id)) {
            throw new ClinicNotFoundException();
        }

        return true;
    }

    public boolean isClinicValidByName(String name) {
        if (repository.existsByName(name)) {
            throw new InvalidClinicException("clinic.name");
        }

        return true;
    }

    public boolean isClinicValidByCnpj(String cnpj) {
        if (repository.existsByCnpj(cnpj)) {
            throw new InvalidClinicException("clinic.cnpj");
        }

        return true;
    }

    public boolean isClinicValidByPhone(String phone) {
        if (repository.existsByPhone(phone)) {
            throw new InvalidClinicException("clinic.phone");
        }

        return true;
    }
}
