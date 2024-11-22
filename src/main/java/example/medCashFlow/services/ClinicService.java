package example.medCashFlow.services;

import example.medCashFlow.dto.ClinicRegisterDTO;
import example.medCashFlow.exceptions.ClinicNotFoundException;
import example.medCashFlow.exceptions.InvalidClinicException;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.repository.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository repository;

    public Clinic getClinicById(UUID id) {
        return repository.findById(id).orElseThrow(ClinicNotFoundException::new);
    }

    public Clinic saveClinic(ClinicRegisterDTO data) {
            return repository.save(new Clinic(data));
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
