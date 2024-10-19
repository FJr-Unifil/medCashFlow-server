package example.medCashFlow.services;

import example.medCashFlow.dto.ClinicRegisterDTO;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.repository.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository repository;

    public Clinic getClinicById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Clinic saveClinic(Clinic clinic) {
        return repository.save(clinic);
    }

    public boolean isClinicValid(ClinicRegisterDTO clinicData) {
        return isClinicValidByName(clinicData.name()) && isClinicValidByCnpj(clinicData.cnpj()) && isClinicValidByPhone(clinicData.phone());
    }

    public boolean isClinicValidById(Long id) {
        return repository.existsByIdAndIsActiveTrue(id);
    }

    public boolean isClinicValidByName(String name) {
        return !repository.existsByNameAndIsActiveTrue(name);
    }

    public boolean isClinicValidByCnpj(String cnpj) {
        return !repository.existsByCnpjAndIsActiveTrue(cnpj);
    }

    public boolean isClinicValidByPhone(String phone) {
        return !repository.existsByPhoneAndIsActiveTrue(phone);
    }
}
